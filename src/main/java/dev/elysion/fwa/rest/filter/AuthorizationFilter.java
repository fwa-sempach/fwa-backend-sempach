package dev.elysion.fwa.rest.filter;

import dev.elysion.fwa.enumeration.Role;
import dev.elysion.fwa.rest.auth.TokenBasedSecurityContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)

public class AuthorizationFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LogManager.getLogger();

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Get the resource class which matches with the requested URL
		// Extract the roles declared by it
		Class<?> resourceClass = resourceInfo.getResourceClass();
		List<Role> classRoles = extractRoles(resourceClass);

		// Get the resource method which matches with the requested URL
		// Extract the roles declared by it
		Method resourceMethod = resourceInfo.getResourceMethod();
		List<Role> methodRoles = extractRoles(resourceMethod);

		try {
			// Check if the user is allowed to execute the method
			// The method annotations override the class annotations
			if (!methodRoles.isEmpty()) {
				checkPermissions(methodRoles, requestContext.getSecurityContext());
			}
			else if (!classRoles.isEmpty()) {
				checkPermissions(classRoles, requestContext.getSecurityContext());
			}

		}
		catch (Exception e) {
			LOGGER.error("Authorization failed", e);
			requestContext.abortWith(Response.status(Response.Status.FORBIDDEN)
											 .build());
		}
	}


	// Extract the roles from the annotated element
	private List<Role> extractRoles(AnnotatedElement annotatedElement) {
		if (annotatedElement == null) {
			return Collections.emptyList();
		}
		else {
			Secured secured = annotatedElement.getAnnotation(Secured.class);
			if (secured == null) {
				return Collections.emptyList();
			}
			else {
				Role[] allowedRoles = secured.value();
				return Arrays.asList(allowedRoles);
			}
		}
	}

	private void checkPermissions(List<Role> allowedRoles, SecurityContext securityContext) throws Exception {
		TokenBasedSecurityContext tokenBasedSecurityContext = (TokenBasedSecurityContext) securityContext;

		boolean authorized = allowedRoles.stream()
										 .map(Role::name)
										 .anyMatch(tokenBasedSecurityContext::isUserInRole);

		if (!authorized) {
			throw new Exception("not authorized");
		}
	}
}
