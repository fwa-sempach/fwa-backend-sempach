package dev.elysion.fwa.rest;

import dev.elysion.fwa.services.SecurityService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/verify")
@RequestScoped
public class VerificationRessource {

	private SecurityService service;

	protected VerificationRessource() {
		//Benötigt für Proxy
	}

	@Inject
	public VerificationRessource(SecurityService service) {
		this.service = service;
	}

	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response verifyEmail(String token) {
		try {
			service.verifyEmailAddress(token);
		}
		catch (NotAuthorizedException e) {
			return Response.status(Response.Status.UNAUTHORIZED)
						   .build();
		}
		catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						   .build();
		}
		return Response.ok()
					   .build();
	}
}
