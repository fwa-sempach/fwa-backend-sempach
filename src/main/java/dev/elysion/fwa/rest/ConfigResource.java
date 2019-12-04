package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.Config;
import dev.elysion.fwa.enumeration.Role;
import dev.elysion.fwa.rest.filter.Secured;
import dev.elysion.fwa.services.ConfigService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/config")
@RequestScoped
public class ConfigResource {

	private ConfigService configService;

	protected ConfigResource() {
		// CDI proxy
	}

	@Inject
	public ConfigResource(ConfigService configService) {
		this.configService = configService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConfig() {
		Config payload = configService.readConfig();

		GenericEntity<Config> genericEntity = new GenericEntity<Config>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@POST
	@Secured({Role.ADMIN})
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setConfig(Config config) {
		// TODO: user service to write config
		Config payload = config;

		GenericEntity<Config> genericEntity = new GenericEntity<Config>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

}

