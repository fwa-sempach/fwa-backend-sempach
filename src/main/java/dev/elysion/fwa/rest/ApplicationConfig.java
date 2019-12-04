package dev.elysion.fwa.rest;

import dev.elysion.fwa.rest.auth.AuthResource;
import dev.elysion.fwa.rest.filter.AuthenticationFilter;
import dev.elysion.fwa.rest.filter.AuthorizationFilter;
import dev.elysion.fwa.rest.filter.CorsResponseFilter;
import dev.elysion.fwa.rest.filter.ObjectMapperContextResolver;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;


@ApplicationScoped
@ApplicationPath("/api/v1")
public class ApplicationConfig extends Application {
	@Override
	public Set<Class<?>> getClasses() {
		return Set.of(AuthResource.class, AdsResource.class, ApiStatusResource.class, CategoriesResource.class,
				ConfigResource.class, DocumentsResource.class, OffersResource.class, OrganisationsResource.class,
				UsersRessource.class, VerificationRessource.class, CorsResponseFilter.class,
				ObjectMapperContextResolver.class, AuthenticationFilter.class, AuthorizationFilter.class,
				JacksonFeature.class);
	}
}