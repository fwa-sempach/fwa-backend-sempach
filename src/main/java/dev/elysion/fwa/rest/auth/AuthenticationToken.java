package dev.elysion.fwa.rest.auth;

import dev.elysion.fwa.enumeration.Role;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AuthenticationToken {

	private final String id;
	private final String username;
	private final List<Role> roles;
	private final ZonedDateTime issuedDate;
	private final ZonedDateTime expirationDate;

	private AuthenticationToken(String id, String username, List<Role> roles, ZonedDateTime issuedDate,
								ZonedDateTime expirationDate) {
		this.id = id;
		this.username = username;
		this.roles = roles;
		this.issuedDate = issuedDate;
		this.expirationDate = expirationDate;
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public ZonedDateTime getIssuedDate() {
		return issuedDate;
	}

	public ZonedDateTime getExpirationDate() {
		return expirationDate;
	}

	public static class Builder {

		private String id;
		private String username;
		private List<Role> roles;
		private ZonedDateTime issuedDate;
		private ZonedDateTime expirationDate;

		public Builder withId(String id) {
			this.id = id;
			return this;
		}

		public Builder withUsername(String username) {
			this.username = username;
			return this;
		}

		public Builder withRoles(List<Role> authorities) {
			this.roles = Collections.unmodifiableList(authorities == null ? new ArrayList<>() : authorities);
			return this;
		}

		public Builder withIssuedDate(ZonedDateTime issuedDate) {
			this.issuedDate = issuedDate;
			return this;
		}

		public Builder withExpirationDate(ZonedDateTime expirationDate) {
			this.expirationDate = expirationDate;
			return this;
		}

		public AuthenticationToken build() {
			return new AuthenticationToken(id, username, roles, issuedDate, expirationDate);
		}
	}
}
