package com.gadgetbadget.payment.util;

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
	private static final String SERVICE_TOKEN_PYT= "SVC eyJraWQiOiJKV0syIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLnNlcnZpY2VhdXRoIiwianRpIjoiMXJDWmxPbnNLOC0wa004QmpHb09QQSIsImlhdCI6MTYxODUzOTgyOCwibmJmIjoxNjE4NTM5NzA4LCJzdWIiOiJnYWRnZXRiYWRnZXQuYXV0aC5jb20uZ2FkZ2V0YmFkZ2V0LnBheW1lbnQiLCJ1c2VybmFtZSI6ImNvbS5nYWRnZXRiYWRnZXQucGF5bWVudCIsInVzZXJfaWQiOiIwMDIiLCJyb2xlIjoiUFlUIn0.jyJqo-rx5xPCj11m6j4aRVguYbpULcGbbSPPdX0-poDAuQ8LhYX5FtjRhthfojJ6_Ens-QciM1y3rxWTgLVFqQzmpbsfGUA0FYurhGusw_0NtTgpBTak1xzYDZB70GpzWQk5UQBgUAH6oUn2jJcIfD6C5wjv-0oL8dsHLnXo7Mjt8j-5plM2q89n-sBoHSkEl9iztGeyOQU_NGycw437vURNZFk_X81LVjcSFxjAZukP0sIoeCEaN07IoR3QwnHf6yBsW9rIfHhmJ1rV_YF-zniAsgsi8CGpJGtoUsADglPxyDXOZeMqpMRBvn65dj1I11DPGHMwHGNr9u_JgWuC0w";
	
	private Client client = null;
	private WebResource webRes = null;

	public JsonObject userIntercomms(String absolutePath, JsonObject payload)
	{
		client = Client.create();
		webRes = client.resource(USER_SERVICE_URI+absolutePath);
		String output = webRes.header("Authorization", SERVICE_TOKEN_PYT)
									.entity(payload.toString(), MediaType.APPLICATION_JSON)
									.post(String.class);
		
		JsonObject JSONoutput = new JsonParser().parse(output).getAsJsonObject();
		return JSONoutput;
	}
}
