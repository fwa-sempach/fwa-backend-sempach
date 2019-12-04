package dev.elysion.fwa.rest;

import dev.elysion.fwa.dao.UserDao;
import dev.elysion.fwa.dto.InfoWrapper;
import dev.elysion.fwa.dto.Organisation;
import dev.elysion.fwa.dto.Participant;
import dev.elysion.fwa.enumeration.ParticipantStatus;
import dev.elysion.fwa.enumeration.Role;
import dev.elysion.fwa.rest.filter.Secured;
import dev.elysion.fwa.services.OrganisationService;
import dev.elysion.fwa.services.ParticipantService;
import dev.elysion.fwa.services.SecurityService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.List;

@Path("/organisations")
@RequestScoped
public class OrganisationsResource {

	private static final Logger LOGGER = LogManager.getLogger();

	private OrganisationService organisationService;
	private ParticipantService participantService;
	private SecurityService securityService;
	private UserDao userDao;

	protected OrganisationsResource() {
		//Benötigt für Proxy
	}

	@Context
	private SecurityContext securityContext;

	@Inject
	public OrganisationsResource(OrganisationService organisationService, ParticipantService participantService,
								 SecurityService securityService, UserDao userDao) {
		this.organisationService = organisationService;
		this.participantService = participantService;
		this.securityService = securityService;
		this.userDao = userDao;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrganisations(@QueryParam("_page") @DefaultValue("1") int page,
									 @QueryParam("_size") @DefaultValue("10") int pageSize,
									 @QueryParam("_sort") @DefaultValue("orgName") String sortField,
									 @QueryParam("_order") @DefaultValue("asc") String sortOrder,
									 @QueryParam("verified") @DefaultValue("true") boolean onlyVerified) {
		Long totalCount = organisationService.readFilteredCount(onlyVerified);
		List<Organisation> organisations = organisationService.readPagedAndFiltered(page, pageSize, sortField,
				sortOrder, onlyVerified);

		InfoWrapper<List<Organisation>> payload = new InfoWrapper<>();
		payload.setTotalCount(totalCount);
		payload.setCount(organisations.size());
		payload.setElements(organisations);

		GenericEntity<InfoWrapper<List<Organisation>>> genericEntity =
				new GenericEntity<InfoWrapper<List<Organisation>>>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@GET
	@Path("/{organisationId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrganisationById(@PathParam("organisationId") int organisationId) {
		Organisation payload = organisationService.readOrganisationById(organisationId);

		if (payload == null || payload.getId() == 0) {
			return Response.status(Response.Status.NOT_FOUND)
						   .build();
		}

		GenericEntity<Organisation> genericEntity = new GenericEntity<Organisation>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@POST
	@Secured({Role.ORG, Role.ADMIN})
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response insertOrganisation(Organisation organisation) {
		organisation.setVerified(false);

		int userId = userDao.readByUsername(securityContext.getUserPrincipal()
														   .getName())
							.getId();
		organisation.setUserId(userId);

		try {
			organisation = organisationService.saveOrganisation(organisation, false);
		}
		catch (IOException e) {
			LOGGER.error("notifyOrg Mail not sent", e, organisation.getName());

			return Response.status(Response.Status.BAD_REQUEST)
						   .entity("OrganisationEmail could not be sent")
						   .build();
		}


		GenericEntity<Organisation> genericEntity = new GenericEntity<Organisation>(organisation) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@PUT
	@Path("/{organisationId}")
	@Secured({Role.ORG, Role.ADMIN})
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateOrganisation(@PathParam("organisationId") int organisationId, Organisation organisation,
									   @QueryParam("notifyOrg") @DefaultValue("false") boolean notifyOrg) {
		securityService.verifyOrganisation(securityContext, organisationId);

		try {
			organisation = organisationService.saveOrganisation(organisation, notifyOrg);
		}
		catch (IOException e) {
			LOGGER.error("notifyOrg Mail not sent", e, organisation.getName());

			return Response.status(Response.Status.BAD_REQUEST)
						   .entity("OrganisationEmail could not be sent")
						   .build();
		}

		GenericEntity<Organisation> genericEntity = new GenericEntity<Organisation>(organisation) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@DELETE
	@Path("/{organisationId}")
	@Secured({Role.ADMIN})
	public Response deleteOrganisation(@PathParam("organisationId") int organisationId, @QueryParam("notifyOrg") @DefaultValue("false") boolean notifyOrg){
		Organisation organisation = organisationService.deleteOrganisation(organisationId);
		GenericEntity<Organisation> genericEntity = new GenericEntity<Organisation>(organisation) {};
		return Response.ok(genericEntity)
				.build();
	}

	@POST
	@Path("/{organisationId}/participants")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response insertParticipant(@PathParam("organisationId") int organisationId,
									  @QueryParam("notifyOrg") @DefaultValue("false") boolean notifyOrg,
									  Participant participant) {
		try {
			participant = participantService.saveParticipant(participant, notifyOrg);
		}
		catch (IOException e) {
			LOGGER.error("participant not saved: {}", e, participant.getId());

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						   .build();
		}


		GenericEntity<Participant> genericEntity = new GenericEntity<Participant>(participant) {};

		return Response.ok(genericEntity)
					   .build();
	}


	@PUT
	@Secured({Role.ORG, Role.ADMIN})
	@Path("/{organisationId}/participants/{participantId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateParticipant(@PathParam("organisationId") int organisationId,
									  @PathParam("participantId") int participantId, Participant participant) {
		securityService.verifyOrganisation(securityContext, organisationId);

		participant.setOrganisationId(organisationId);
		participant.setId(participantId);

		try {
			participant = participantService.saveParticipant(participant, false);
		}
		catch (IOException e) {
			LOGGER.error("participant not saved: {}", e, participant.getId());

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
						   .build();
		}

		GenericEntity<Participant> genericEntity = new GenericEntity<Participant>(participant) {};

		return Response.ok(genericEntity)
					   .build();
	}

	@DELETE
	@Secured({Role.ORG, Role.ADMIN})
	@Path("/{organisationId}/participants/{participantId}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteOffer(@PathParam("organisationId") int organisationId,
								@PathParam("participantId") int participantId) {
		securityService.verifyOrganisation(securityContext, organisationId);

		participantService.deleteParticipant(participantId);
		return Response.ok()
					   .build();
	}


	@GET
	@Secured({Role.ORG, Role.ADMIN})
	@Path("/{organisationId}/participants")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getParticipantsPagedAndFiltered(@PathParam("organisationId") int organisationId,
													@QueryParam("_page") @DefaultValue("1") int page,
													@QueryParam("_size") @DefaultValue("10") int pageSize,
													@QueryParam("_sort") @DefaultValue("status") String sortField,
													@QueryParam("_order") @DefaultValue("asc") String sortOrder,
													@QueryParam("ad.id") List<Integer> adIds,
													@QueryParam("status") List<ParticipantStatus> status) {
		securityService.verifyOrganisation(securityContext, organisationId);

		// TODO: select real count
		Long totalCount = participantService.readFilteredCount(organisationId, adIds, status);
		List<Participant> participants = participantService.readPagedAndFiltered(page, pageSize, organisationId, adIds
				, status, sortField, sortOrder);

		InfoWrapper<List<Participant>> payload = new InfoWrapper<>();
		payload.setTotalCount(totalCount);
		payload.setCount(participants.size());
		payload.setElements(participants);

		GenericEntity<InfoWrapper<List<Participant>>> genericEntity =
				new GenericEntity<InfoWrapper<List<Participant>>>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}


	@GET
	@Secured({Role.ORG, Role.ADMIN})
	@Path("/{organisationId}/participants/{participantId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getParticipantById(@PathParam("organisationId") int organisationId,
									   @PathParam("participantId") int participantId) {
		securityService.verifyOrganisation(securityContext, organisationId);

		Participant payload = participantService.readParticipantById(participantId);
		GenericEntity<Participant> genericEntity = new GenericEntity<Participant>(payload) {};

		return Response.ok(genericEntity)
					   .build();
	}
}
