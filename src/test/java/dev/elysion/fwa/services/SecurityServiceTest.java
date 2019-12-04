package dev.elysion.fwa.services;

import dev.elysion.fwa.dao.OrganisationDao;
import dev.elysion.fwa.dao.UserDao;
import dev.elysion.fwa.dao.UserRoleDao;
import dev.elysion.fwa.dto.User;
import dev.elysion.fwa.entity.UserEntity;
import dev.elysion.fwa.util.SecurityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.NotAuthorizedException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SecurityServiceTest {

	private static UserDao mockedUserDao;
	private static UserRoleDao mockedUserRoleDao;
	private static OrganisationDao mockedOrganisationDao;
	private static MailService mockedMailService;

	@BeforeAll
	public static void init() {
		mockedUserDao = mock(UserDao.class);
		mockedUserRoleDao = mock(UserRoleDao.class);
		mockedOrganisationDao = mock(OrganisationDao.class);
		mockedMailService = mock(MailService.class);
	}

	@Test
	public void createNewUser() throws IOException {
		final int userId = 1;

		when(mockedUserDao.merge(any(UserEntity.class))).thenAnswer(answer -> {
			UserEntity user = answer.getArgument(0);
			user.setId(userId);
			return user;
		});

		String username = "admin";
		String email = "mrvonwyl@gmail.com";
		SecurityService testee = new SecurityService(mockedUserDao, mockedUserRoleDao, mockedOrganisationDao,
				mockedMailService);
		User user = testee.createNewUser(username, email);

		assertEquals(userId, user.getId());
		assertEquals(username, user.getUsername());
		assertEquals(false, user.isDeleted());
		assertEquals(email, user.getEmail());
		assertTrue(user.getPassword() != null);
		assertTrue(user.getSalt() != null);
		assertTrue(user.getToken() == null);
	}

	@Test
	public void authenticate_userNotFound_exception() {
		SecurityService testee = new SecurityService(mockedUserDao, mockedUserRoleDao, mockedOrganisationDao,
				mockedMailService);

		doReturn(null).when(mockedUserDao)
					  .readByUsername(any(String.class));


		assertThrows(NotAuthorizedException.class, () -> testee.authenticate("admin", "pass"), "HTTP 401 " +
				"Unauthorized");
	}

	@Test
	public void authenticate_wrongPassword_exception() {
		SecurityService testee = new SecurityService(mockedUserDao, mockedUserRoleDao, mockedOrganisationDao,
				mockedMailService);

		doReturn(generateUser()).when(mockedUserDao)
								.readByUsername(any(String.class));

		assertThrows(NotAuthorizedException.class, () -> testee.authenticate("admin", "pass2"), "HTTP 401 " +
				"Unauthorized");
	}

	@Test
	public void authenticate_success() {
		SecurityService testee = new SecurityService(mockedUserDao, mockedUserRoleDao, mockedOrganisationDao,
				mockedMailService);

		doReturn(generateUser()).when(mockedUserDao)
								.readByUsername(any(String.class));

		testee.authenticate("admin", "pass");
	}

	private UserEntity generateUser() {
		UserEntity user = new UserEntity();

		user.setId(1);
		user.setUsername("admin");
		user.setSalt(SecurityUtil.getNextSalt());
		user.setPassword(SecurityUtil.hash("pass", user.getSalt()));
		user.setDeleted(false);
		user.setVerified(true);

		return user;
	}
}