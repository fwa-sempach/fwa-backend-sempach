package dev.elysion.fwa.services;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.dao.OrganisationDao;
import dev.elysion.fwa.dao.UserDao;
import dev.elysion.fwa.dao.UserRoleDao;
import dev.elysion.fwa.dto.User;
import dev.elysion.fwa.entity.OrganisationEntity;
import dev.elysion.fwa.entity.UserEntity;
import dev.elysion.fwa.entity.UserRoleEntity;
import dev.elysion.fwa.enumeration.Role;
import dev.elysion.fwa.rest.auth.AuthenticatedUser;
import dev.elysion.fwa.rest.auth.AuthenticationToken;
import dev.elysion.fwa.util.SecurityUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.SecurityContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;


@RequestScoped
public class SecurityService {

	private static final Logger LOGGER = LogManager.getLogger();

	private static final int INITIAL_PASSWORD_LENGTH = 10;
	private UserDao userDao;
	private UserRoleDao userRoleDao;
	private OrganisationDao organisationDao;
	private MailService mailService;

	protected SecurityService() {
		// weld proxy
	}

	@Inject
	public SecurityService(UserDao userDao, UserRoleDao userRoleDao, OrganisationDao organisationDao,
						   MailService mailService) {
		this.userDao = userDao;
		this.userRoleDao = userRoleDao;
		this.organisationDao = organisationDao;
		this.mailService = mailService;
	}

	public User createNewUser(String username, String email) throws IOException {
		return createNewUser(username, SecurityUtil.generateRandomPassword(INITIAL_PASSWORD_LENGTH), email);
	}

	public User createNewUser(String username, String password, String email) throws IOException {
		User user = new User();
		user.setUsername(username.toLowerCase());
		user.setEmail(email);
		user.setDeleted(false);
		user.setVerified(false);

		String salt = SecurityUtil.getNextSalt();
		String hashedPassword = SecurityUtil.hash(password, salt);

		user.setPassword(hashedPassword);
		user.setSalt(salt);

		String token = SecurityUtil.issueVerificationToken(user.getUsername(), user.getEmail());
		User newUser = Converter.convert(userDao.merge(Converter.convert(user)));

		mailService.sendEmailVerification(user.getEmail(), token);
		return newUser;
	}

	public void verifyEmailAddress(String token) throws UnsupportedEncodingException {
		AuthenticationToken t = SecurityUtil.verifyToken(token);
		User user = Converter.convert(userDao.readByUsername(t.getUsername()));
		user.setVerified(true);

		// FIXME: role is inserted twice, when user hits activation link twice
		UserRoleEntity userRole = new UserRoleEntity();
		userRole.setUserId(user.getId());
		userRole.setRole(Role.ORG);
		userRoleDao.merge(userRole);

		userDao.merge(Converter.convert(user));
	}

	public String authenticate(String token) throws UnsupportedEncodingException {
		AuthenticationToken t = SecurityUtil.verifyToken(token);
		return generateAuthToken(t.getUsername());
	}

	public User authenticate(String username, String password) throws NotAuthorizedException {
		User user = Converter.convert(userDao.readByUsername(username.toLowerCase()));

		if (user == null) {
			LOGGER.info("user not found: {}", username);
			throw new NotAuthorizedException("user not found");
		}
		else if (!user.isVerified()) {
			LOGGER.info("user not verified: {}", username);
			throw new NotAuthorizedException("user not found");
		}
		else {
			if (!SecurityUtil.isExpectedPassword(password, user.getSalt(), user.getPassword())) {
				LOGGER.info("wrong password for: {}", username);
				throw new NotAuthorizedException("wrong password");
			}
		}

		LOGGER.info("authentication succesful for: {}", username);

		return user;
	}

	public String generateAuthToken(String username) throws UnsupportedEncodingException {
		User user = Converter.convert(userDao.readByUsername(username));
		OrganisationEntity organisationEntity = organisationDao.readByUserId(user.getId());

		Integer organisationId = organisationEntity != null ? organisationEntity.getId() : null;

		return SecurityUtil.issueToken(user.getUsername(), organisationId, user.getRoles());
	}

	public void verifyOrganisation(SecurityContext securityContext, int organisationId) throws ForbiddenException {
		AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getUserPrincipal();

		if (authenticatedUser.getOrganisationId() != organisationId && !authenticatedUser.getRoles()
																						 .contains(Role.ADMIN)) {
			ForbiddenException e = new ForbiddenException("access to organisation denied");
			LOGGER.error("Unauthorized Access for {} from {}", e, organisationId, securityContext.getUserPrincipal());
			throw e;
		}
	}

	public void verifyUser(SecurityContext securityContext, String username) throws ForbiddenException {
		AuthenticatedUser authenticatedUser = (AuthenticatedUser) securityContext.getUserPrincipal();

		if (!authenticatedUser.getName()
							  .equalsIgnoreCase(username) && !authenticatedUser.getRoles()
																			   .contains(Role.ADMIN)) {
			ForbiddenException e = new ForbiddenException("access to user denied");
			LOGGER.error("Unauthorized Access for {} from {}", e, username, securityContext.getUserPrincipal());
			throw e;
		}
	}

	public void updatePassword(String username, String password) throws IOException {
		UserEntity user = userDao.readByUsername(username);

		String salt = SecurityUtil.getNextSalt();
		String hashedPassword = SecurityUtil.hash(password, salt);

		user.setPassword(hashedPassword);
		user.setSalt(salt);

		mailService.sendPasswordChanged(Converter.convert(user));

		userDao.merge(user);
	}

	public void updateEmail(String username, String email) throws IOException {
		UserEntity user = userDao.readByUsername(username);

		user.setEmail(email);

		mailService.sendEmailChanged(Converter.convert(user));

		userDao.merge(user);
	}

	public void sendResetPasswordEmail(String username) throws IOException {


		UserEntity user = userDao.readByUsername(username);
		if (user == null) {
			throw new BadRequestException("username does not exist");
		}

		String token = generateAuthToken(user.getUsername());

		mailService.sendPasswordReset(user, token);
	}
}
