package dev.elysion.fwa.rest.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsResponseFilter implements ContainerResponseFilter {

	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void filter(ContainerRequestContext containerRequestContext,
					   ContainerResponseContext containerResponseContext) throws IOException {
		MultivaluedMap<String, Object> headers = containerResponseContext.getHeaders();

		headers.add("Access-Control-Allow-Origin", "*");
		headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT, OPTIONS, HEAD, PATCH");
		headers.add("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Authorization");

		LOGGER.trace("rest cors filter applied");
	}
}
