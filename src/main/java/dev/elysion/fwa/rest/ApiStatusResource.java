package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.ApiStatus;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/")
@RequestScoped
public class ApiStatusResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getApiInformation() {
		ApiStatus status = new ApiStatus();
		status.setVersion("v1.1");
		status.setStatus("good");

		List<String> resources = new ArrayList<>();
		resources.add("/offers");
		resources.add("/offers/:id");
		status.setResources(resources);

		GenericEntity<ApiStatus> genericEntity = new GenericEntity<ApiStatus>(status) {};

		return Response.ok(genericEntity)
					   .build();
	}
}
