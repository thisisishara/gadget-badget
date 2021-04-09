package com.gadgetbadget.user.security;

import java.util.List;
import java.util.StringTokenizer;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.Provider;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.core.ResourceContext;
import com.sun.jersey.core.util.Base64;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Provider
public class AuthorizationFilter implements ContainerRequestFilter{

	//Need to access same-API End-points
	@Context
	ResourceContext resourceContext;

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";

	@Override
	public ContainerRequest filter(ContainerRequest request) {
		//Checking URI paths to grant login requests
		String[] URISegments = request.getPath().split("/");
		if(URISegments.length>1) {
			if((URISegments[0]+"/"+URISegments[1]).equals("security/login")) {
				return request;
			}
		}

		/*
		//Checking headers to grant only authorized users
		List<String> authHeader = request.getRequestHeaders().get(AUTHORIZATION_HEADER_KEY);
		if(authHeader!=null && authHeader.size()>0) {
			String authToken = authHeader.get(0);
			authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
			String decodedString = new String(Base64.decode(authToken));
			StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
			String username = tokenizer.nextToken();
			System.out.println("user"+ authHeader + " after: " +authToken+ " dec: " + decodedString); //log
			String password = tokenizer.nextToken();

			if(username.equalsIgnoreCase("hey") && password.equalsIgnoreCase("pass")) {

				//Access remote service 
				Client c = Client.create();
				WebResource resource = c.resource("http://127.0.0.1:8080/Lab5Rest/myService/Hello/" + username);
				String output = resource.get(String.class);
				System.out.println(output+" damnnnnnn!");

				//Access same-API end points.
				//@Context code on the top is required: on top after class declaration.
				//	@Context
				//	ResourceContext resourceContext;
				//				ItemService itemService = resourceContext.getResource(ItemService.class);
				//				String output2 = itemService.intercomms("Ishara");
				//				System.out.println("Same Project Resource usage output: " + output2);

				return request;
			}
		}

		//Deny Access with an UNAUTHORIZED response for unauthorized users
		ResponseBuilder builder = null;
		String response = "You are not Authorized to access this End Point!";
		builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
		throw new WebApplicationException(builder.build());
		*/
		
		//temporarily accept all requests
		return request;
	}
}