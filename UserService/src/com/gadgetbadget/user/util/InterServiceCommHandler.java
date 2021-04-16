package com.gadgetbadget.user.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * This class is used when classes in USER SERVICE needs to establish service-to-service communication 
 * with other web services of the GADGETBADGET system. All known web service host URIs are hard-coded and 
 * the service authentication JWTs are also included in this class which can be utilized when making
 * a HTTP request to authenticate the validity of the service and service authorization when accessing
 * particular end-points of other web-services.
 * 
 * @author Ishara_Dissanayake
 */
public class InterServiceCommHandler {
	//List of hard-coded Service URIs
	private static final String PROTOCOL = "http://";
	private static final String HOST = "127.0.0.1";
	private static final String PORT = "8080";
	private static final String PAYMENT_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/DummyPayment/psdummy/";
	private static final String FUNDING_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/DummyFunding/fsdummy/";
	private static final String RESEARCHHUB_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/DummyResearch/rsdummy/";
	private static final String MARKETPLACE_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/DummyMarket/mpdummy/";
	
	//JWT Service Token
	private static final String SERVICE_TOKEN_USR= "SVC eyJraWQiOiJKV0sxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLmF1dGgiLCJqdGkiOiJSdFVzbUFpUDhLTERjamVsbGtUQ2lnIiwiaWF0IjoxNjE4MzU4MzI5LCJuYmYiOjE2MTgzNTgyMDksInN1YiI6ImdhZGdldGJhZGdldC5hdXRoLnVzZXIiLCJ1c2VybmFtZSI6InVzZXIiLCJ1c2VyX2lkIjoiMSIsInJvbGUiOiJVU1IifQ.bDWNTZfEN7xwO9g1xf6BzYB1G22xnNDSaSMjxbK-MrqJJyyjqvfC47V-nNBLPKLzQdBVtDW6sHTtfU7SYgw5kJ8lrS4iin-PkzfDmOJR3I0eBDN5idRW0D0OzmRpkjeZDHef3LSatCl7S1ZXq84Y43QK9QSXIHQcOHFL2JA5VOgB8PrhCEYIrZSPG7da1sF6f2uTvhK4m_MZVQAdzdMCC0hq50Zcm5843qPjIY0Oq93XjSzz4H_OjdZS2g0nrSa5GAhYxvam-E7WQJpgm76cpzMcU7VSgumCdO4TyBW-sINpDn7atEVk5v7yzietJG3v1UNDg0or0ms9GNT08M7jNQ";
	
	private Client client = null;
	private WebResource webRes = null;

	/**
	 * This method is used to establish service-to-service communication with the payment service.
	 * 
	 * @param absolutePath	absolute path of the service end-point is given here
	 * @return				returns the original response of the payment service as a JSON object.  
	 */
	public JsonObject paymentIntercomms(String absolutePath)
	{
		client = Client.create();
		webRes = client.resource(PAYMENT_SERVICE_URI+absolutePath);
		String output = webRes.header("Authorization", SERVICE_TOKEN_USR)
									.get(String.class);
		
		JsonObject JSONoutput = new JsonParser().parse(output).getAsJsonObject();
		return JSONoutput;
	}

	/**
	 * This method is used to establish service-to-service communication with the funding service.
	 * 
	 * @param absolutePath	absolute path of the service end-point is given here
	 * @return				returns the original response of the funding service as a JSON object.  
	 */
	public JsonObject fundingIntercomms(String absolutePath)
	{
		client = Client.create();
		webRes = client.resource(FUNDING_SERVICE_URI+absolutePath);
		String output = webRes.header("Authorization", SERVICE_TOKEN_USR)
									.get(String.class);
		
		JsonObject JSONoutput = new JsonParser().parse(output).getAsJsonObject();
		return JSONoutput;
	}

	/**
	 * This method is used to establish service-to-service communication with the research-hub service.
	 * 
	 * @param absolutePath	absolute path of the service end-point is given here
	 * @return				returns the original response of the research-hub service as a JSON object.  
	 */
	public JsonObject researchHubIntercomms(String absolutePath)
	{
		client = Client.create();
		webRes = client.resource(RESEARCHHUB_SERVICE_URI+absolutePath);
		String output = webRes.header("Authorization", SERVICE_TOKEN_USR)
									.get(String.class);
		
		JsonObject JSONoutput = new JsonParser().parse(output).getAsJsonObject();
		return JSONoutput;
	}

	/**
	 * This method is used to establish service-to-service communication with the marketplace service.
	 * 
	 * @param absolutePath	absolute path of the service end-point is given here
	 * @return				returns the original response of the marketplace service as a JSON object.  
	 */
	public JsonObject marketplaceIntercomms(String absolutePath)
	{
		client = Client.create();
		webRes = client.resource(MARKETPLACE_SERVICE_URI+absolutePath);
		String output = webRes.header("Authorization", SERVICE_TOKEN_USR)
									.get(String.class);
		
		JsonObject JSONoutput = new JsonParser().parse(output).getAsJsonObject();
		return JSONoutput;
	}
}
