package dev.elysion.fwa.rest.auth;

import dev.elysion.fwa.dto.User;
import dev.elysion.fwa.rest.filter.Secured;
import dev.elysion.fwa.services.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/auth")
@RequestScoped
public class AuthResource {

	private static final Logger LOGGER = LogManager.getLogger();

	private SecurityService securityService;

	protected AuthResource() {
		//Benötigt für Proxy
	}

	@Inject
	public AuthResource(SecurityService securityService) {
		this.securityService = securityService;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response authenticateUser(User user) {
		try {
			// Authenticate the user using the credentials provided
			User authenticatedUser = securityService.authenticate(user.getUsername(), user.getPassword());

			// Issue a token for the user
			String token = securityService.generateAuthToken(user.getUsername());

			// Return the token on the response
			return Response.ok("{\"token\": \"" + token + "\"}")
						   .build();
		}
		catch (Exception e) {
			LOGGER.warn("authentication failed", e);
			return Response.status(Response.Status.FORBIDDEN)
						   .build();
		}
	}

	// FIXME this secured does not seem to work!?
	@POST
	@Secured
	@Path("/renew")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response renewToken(User user) {
		try {
			String newToken = securityService.authenticate(user.getToken());

			// Return the token on the response
			return Response.ok("{\"token\": \"" + newToken + "\"}")
						   .build();
		}
		catch (Exception e) {
			LOGGER.warn("authentication failed", e);
			return Response.status(Response.Status.FORBIDDEN)
						   .build();
		}
	}

	@POST
	@Path("/reset")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response resetPasswordEmail(User user) {
		try {
			securityService.sendResetPasswordEmail(user.getUsername());
			// Return the token on the response
			return Response.ok()
						   .build();
		}
		catch (Exception e) {
			LOGGER.warn("reset send mail failed", e);
			return Response.status(Response.Status.FORBIDDEN)
						   .build();
		}
	}

}

