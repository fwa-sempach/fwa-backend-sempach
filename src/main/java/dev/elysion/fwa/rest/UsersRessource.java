package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.User;
import dev.elysion.fwa.rest.filter.Secured;
import dev.elysion.fwa.services.SecurityService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/users")
@RequestScoped
public class UsersRessource {

	private SecurityService service;
	private SecurityService securityService;

	protected UsersRessource() {
		//Benötigt für Proxy
	}

	@Context
	private SecurityContext securityContext;

	@Inject
	public UsersRessource(SecurityService service, SecurityService securityService) {
		this.service = service;
		this.securityService = securityService;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(User user) {
		try {
			user = service.createNewUser(user.getUsername(), user.getPassword(), user.getEmail());
		}
		catch (Exception e) {
			LogManager.getLogger()
					  .error("user {} already exists", e, user.getUsername());

			return Response.status(Response.Status.BAD_REQUEST)
						   .entity("User already exists")
						   .build();
		}

		user.setPassword(null);
		user.setSalt(null);

		GenericEntity<User> genericEntity = new GenericEntity<User>(user) {};
		return Response.ok(genericEntity)
					   .build();
	}

	@PUT
	@Secured()
	@Path("/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateUser(@PathParam("username") String username, User user) {
		securityService.verifyUser(securityContext, username);

		user.setUsername(username);

		try {
			if (StringUtils.isNotEmpty(user.getPassword())) {
				service.updatePassword(username, user.getPassword());
			}

			if (StringUtils.isNotEmpty(user.getEmail())) {
				service.updateEmail(username, user.getEmail());
			}
		}
		catch (Exception e) {
			LogManager.getLogger()
					  .error("password or email could not be changed for user {}", e, user.getUsername());

			return Response.status(Response.Status.BAD_REQUEST)
						   .build();
		}

		return Response.ok()
					   .build();
	}
}
