package dev.elysion.fwa.rest;

import dev.elysion.fwa.dto.Category;
import dev.elysion.fwa.rest.filter.ObjectMapperContextResolver;
import dev.elysion.fwa.services.CategoryService;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class CategoriesResourceTest extends JerseyTest {

	private static CategoryService mockedCategoryService;

	@Override
	protected Application configure() {
		mockedCategoryService = Mockito.mock(CategoryService.class);
		ResourceConfig config = new ResourceConfig();
		config.register(new CategoriesResource(mockedCategoryService));
		config.register(ObjectMapperContextResolver.class);
		return config;
	}

	@Override
	protected void configureClient(ClientConfig config) {
		config.register(ObjectMapperContextResolver.class);
	}

	private List<Category> categoriesList;

	@Before
	public void init() {

		//Init Testdata
		categoriesList = new ArrayList<>();
		for (int i = 1; i < 4; i++) {
			Category dto = new Category();
			dto.setId(i);
			dto.setName("category " + i);
			categoriesList.add(dto);
		}

		doReturn(categoriesList).when(mockedCategoryService)
								.readAllDtos();
	}

	@Test
	public void getCategories() {
		List<Category> response = target("categories").request()
													  .get(new GenericType<List<Category>>() {});
		assertEquals(3, response.size());
		verify(mockedCategoryService).readAllDtos();
		verifyNoMoreInteractions(mockedCategoryService);
	}

}