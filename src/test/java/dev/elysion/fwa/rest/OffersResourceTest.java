package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.InfoWrapper;
import dev.elysion.fwa.dto.Offer;
import dev.elysion.fwa.rest.filter.ObjectMapperContextResolver;
import dev.elysion.fwa.services.OfferService;
import dev.elysion.fwa.services.SecurityService;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Ignore;
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


public class OffersResourceTest extends JerseyTest {

	private static OfferService mockedOfferService;
	private static SecurityService mockedSecurityService;

	@Override
	protected Application configure() {
		mockedOfferService = Mockito.mock(OfferService.class);
		mockedSecurityService = Mockito.mock(SecurityService.class);
		ResourceConfig config = new ResourceConfig();
		config.register(new OffersResource(mockedOfferService, mockedSecurityService));
		config.register(ObjectMapperContextResolver.class);
		return config;
	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(ObjectMapperContextResolver.class);
	}

	private List<Offer> offerList;
	private Offer newOffer;
	private Offer insertedOffer;
	private Offer updatedOffer;

	@Before
	public void init() throws IOException {

		//Init test data
		offerList = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			Offer dto = new Offer();
			dto.setId(i);
			dto.setTitle("offer " + i);
			offerList.add(dto);
		}

		insertedOffer = new Offer();
		insertedOffer.setId(5);
		insertedOffer.setTitle("inserted Offer");

		doReturn(offerList.get(0)).when(mockedOfferService)
								  .getOfferById(1);

		doReturn(offerList).when(mockedOfferService)
						   .readPagedAndFiltered(anyInt(), anyInt(), anyList(), anyList(), anyBoolean(), anyString(),
								   anyString());
		doReturn(insertedOffer).when(mockedOfferService)
							   .saveOffer(any(Offer.class));
	}

	@Test
	public void getOfferById() {
		Offer response = target("offers/1").request()
										   .get(Offer.class);
		assertEquals(offerList.get(0)
							  .getId(), response.getId());
		assertEquals(offerList.get(0)
							  .getTitle(), response.getTitle());

		verify(mockedOfferService).getOfferById(1);
		verifyNoMoreInteractions(mockedOfferService);
	}


	//TODO: read all, post, put


	@Test
	public void getOffers() {
		InfoWrapper<List<Offer>> response = target("offers").request()
															.get(new GenericType<InfoWrapper<List<Offer>>>() {});
		assertEquals(3, response.getElements()
								.size());
		assertEquals(Integer.valueOf(3), response.getCount());

		verify(mockedOfferService).readPagedAndFiltered(1, 10, new ArrayList<>(), new ArrayList<>(), false, "title",
				"asc");
		verify(mockedOfferService).readFilteredCount(new ArrayList<>(), new ArrayList<>(), false);
		verifyNoMoreInteractions(mockedOfferService);
	}

	@Test
	@Ignore
	public void postOffer() throws IOException {
		Offer response = target("offers").request()
										 .post(Entity.entity(new Offer(), MediaType.APPLICATION_JSON), Offer.class);
		assertEquals(5, response.getId());
		assertEquals("inserted Offer", response.getTitle());

		verify(mockedOfferService).saveOffer(any(Offer.class));
	}

	@Test
	@Ignore
	public void putOffer() throws IOException {
		Offer response = target("offers/5").request()
										   .put(Entity.entity(insertedOffer, MediaType.APPLICATION_JSON), Offer.class);
		assertEquals(5, response.getId());
		assertEquals("inserted Offer", response.getTitle());

		verify(mockedOfferService).saveOffer(any(Offer.class));
		verifyNoMoreInteractions(mockedOfferService);
	}
}