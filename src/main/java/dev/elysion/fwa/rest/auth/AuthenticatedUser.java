package dev.elysion.fwa.rest.auth;

import dev.elysion.fwa.enumeration.Role;

import java.security.Principal;
import java.util.List;

public class AuthenticatedUser implements Principal {

	private String username;
	private int organisationId;
	private List<Role> roles;

	public AuthenticatedUser(String username, int organisationId, List<Role> roles) {
		this.username = username;
		this.organisationId = organisationId;
		this.roles = roles;
	}

	@Override
	public String getName() {
		return username;
	}

	public int getOrganisationId() {
		return organisationId;
	}

	public List<Role> getRoles() {
		return roles;
	}

	@Override
	public String toString() {
		return "AuthenticatedUser{" + "username='" + username + '\'' + ", organisationId=" + organisationId + ", " +
				"roles=" + roles + '}';
	}
}
