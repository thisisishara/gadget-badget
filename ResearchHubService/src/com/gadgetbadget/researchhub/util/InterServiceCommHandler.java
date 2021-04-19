package com.gadgetbadget.researchhub.util;



import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class InterServiceCommHandler {
	//List of hard-coded Service URIs
	private static final String PROTOCOL = "http://";
	private static final String HOST = "127.0.0.1";
	private static final String PORT = "8081";
	private static final String FUNDING_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/FundingService/fundingservice/";
	
	//JWT Service Token
	private static final String SERVICE_TOKEN_RHB= "SVC eyJraWQiOiJKV0syIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLnNlcnZpY2VhdXRoIiwianRpIjoia0ZCZU9zaGZSYUVDbXY4TWpkSl9tUSIsImlhdCI6MTYxODUzOTk1MiwibmJmIjoxNjE4NTM5ODMyLCJzdWIiOiJnYWRnZXRiYWRnZXQuYXV0aC5jb20uZ2FkZ2V0YmFkZ2V0LnJlc2VhcmNoaHViIiwidXNlcm5hbWUiOiJjb20uZ2FkZ2V0YmFkZ2V0LnJlc2VhcmNoaHViIiwidXNlcl9pZCI6IjAwMyIsInJvbGUiOiJSSEIifQ.o99mVW0xHSOiD-CxetyXCYwUk4ps_xCT5mCTsE7fVgnxZG3M8Ba8_fybzgohW6I3xONc-ivQhr43KeDqMD5llv1X5wcbPFNM6D4JjcOs20prds5ETu_T_GAHEYIn2yAkoMoXiCKr7LmFp3ABkFwPJJ_XQARQSptwmOT00QHY27ndqetO9Xj7UuhRteJzSObozSsSockdqdZgQXQhbAhyCqED4l1OPTWX81-nM5Ce20b4vr-rSbcG1fUKeoYQCouAY4C_ZuLzLCrVqhy7RuHuCYlo1wcIEbhPoZAhtL8sJni_QketgqBiZDAYewp9pYYKi1TYAFArzJkkBinSBOxP8g";
	
	private Client client = null;
	private WebResource webRes = null;

	public JsonObject fundingIntercomms(String absolutePath)
	{
		client = Client.create();
		webRes = client.resource(FUNDING_SERVICE_URI+absolutePath);
		String output = webRes.header("Authorization", SERVICE_TOKEN_RHB)
									.get(String.class);
		
		JsonObject JSONoutput = new JsonParser().parse(output).getAsJsonObject();
		return JSONoutput;
	}
}
