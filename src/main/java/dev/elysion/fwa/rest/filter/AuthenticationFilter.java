package dev.elysion.fwa.rest.filter;

import dev.elysion.fwa.dto.Organisation;
import dev.elysion.fwa.dto.User;
import dev.elysion.fwa.rest.auth.AuthenticatedUser;
import dev.elysion.fwa.rest.auth.AuthenticationToken;
import dev.elysion.fwa.rest.auth.TokenBasedSecurityContext;
import dev.elysion.fwa.services.OrganisationService;
import dev.elysion.fwa.services.UserService;
import dev.elysion.fwa.util.SecurityUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String REALM = "fwas";
	private static final String AUTHENTICATION_SCHEME = "Bearer ";

	@Inject
	private UserService userService;

	@Inject
	private OrganisationService organisationService;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith(AUTHENTICATION_SCHEME)) {
			String authenticationToken = authorizationHeader.substring(AUTHENTICATION_SCHEME.length())
															.trim();
			try {
				handleTokenBasedAuthentication(authenticationToken, requestContext);
			}
			catch (Exception e) {
				LOGGER.error("authentication failed", e);
				abortWithUnauthorized(requestContext);
			}
			return;
		}

		// Other authentication schemes (such as Basic) could be supported
	}

	private void handleTokenBasedAuthentication(String token, ContainerRequestContext requestContext)
			throws UnsupportedEncodingException {

		AuthenticationToken authenticationToken = SecurityUtil.verifyToken(token);
		User user = userService.readByUsername(authenticationToken.getUsername());
		Organisation organisation = organisationService.readOrganisationByUserId(user.getId());
		AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getUsername(), organisation != null ?
				organisation.getId() : 0, user.getRoles());

		boolean isSecure = requestContext.getSecurityContext()
										 .isSecure();
		SecurityContext securityContext = new TokenBasedSecurityContext(authenticatedUser, authenticationToken,
				isSecure);
		requestContext.setSecurityContext(securityContext);
	}

	private void abortWithUnauthorized(ContainerRequestContext requestContext) {
		// Abort the filter chain with a 401 status code response
		// The WWW-Authenticate header is sent along with the response
		requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
										 .header(HttpHeaders.WWW_AUTHENTICATE,
												 AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
										 .build());
	}

}
