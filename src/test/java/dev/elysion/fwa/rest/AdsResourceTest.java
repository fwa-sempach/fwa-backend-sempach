package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.Ad;
import dev.elysion.fwa.dto.InfoWrapper;
import dev.elysion.fwa.rest.filter.ObjectMapperContextResolver;
import dev.elysion.fwa.services.AdService;
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


public class AdsResourceTest extends JerseyTest {

	private static AdService mockedAdService;
	private static SecurityService mockedSecurityService;

	@Override
	protected Application configure() {
		mockedAdService = Mockito.mock(AdService.class);
		mockedSecurityService = Mockito.mock(SecurityService.class);
		ResourceConfig config = new ResourceConfig();
		config.register(new AdsResource(mockedAdService, mockedSecurityService));
		config.register(ObjectMapperContextResolver.class);
		return config;
	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(ObjectMapperContextResolver.class);
	}

	private List<Ad> adList;
	private Ad insertedAd;

	@Before
	public void init() throws IOException {

		//Init Testdata
		adList = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			Ad dto = new Ad();
			dto.setId(i);
			dto.setTitle("ad " + i);
			adList.add(dto);
		}

		insertedAd = new Ad();
		insertedAd.setId(5);
		insertedAd.setTitle("inserted Ad");


		doReturn(adList.get(0)).when(mockedAdService)
							   .getAdById(1);

		doReturn(adList).when(mockedAdService)
						.readPagedAndFiltered(anyInt(), anyInt(), anyList(), anyList(), anyString(), anyString(),
								anyBoolean());
		doReturn(insertedAd).when(mockedAdService)
							.saveAd(any(Ad.class));
	}

	@Test
	public void getAdById() {
		Ad response = target("jobads/1").request()
										.get(Ad.class);
		assertEquals(adList.get(0)
						   .getId(), response.getId());
		assertEquals(adList.get(0)
						   .getTitle(), response.getTitle());

		verify(mockedAdService).getAdById(1);
		verifyNoMoreInteractions(mockedAdService);
	}

	@Test
	public void getAds() {
		InfoWrapper<List<Ad>> response = target("jobads").request()
														 .get(new GenericType<InfoWrapper<List<Ad>>>() {});
		assertEquals(3, response.getElements()
								.size());

		verify(mockedAdService).readPagedAndFiltered(1, 10, new ArrayList<Integer>(), new ArrayList<Integer>(), "title"
				, "asc", false);
	}

	@Ignore
	@Test
	public void postAd() throws IOException {
		Ad response = target("jobads").request()
									  .post(Entity.entity(new Ad(), MediaType.APPLICATION_JSON), Ad.class);
		assertEquals(5, response.getId());
		assertEquals("inserted Ad", response.getTitle());

		verify(mockedAdService).saveAd(any(Ad.class));
	}

}