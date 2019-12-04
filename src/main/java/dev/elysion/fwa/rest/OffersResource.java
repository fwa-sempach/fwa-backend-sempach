package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.InfoWrapper;
import dev.elysion.fwa.dto.Offer;
import dev.elysion.fwa.enumeration.Role;
import dev.elysion.fwa.rest.filter.Secured;
import dev.elysion.fwa.services.OfferService;
import dev.elysion.fwa.services.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.List;

@Path("/offers")
@RequestScoped
public class OffersResource {

	private static final Logger LOGGER = LogManager.getLogger();

	private OfferService service;
	private SecurityService securityService;

	protected OffersResource() {
		//Benötigt für Proxy
	}

	@Context
	private SecurityContext securityContext;

	@Inject
	public OffersResource(OfferService service, SecurityService securityService) {
		this.service = service;
		this.securityService = securityService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOffers(@QueryParam("_page") @DefaultValue("1") int page,
							  @QueryParam("_size") @DefaultValue("10") int pageSize,
							  @QueryParam("_sort") @DefaultValue("title") String sortField,
							  @QueryParam("_order") @DefaultValue("asc") String sortOrder,
							  @QueryParam("category.id") List<Integer> categoryIds,
							  @QueryParam("organisation.id") List<Integer> organisationIds,
							  @QueryParam("active") @DefaultValue("false") boolean onlyActive) {
		Long totalCount = service.readFilteredCount(categoryIds, organisationIds, onlyActive);
		// TODO: only allow to sort by fields which are in the offerOverview class
		List<Offer> offers = service.readPagedAndFiltered(page, pageSize, categoryIds, organisationIds, onlyActive,
				sortField, sortOrder);

		InfoWrapper<List<Offer>> payload = new InfoWrapper<>();
		payload.setTotalCount(totalCount);
		payload.setCount(offers.size());
		payload.setElements(offers);

		GenericEntity<InfoWrapper<List<Offer>>> genericEntity =
				new GenericEntity<InfoWrapper<List<Offer>>>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@GET
	@Path("/{offerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOfferById(@PathParam("offerId") int offerId) {
		Offer payload = service.getOfferById(offerId);

		if (payload == null || payload.getId() == 0) {
			return Response.status(Response.Status.NOT_FOUND)
						   .build();
		}

		GenericEntity<Offer> genericEntity = new GenericEntity<Offer>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@POST
	@Secured({Role.ORG, Role.ADMIN})
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response insertOffer(Offer offer) {
		int organisationId = offer.getOrganisation()
								  .getId();
		securityService.verifyOrganisation(securityContext, organisationId);

		try {
			offer = service.saveOffer(offer);
		}
		catch (IOException e) {
			LOGGER.error("offer not saved: {}", e, offer.getId());

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						   .build();
		}

		GenericEntity<Offer> genericEntity = new GenericEntity<Offer>(offer) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@PUT
	@Secured({Role.ORG, Role.ADMIN})
	@Path("/{offerId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOffer(@PathParam("offerId") int offerId, Offer offer) {
		int organisationId = offer.getOrganisation()
								  .getId();
		securityService.verifyOrganisation(securityContext, organisationId);

		offer.setId(offerId);
		try {
			offer = service.saveOffer(offer);
		}
		catch (IOException e) {
			LOGGER.error("offer not saved: {}", e, offer.getId());

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						   .build();
		}

		GenericEntity<Offer> genericEntity = new GenericEntity<Offer>(offer) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@DELETE
	@Secured({Role.ORG, Role.ADMIN})
	@Path("/{offerId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteOffer(@PathParam("offerId") int offerId) {
		int organisationId = service.getOfferById(offerId)
									.getOrganisation()
									.getId();
		securityService.verifyOrganisation(securityContext, organisationId);

		service.deleteOffer(offerId);
		return Response.ok()
					   .build();
	}
}
