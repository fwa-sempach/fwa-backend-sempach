package dev.elysion.fwa.services;

import dev.elysion.fwa.converter.Converter;
import dev.elysion.fwa.dao.UserDao;
import dev.elysion.fwa.dto.User;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class UserService {

	private UserDao userDao;

	protected UserService() {
		// CDI Proxy
	}

	@Inject
	public UserService(UserDao userDao) {
		this.userDao = userDao;
	}

	public User readByUsername(String username) {
		return Converter.convert(userDao.readByUsername(username));
	}
}
