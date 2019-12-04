package dev.elysion.fwa.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.enumeration.Role;
import dev.elysion.fwa.rest.auth.AuthenticationToken;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.ws.rs.NotAuthorizedException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * A utility class to hash passwords and check passwords vs hashed values. It uses a combination of hashing and unique
 * salt. The algorithm used is PBKDF2WithHmacSHA1 which, although not the best for hashing password (vs. bcrypt) is
 * still considered robust and <a href="https://security.stackexchange.com/a/6415/12614"> recommended by NIST </a>.
 * The hashed value has 256 bits.
 * Modified version to work with strings of:
 * https://stackoverflow.com/questions/18142745/how-do-i-generate-a-salt-in-java-for-salted-hash
 */
public class SecurityUtil {

	private static final Random RANDOM = new SecureRandom();
	private static final int ITERATIONS = 10000;
	private static final int KEY_LENGTH = 256;
	private static final String TOKEN_SECRET =
			"JqDaFufphKEEdKrtmNTh813ovkSsGuDzIoXPC5lyInv5hO28WSQVuD2M5a8dn0iPajku8Jih7frHO7m2BDX40H8ZkRia3MYuLQ3y";
	private static final String TOKEN_ISSUER = "fwas-backend";

	private static final String CLAIM_ORG_ID = "orgId";
	private static final String CLAIM_ROLE_CODES = "roleCodes";

	private SecurityUtil() {
		// static utility class
	}

	/**
	 * Returns a random salt to be used to hash a password.
	 *
	 * @return a 16 bytes random salt
	 */
	public static String getNextSalt() {
		byte[] salt = new byte[16];
		RANDOM.nextBytes(salt);
		return Base64.encodeBase64String(salt);
	}

	/**
	 * Returns a salted and hashed password using the provided hash.<br>
	 * Note - side effect: the password is destroyed (the char[] is filled with zeros)
	 *
	 * @param password the password to be hashed
	 * @param salt     a 16 bytes salt, ideally obtained with the getNextSalt method
	 * @return the hashed password with a pinch of salt
	 */
	public static String hash(String password, String salt) {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), Base64.decodeBase64(salt), ITERATIONS, KEY_LENGTH);
		Arrays.fill(password.toCharArray(), Character.MIN_VALUE);
		try {
			SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			return Base64.encodeBase64String(skf.generateSecret(spec)
												.getEncoded());
		}
		catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
		}
		finally {
			spec.clearPassword();
		}
	}

	/**
	 * Returns true if the given password and salt match the hashed value, false otherwise.<br>
	 * Note - side effect: the password is destroyed (the char[] is filled with zeros)
	 *
	 * @param password     the password to check
	 * @param salt         the salt used to hash the password
	 * @param expectedHash the expected hashed value of the password
	 * @return true if the given password and salt match the hashed value, false otherwise
	 */
	public static boolean isExpectedPassword(String password, String salt, String expectedHash) {
		byte[] pwdHash = Base64.decodeBase64(hash(password, salt));
		byte[] expHash = Base64.decodeBase64(expectedHash);
		Arrays.fill(password.toCharArray(), Character.MIN_VALUE);
		if (pwdHash.length != expHash.length) {
			return false;
		}
		for (int i = 0; i < pwdHash.length; i++) {
			if (pwdHash[i] != expHash[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Generates a random password of a given length, using letters and digits.
	 *
	 * @param length the length of the password
	 * @return a random password
	 */
	public static String generateRandomPassword(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int c = RANDOM.nextInt(62);
			if (c <= 9) {
				sb.append(String.valueOf(c));
			}
			else if (c < 36) {
				sb.append((char) ('a' + c - 10));
			}
			else {
				sb.append((char) ('A' + c - 36));
			}
		}
		return sb.toString();
	}

	public static String issueVerificationToken(String username, String email) throws UnsupportedEncodingException {
		LocalDateTime expiry = LocalDateTime.now()
											.plusDays(1);

		Algorithm algorithm = Algorithm.HMAC512(TOKEN_SECRET);
		return JWT.create()
				  .withSubject(username)
				  .withClaim("email", email)
				  .withExpiresAt(Converter.convertToDate(expiry))
				  .withIssuedAt(Converter.convertToDate(LocalDateTime.now()))
				  .withIssuer(TOKEN_ISSUER)
				  .sign(algorithm);
	}

	public static String issueToken(String username, Integer organisationId, List<Role> roleCodes)
			throws UnsupportedEncodingException {
		LocalDateTime expiry = LocalDateTime.now()
											.plusDays(1);

		Algorithm algorithm = Algorithm.HMAC512(TOKEN_SECRET);
		return JWT.create()
				  .withSubject(username)
				  .withExpiresAt(Converter.convertToDate(expiry))
				  .withIssuedAt(Converter.convertToDate(LocalDateTime.now()))
				  .withIssuer(TOKEN_ISSUER)
				  .withJWTId(UUID.randomUUID()
								 .toString())
				  .withClaim(CLAIM_ORG_ID, organisationId)
				  .withArrayClaim(CLAIM_ROLE_CODES, roleCodes.stream()
															 .map(Role::toString)
															 .toArray(String[]::new))
				  .sign(algorithm);
	}

	public static AuthenticationToken verifyToken(String token) throws UnsupportedEncodingException {
		Algorithm algorithm = Algorithm.HMAC512(TOKEN_SECRET);
		// TODO: Reusable verifier instance -> put somewhere else
		JWTVerifier verifier = JWT.require(algorithm)
								  .withIssuer(TOKEN_ISSUER)
								  .build();
		DecodedJWT jwt = verifier.verify(token);

		LocalDateTime now = LocalDateTime.now();

		if (Converter.convertToLocalDateTime(jwt.getExpiresAt())
					 .isBefore(now) || Converter.convertToLocalDateTime(jwt.getIssuedAt())
												.isAfter(now)) {
			throw new NotAuthorizedException("token is expired");
		}

		return new AuthenticationToken.Builder().withId(jwt.getId())
												.withUsername(jwt.getSubject())
												.withIssuedDate(ZonedDateTime.ofInstant(jwt.getIssuedAt()
																						   .toInstant(),
														ZoneId.systemDefault()))
												.withExpirationDate(ZonedDateTime.ofInstant(jwt.getExpiresAt()
																							   .toInstant(),
														ZoneId.systemDefault()))
												.build();
	}
}
