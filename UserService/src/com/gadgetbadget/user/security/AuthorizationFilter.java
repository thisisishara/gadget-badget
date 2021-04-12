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

import org.jose4j.base64url.Base64;

import com.gadgetbadget.user.util.JWTHandler;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Provider
public class AuthorizationFilter implements ContainerRequestFilter{

	@Context
	ResourceContext resourceContext;

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX = "JWT ";

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		String authToken = null;
		ResponseBuilder builder = null;
		boolean isValidToken = false;

		//Grant Access if Authentication is Requested.
		String[] URISegments = request.getPath().split("/");
		if(URISegments.length>1) {
			if((URISegments[0]+"/"+URISegments[1]).equals("security/authenticate")) {
				return request;
			}
		}

		//Check if the Authorization is not included in the Header
		List<String> authHeader = request.getRequestHeaders().get(AUTHORIZATION_HEADER_KEY);

		if(authHeader==null || authHeader.size()<=0) {
			String response = "Authorization Token not Found.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}

		authToken = authHeader.get(0);
		authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
		//TEST: System.out.println(authToken);

		if (authToken == null) {
			String response = "Invalid Authorization Token Format.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}

		//Validate JWT
		isValidToken = new JWTHandler().validateToken(authToken);
		if(!isValidToken) {
			String response = "Invalid Authorization Token Provided.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}

		//Check in Local TokenBlackList
		//Not implemented yet

		try {
			//Get JWT PAYLOAD Data
			String[] tokenSplitted = authToken.split("\\.");
			String tokenDecoded = new String(Base64.decode(tokenSplitted[1]));
			JsonObject tokenPayload = new JsonParser().parse(tokenDecoded).getAsJsonObject();
			//TEST: System.out.println(tokenPayload.toString());

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