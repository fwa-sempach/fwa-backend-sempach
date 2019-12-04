package dev.elysion.fwa.rest.auth;

import dev.elysion.fwa.enumeration.Role;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class TokenBasedSecurityContext implements SecurityContext {

	private AuthenticatedUser authenticatedUser;
	private AuthenticationToken authenticationToken;
	private final boolean secure;

	public TokenBasedSecurityContext(AuthenticatedUser authenticatedUser, AuthenticationToken authenticationToken,
									 boolean secure) {
		this.authenticatedUser = authenticatedUser;
		this.authenticationToken = authenticationToken;
		this.secure = secure;
	}

	@Override
	public Principal getUserPrincipal() {
		return authenticatedUser;
	}

	@Override
	public boolean isUserInRole(String s) {
		return authenticatedUser.getRoles()
								.contains(Role.valueOf(s));
	}

	@Override
	public boolean isSecure() {
		return secure;
	}

	@Override
	public String getAuthenticationScheme() {
		return "Bearer";
	}

	public AuthenticationToken getAuthenticationToken() {
		return this.authenticationToken;
	}
}
