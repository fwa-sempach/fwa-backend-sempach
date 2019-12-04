package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.Category;
import dev.elysion.fwa.services.CategoryService;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/categories")
@RequestScoped
public class CategoriesResource {
	private CategoryService service;

	protected CategoriesResource() {
		//Benötigt für Proxy
	}

	@Inject
	public CategoriesResource(CategoryService service) {
		this.service = service;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOffers() {
		List<Category> result = service.readAllDtos();
		GenericEntity<List<Category>> genericEntity = new GenericEntity<List<Category>>(result) {};

		return Response.ok(genericEntity)
					   .build();
	}
}
