package com.gadgetbadget.marketplace.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class InterServiceCommHandler {
	//List of hard-coded Service URIs
	private static final String PROTOCOL = "http://";
	private static final String HOST = "127.0.0.1";
	private static final String PORT = "8081";
	private static final String PAYMENT_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/PaymentService/paymentservice/";
	
	//JWT Service Token
	private static final String SERVICE_TOKEN_MKT= "SVC eyJraWQiOiJKV0syIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLnNlcnZpY2VhdXRoIiwianRpIjoiWFNGdEItcFFaSjRVek1oSThWbXdWZyIsImlhdCI6MTYxODUzOTk5MiwibmJmIjoxNjE4NTM5ODcyLCJzdWIiOiJnYWRnZXRiYWRnZXQuYXV0aC5jb20uZ2FkZ2V0YmFkZ2V0Lm1hcmtldHBsYWNlIiwidXNlcm5hbWUiOiJjb20uZ2FkZ2V0YmFkZ2V0Lm1hcmtldHBsYWNlIiwidXNlcl9pZCI6IjAwNCIsInJvbGUiOiJNS1QifQ.Bj3nqqEw3vlQJeyjA1VgY6jK9DNV_Wypu41v4HSRKb_0fulxNvXdPBQkqxtmSSK47AGS2k-6qbG-iB0cYTVP09MyNJBQaLWVXOHFJnlfrMzRqsYSM8Uki4_1AIIS7agMXOfrzOAaHzXQkkakLug6EknXmym9h2AlAsjAB9qs_DT1Ay_v-yM30sRAGdE0PcbPgrPIKq5xPWb-DMr_HWRKY8Wdopid-vc0C1gTn7zhiMUyf7B6_gQG8iKN-Ozz-zHG9XltUMZEFfb-MatouVDQcXrMZo8TZPzAfOlxKUF7AlnjAPWMZyPTn7e2zEbtxJ4l-6J4sym3e-CZvwOmmSTnmQ";
	
	private Client client = null;
	private WebResource webRes = null;

	public JsonObject paymentIntercomms(String absolutePath)
	{
		client = Client.create();
		webRes = client.resource(PAYMENT_SERVICE_URI+absolutePath);
		String output = webRes.header("Authorization", SERVICE_TOKEN_MKT)
									.get(String.class);
		
		JsonObject JSONoutput = new JsonParser().parse(output).getAsJsonObject();
		return JSONoutput;
	}
}
