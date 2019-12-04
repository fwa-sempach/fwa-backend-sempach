package dev.elysion.fwa.rest;

import dev.elysion.fwa.dao.UserDao;
import dev.elysion.fwa.dto.InfoWrapper;
import dev.elysion.fwa.dto.Organisation;
import dev.elysion.fwa.dto.Participant;
import dev.elysion.fwa.rest.filter.ObjectMapperContextResolver;
import dev.elysion.fwa.services.OrganisationService;
import dev.elysion.fwa.services.ParticipantService;
import dev.elysion.fwa.services.SecurityService;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class OrganisationsResourceTest extends JerseyTest {

	private static OrganisationService mockedOrganisationService;
	private static ParticipantService mockedParticipantService;
	private static SecurityService mockedSecurityService;
	private static UserDao mockedUserDao;

	@Override
	protected Application configure() {
		mockedOrganisationService = Mockito.mock(OrganisationService.class);
		mockedParticipantService = Mockito.mock(ParticipantService.class);
		mockedSecurityService = mock(SecurityService.class);
		mockedUserDao = mock(UserDao.class);

		ResourceConfig config = new ResourceConfig();
		config.register(new OrganisationsResource(mockedOrganisationService, mockedParticipantService,
				mockedSecurityService, mockedUserDao));
		config.register(ObjectMapperContextResolver.class);
		return config;
	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(ObjectMapperContextResolver.class);
	}

	private List<Organisation> organisationList;
	private Organisation insertedOrganisation;
	private List<Participant> participantList;
	private Participant insertedParticipant;

	@Before
	public void init() throws IOException {

		//Init Testdata
		organisationList = new ArrayList<>();
		participantList = new ArrayList<>();

		for (int i = 1; i < 4; i++) {
			Organisation dto = new Organisation();
			dto.setId(i);
			dto.setName("org " + i);
			organisationList.add(dto);
			Participant p = new Participant();
			p.setId(i);
			participantList.add(p);
		}

		insertedOrganisation = new Organisation();
		insertedOrganisation.setId(5);
		insertedOrganisation.setName("inserted Organisation");

		insertedParticipant = new Participant();
		insertedParticipant.setId(5);

		doReturn(organisationList.get(0)).when(mockedOrganisationService)
										 .readOrganisationById(1);
		doReturn(organisationList).when(mockedOrganisationService)
								  .readPagedAndFiltered(anyInt(), anyInt(), anyString(), anyString(), anyBoolean());
		doReturn(participantList.get(0)).when(mockedParticipantService)
										.readParticipantById(1);
		doReturn(participantList).when(mockedParticipantService)
								 .readAll();
		doReturn(insertedParticipant).when(mockedParticipantService)
									 .saveParticipant(any(Participant.class), anyBoolean());
	}

	@Test
	public void getOrganisations() {
		InfoWrapper<List<Organisation>> response = target("organisations").request()
																		  .get(new GenericType<InfoWrapper<List<Organisation>>>() {});
		assertEquals(3, response.getElements()
								.size());
		verify(mockedOrganisationService).readPagedAndFiltered(1, 10, "orgName", "asc", true);
		verify(mockedOrganisationService).readFilteredCount(true);
		verifyNoMoreInteractions(mockedOrganisationService);
	}

	@Test
	public void getOrganistaionById() {
		Organisation response = target("organisations/1").request()
														 .get(Organisation.class);
		assertEquals(organisationList.get(0)
									 .getId(), response.getId());
		assertEquals(organisationList.get(0)
									 .getName(), response.getName());

		verify(mockedOrganisationService).readOrganisationById(1);
		verifyNoMoreInteractions(mockedOrganisationService);
	}

	@Test
	public void postParticipant() throws IOException {
		Participant response = target("organisations/1/participants").request()
																	 .post(Entity.entity(new Participant(),
																			 MediaType.APPLICATION_JSON),
																			 Participant.class);
		assertEquals(5, response.getId());
		verify(mockedParticipantService).saveParticipant(any(Participant.class), anyBoolean());
		verifyNoMoreInteractions(mockedParticipantService);
	}
}