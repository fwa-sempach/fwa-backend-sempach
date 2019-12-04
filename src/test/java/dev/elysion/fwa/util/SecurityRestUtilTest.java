package dev.elysion.fwa.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SecurityRestUtilTest {

	@Test
	public void generateRandomPassword_salt_checkAgainstHash_true() {
		String randomPassword = SecurityUtil.generateRandomPassword(12);
		String salt = SecurityUtil.getNextSalt();
		String hashedPassword = SecurityUtil.hash(randomPassword, salt);
		boolean passwordCorrect = SecurityUtil.isExpectedPassword(randomPassword, salt, hashedPassword);
		assertTrue(passwordCorrect);
	}

	@Test
	public void generateRandomPassword_modify_salt_checkAgainstHash_false() {
		String randomPassword = SecurityUtil.generateRandomPassword(12);
		String salt = SecurityUtil.getNextSalt();
		String hashedPassword = SecurityUtil.hash(randomPassword, salt);
		boolean passwordCorrect = SecurityUtil.isExpectedPassword("unRandomPass", salt, hashedPassword);
		assertTrue(!passwordCorrect);
	}

	@Test
	public void generatePasswordHash() {
		String password = "test";
		String salt = SecurityUtil.getNextSalt();
		String hashedPassword = SecurityUtil.hash(password, salt);

		System.out.println("salt: " + salt);
		System.out.println("hash: " + hashedPassword);

		assertTrue(SecurityUtil.isExpectedPassword(password, salt, hashedPassword));
	}

	@Test
	public void getSalt_getNextSalt_compare_notEqual() {
		String salt = SecurityUtil.getNextSalt();
		String salt2 = SecurityUtil.getNextSalt();
		assertTrue(!salt.equals(salt2));
	}

	@Test
	public void generatePassword_generateNextPassword_compare_notEqual() {
		String pass = SecurityUtil.generateRandomPassword(12);
		assertEquals(12, pass.length());
		String pass2 = SecurityUtil.generateRandomPassword(12);
		assertEquals(12, pass2.length());
		assertTrue(!pass.equals(pass2));
	}
}