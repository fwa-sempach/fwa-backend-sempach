package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.Ad;
import dev.elysion.fwa.dto.InfoWrapper;
import dev.elysion.fwa.enumeration.Role;
import dev.elysion.fwa.rest.filter.Secured;
import dev.elysion.fwa.services.AdService;
import dev.elysion.fwa.services.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.List;

@Path("/jobads")
@RequestScoped
public class AdsResource {

	private static final Logger LOGGER = LogManager.getLogger();

	private AdService service;
	private SecurityService securityService;

	protected AdsResource() {
		//Benötigt für Proxy
	}

	@Context
	private SecurityContext securityContext;

	@Inject
	public AdsResource(AdService service, SecurityService securityService) {
		this.service = service;
		this.securityService = securityService;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAds(@QueryParam("_page") @DefaultValue("1") int page,
						   @QueryParam("_size") @DefaultValue("10") int pageSize,
						   @QueryParam("_sort") @DefaultValue("title") String sortField,
						   @QueryParam("_order") @DefaultValue("asc") String sortOrder,
						   @QueryParam("offer.category.id") List<Integer> categoryIds,
						   @QueryParam("offer.organisation.id") List<Integer> organisationIds,
						   @QueryParam("active") @DefaultValue("false") boolean onlyActive) {

		Long totalCount = service.readFilteredCount(categoryIds, organisationIds, onlyActive);
		// TODO: only allow to sort by fields which are in the offerOverview class
		List<Ad> ads = service.readPagedAndFiltered(page, pageSize, categoryIds, organisationIds, sortField, sortOrder
				, onlyActive);

		InfoWrapper<List<Ad>> payload = new InfoWrapper<>();
		payload.setTotalCount(totalCount);
		payload.setCount(ads.size());
		payload.setElements(ads);

		GenericEntity<InfoWrapper<List<Ad>>> genericEntity = new GenericEntity<InfoWrapper<List<Ad>>>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@GET
	@Path("/{adId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAdById(@PathParam("adId") int adId) {
		Ad payload = service.getAdById(adId);

		if (payload == null || payload.getId() == 0) {
			return Response.status(Response.Status.NOT_FOUND)
						   .build();
		}

		GenericEntity<Ad> genericEntity = new GenericEntity<Ad>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@POST
	@Secured({Role.ORG, Role.ADMIN})
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response insertAd(Ad ad) {
		int organisationId = ad.getOffer()
							   .getOrganisation()
							   .getId();
		securityService.verifyOrganisation(securityContext, organisationId);

		try {
			ad = service.saveAd(ad);
		}
		catch (IOException e) {
			LOGGER.error("ad not saved: {}", e, ad.getId());

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						   .build();
		}

		GenericEntity<Ad> genericEntity = new GenericEntity<Ad>(ad) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@PUT
	@Secured({Role.ORG, Role.ADMIN})
	@Path("/{adId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateAd(@PathParam("adId") int adId, Ad ad) {
		int organisationId = ad.getOffer()
							   .getOrganisation()
							   .getId();
		securityService.verifyOrganisation(securityContext, organisationId);

		ad.setId(adId);

		try {
			ad = service.saveAd(ad);
		}
		catch (IOException e) {
			LOGGER.error("ad not saved: {}", e, ad.getId());

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						   .build();
		}

		GenericEntity<Ad> genericEntity = new GenericEntity<Ad>(ad) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@DELETE
	@Secured({Role.ORG, Role.ADMIN})
	@Path("/{adId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteAd(@PathParam("adId") int adId) {
		int organisationId = service.getAdById(adId)
									.getOffer()
									.getOrganisation()
									.getId();
		securityService.verifyOrganisation(securityContext, organisationId);

		service.deleteAd(adId);
		return Response.ok()
					   .build();
	}
}
