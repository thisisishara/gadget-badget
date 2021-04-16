package com.gadgetbadget.user.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.lang.JoseException;

import com.google.gson.JsonObject;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * This class is the original implementation of the authentication filter class which runs before any of the 
 * requests made to an end-point of this service/project. Looks for a valid authentication header in the 
 * HTTP request header and validates it. JWTHandler class is used to verify JWTs and decode the JWT PAYLOAD.
 * Adds the JWT PAYLOAD data to security context of the user or the service which can be used later on to validate/
 * authorize the particular user or the service at the specific end-point.
 * 
 * @author Ishara_Dissanayake
 * @version 2.0.0
 */
@Provider
public class AuthorizationFilter implements ContainerRequestFilter{

	@Context
	ResourceContext resourceContext;

	String authToken = null;
	String authTokenPrefix = null;
	ResponseBuilder builder = null;
	boolean isValidToken = false;

	// Check if the Authorization is not included in the Header
	List<String> authHeader = null;
	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

	/**
	 * This method is used to identify the headers and to allow access to
	 * different end-points accordingly.
	 */
	@Override
	public ContainerRequest filter(ContainerRequest request) {
		
			// Get Authorization Header
			authHeader = request.getRequestHeaders().get(AUTHORIZATION_HEADER_KEY);

			// Check URI and Grant users access for authenticating(login) and creating accounts
			String[] URISegments = request.getPath().split("/");
			if(URISegments.length>1) {

				// Grant Access if Authentication End-point is Requested.
				if(request.getPath().equals("security/authenticate")) {//(URISegments[0]+"/"+URISegments[1]).equals("security/authenticate")) {
					return request;
				}

				//Grant Access if New User Account is Requested.
				if(request.getMethod().equals("POST") && (request.getPath().equals("users/consumers") || request.getPath().equals("users/funders") || request.getPath().equals("users/researchers") || request.getPath().equals("users/employees"))) {//(URISegments[0]+"/"+URISegments[1]).equals("users/consumers")) {
					// If no header, return without header inspection
					if(authHeader==null || authHeader.size()<=0) {
						return request;
					}

					return inspectHeader(request);
				}
			}

			return inspectHeader(request);
	}

	/**
	 * This method is used by the AuthorizationFilter class to verify JWT tokens 
	 * in inspected headers when the header is not null. All JWT validation takes 
	 * place here onwards.
	 * 
	 * @param request	the HTTP request captured by the AuthorizationFilter
	 * @return			request after validating the JWT Token and attaching new 
	 * 					SecurityContext to the original request.
	 */
	private ContainerRequest inspectHeader(ContainerRequest request) {

		if(authHeader==null || authHeader.size()<=0) {
			String response = "Authorization Token not Found.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}

		authToken = authHeader.get(0);
		String[] authToken_parsed = authToken.split(" ");

		// Check if the Header Format is correct
		if(authToken_parsed.length != 2) {
			String response = "Invalid Authorization Token Format.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}

		authTokenPrefix = authToken.split(" ")[0];
		authToken = authToken.split(" ")[1];

		if (authToken == null) {
			String response = "Invalid Authorization Token Format.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}

		// Validate JWT Type based on the prefix (type of the JWT)
		try {
			if(authTokenPrefix.equalsIgnoreCase("JWT")) {
				isValidToken = new JWTHandler().validateToken(authToken);
			} else if (authTokenPrefix.equalsIgnoreCase("SVC")) {
				isValidToken = new JWTHandler().validateServiceToken(authToken);
			} else {
				String response = "Invalid Authorization Token Format.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build()); 
			}
		} catch (JoseException | MalformedClaimException e) {
			e.printStackTrace();
		}

		if(!isValidToken) {
			String response = "Invalid Authorization Token Provided.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}

		// Check in Local TokenBlackList
		// Token Blacklist is Not implemented
		// Tokens only get invalidated after they are expired

		// Decode JWT PAYLOAD Data
		JsonObject tokenPayload = new JWTHandler().decodeJWTPayload(authToken);

		if (! (tokenPayload.has("username") && tokenPayload.has("user_id") && tokenPayload.has("role"))) {
			String response = "Invalid Authorization Token Payload.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}

		String username = tokenPayload.get("username").getAsString();
		String user_id = tokenPayload.get("user_id").getAsString();
		String role = tokenPayload.get("role").getAsString();

		// Inject Token PAYLOAD data to SecurityContext 
		// of the request for Authorization at end-points
		SecurityContext securityContext = request.getSecurityContext();
		Set<String> roles = new HashSet<String>();
		roles.add(role);
		RoleSecurityContext roleSecurityContext = new RoleSecurityContext(roles, username, user_id, securityContext.isSecure());        
		request.setSecurityContext(roleSecurityContext);

		// Release Request for Authorization at
		// End-point, when Authentication is done
		return request;
	}
}