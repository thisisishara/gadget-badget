package com.gadgetbadget.funding.util;

import javax.ws.rs.core.MediaType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class InterServiceCommHandler {
	//List of hard-coded Service URIs
	private static final String PROTOCOL = "http://";
	private static final String HOST = "127.0.0.1";
	private static final String PORT = "8081";
	private static final String USER_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/UserService/userservice/";
	
	//JWT Service Token
	private static final String SERVICE_TOKEN_FND= "SVC eyJraWQiOiJKV0syIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLnNlcnZpY2VhdXRoIiwianRpIjoib0RVQjJPcXlnbjIxbkFTQ3loY1AwdyIsImlhdCI6MTYxODU0MDA1MywibmJmIjoxNjE4NTM5OTMzLCJzdWIiOiJnYWRnZXRiYWRnZXQuYXV0aC5jb20uZ2FkZ2V0YmFkZ2V0LmZ1bmRpbmciLCJ1c2VybmFtZSI6ImNvbS5nYWRnZXRiYWRnZXQuZnVuZGluZyIsInVzZXJfaWQiOiIwMDUiLCJyb2xlIjoiRk5EIn0.DZqFrrD2LnsDGb8DnE55JAoY44sdVJOVFF6BCA8QIEOfcfdkQTgHuWr3O9uaaT1sqXk_W7cl61bHsDvG9Y67oqInREP8ya1ULU7vbfAxLm8c3_H_cJb8t_87hLd3D43z45UvLz4wc_6Dlu_-h5oTDHbVzrEvGjdv-CTWABBsPaNApx5R_nStBAPKGNgiZ9dRNPqKvaKwQ4tjXwsdS8aVsQHMvjd0Jr6MsQcpIc3svxUESBAmBKvGSOBpqO-p9YU10kfGun3MgJtTWqbGvNZKAxHkN_fg8-2S6X-2q6a3ZwADBFMWYSB4q9rVtN_VL6TIfWzMZwhHx0iOvq8Q59NKZg";
	
	private Client client = null;
	private WebResource webRes = null;

	public JsonObject userIntercomms(String absolutePath, JsonObject payload)
	{
		client = Client.create();
		webRes = client.resource(USER_SERVICE_URI+absolutePath);
		String output = webRes.header("Authorization", SERVICE_TOKEN_FND)
									.entity(payload.toString(), MediaType.APPLICATION_JSON)
									.post(String.class);
		
		JsonObject JSONoutput = new JsonParser().parse(output).getAsJsonObject();
		return JSONoutput;
	}
}
