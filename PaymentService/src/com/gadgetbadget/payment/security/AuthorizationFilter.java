package com.gadgetbadget.payment.security;

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
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * This is a variant of the original AuthorizationFilter class implemented in UserService.
 * 
 * @author Ishara_Dissanayake
 * @version 2.0.1
 */
@Provider
public class AuthorizationFilter implements ContainerRequestFilter{

	@Context
	ResourceContext resourceContext;

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		String authToken = null;
		String authTokenPrefix = null;
		ResponseBuilder builder = null;
		boolean isValidToken = false;

		//Check if the Authorization is not included in the Header
		List<String> authHeader = request.getRequestHeaders().get(AUTHORIZATION_HEADER_KEY);

		if(authHeader==null || authHeader.size()<=0) {
			String response = "Authorization Token not Found.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}
		System.out.println(authHeader);
		authToken = authHeader.get(0);
		
		String[] authToken_parsed = authToken.split(" ");
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

		System.out.println(authToken);
		//Validate JWT
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

		//Check in Local TokenBlackList
		//Token Blacklist is Not implemented
		//Tokens only get invalidated after they are expired
		
		try {
			//Get JWT PAYLOAD Data
			JsonObject tokenPayload = new JWTHandler().decodeJWTPayload(authToken);

			if (! (tokenPayload.has("username") && tokenPayload.has("user_id") && tokenPayload.has("role"))) {
				String response = "Invalid Authorization Token Payload.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}

			String username = tokenPayload.get("username").getAsString();
			String user_id = tokenPayload.get("user_id").getAsString();
			String role = tokenPayload.get("role").getAsString();

			//Inject Token PAYLOAD data to SecurityContext 
			//of the request for Authorization at end-points
			SecurityContext securityContext = request.getSecurityContext();
			Set<String> roles = new HashSet<String>();
			roles.add(role);
			RoleSecurityContext roleSecurityContext = new RoleSecurityContext(roles, username, user_id, securityContext.isSecure());        
			request.setSecurityContext(roleSecurityContext);

			//Release Request for Authorization at
			//End-point, when Authentication is done
			return request;

		} catch (JsonSyntaxException ex) {
			String response = "Invalid Authorization Token Payload.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}
	}
}
