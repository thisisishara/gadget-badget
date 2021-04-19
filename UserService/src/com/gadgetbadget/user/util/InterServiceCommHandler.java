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
	private static final String PORT = "8081";
	private static final String PAYMENT_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/PaymentService/paymentservice/";
	private static final String FUNDING_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/FundingService/fundingservice/";
	private static final String RESEARCHHUB_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/ResearchHubService/researchhubservice/";
	private static final String MARKETPLACE_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/MarketplaceService/marketplaceservice/";

	//JWT Service Token
	private static final String SERVICE_TOKEN_USR= "SVC eyJraWQiOiJKV0syIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLnNlcnZpY2VhdXRoIiwianRpIjoiLXM1Rm5BY25OU0tUd2RQRTg5V3d3QSIsImlhdCI6MTYxODUzOTcwNCwibmJmIjoxNjE4NTM5NTg0LCJzdWIiOiJnYWRnZXRiYWRnZXQuYXV0aC50aGlzaXNpc2hhcmFjb20uZ2FkZ2V0YmFkZ2V0LnVzZXIiLCJ1c2VybmFtZSI6ImNvbS5nYWRnZXRiYWRnZXQudXNlciIsInVzZXJfaWQiOiIwMDEiLCJyb2xlIjoiVVNSIn0.ly2Kibpz266CJLPWd1L2RHE9AOw3NKjVsswWPDDzOl3a1p53AxzNP4ebtXRhLW9kLNiq_c-6z0to9NXjEX8DmAGizE7LidIKjqzg6hXGBMUeg3ufYA-wYcymghbEWdCjZHuiXZ39nBcL8Grv4z-6J-ZJuLHOKTF_Wz-kC5sQ_JMc-MzNKTAoo1MZM_SGcJRoLkcpOT11Vqxq8rm-Gb2jWp79LDumQM8sP8yQRGEM1LZUMUj_210L3PU6kIEXR_xoTMLn7h3yr9RwWBBKZhmusMt9Ik4YEP_jiZlbiOabqHHpUHS8IY_3VEGBbbtInFieSWKVTvOjhd0jMxBVYDSrdw";
	//private static final String SERVICE_TOKEN_PYT= "SVC eyJraWQiOiJKV0syIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLnNlcnZpY2VhdXRoIiwianRpIjoiMXJDWmxPbnNLOC0wa004QmpHb09QQSIsImlhdCI6MTYxODUzOTgyOCwibmJmIjoxNjE4NTM5NzA4LCJzdWIiOiJnYWRnZXRiYWRnZXQuYXV0aC5jb20uZ2FkZ2V0YmFkZ2V0LnBheW1lbnQiLCJ1c2VybmFtZSI6ImNvbS5nYWRnZXRiYWRnZXQucGF5bWVudCIsInVzZXJfaWQiOiIwMDIiLCJyb2xlIjoiUFlUIn0.jyJqo-rx5xPCj11m6j4aRVguYbpULcGbbSPPdX0-poDAuQ8LhYX5FtjRhthfojJ6_Ens-QciM1y3rxWTgLVFqQzmpbsfGUA0FYurhGusw_0NtTgpBTak1xzYDZB70GpzWQk5UQBgUAH6oUn2jJcIfD6C5wjv-0oL8dsHLnXo7Mjt8j-5plM2q89n-sBoHSkEl9iztGeyOQU_NGycw437vURNZFk_X81LVjcSFxjAZukP0sIoeCEaN07IoR3QwnHf6yBsW9rIfHhmJ1rV_YF-zniAsgsi8CGpJGtoUsADglPxyDXOZeMqpMRBvn65dj1I11DPGHMwHGNr9u_JgWuC0w";
	//private static final String SERVICE_TOKEN_MKT= "SVC eyJraWQiOiJKV0syIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLnNlcnZpY2VhdXRoIiwianRpIjoiWFNGdEItcFFaSjRVek1oSThWbXdWZyIsImlhdCI6MTYxODUzOTk5MiwibmJmIjoxNjE4NTM5ODcyLCJzdWIiOiJnYWRnZXRiYWRnZXQuYXV0aC5jb20uZ2FkZ2V0YmFkZ2V0Lm1hcmtldHBsYWNlIiwidXNlcm5hbWUiOiJjb20uZ2FkZ2V0YmFkZ2V0Lm1hcmtldHBsYWNlIiwidXNlcl9pZCI6IjAwNCIsInJvbGUiOiJNS1QifQ.Bj3nqqEw3vlQJeyjA1VgY6jK9DNV_Wypu41v4HSRKb_0fulxNvXdPBQkqxtmSSK47AGS2k-6qbG-iB0cYTVP09MyNJBQaLWVXOHFJnlfrMzRqsYSM8Uki4_1AIIS7agMXOfrzOAaHzXQkkakLug6EknXmym9h2AlAsjAB9qs_DT1Ay_v-yM30sRAGdE0PcbPgrPIKq5xPWb-DMr_HWRKY8Wdopid-vc0C1gTn7zhiMUyf7B6_gQG8iKN-Ozz-zHG9XltUMZEFfb-MatouVDQcXrMZo8TZPzAfOlxKUF7AlnjAPWMZyPTn7e2zEbtxJ4l-6J4sym3e-CZvwOmmSTnmQ";
	//private static final String SERVICE_TOKEN_FND= "SVC eyJraWQiOiJKV0syIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLnNlcnZpY2VhdXRoIiwianRpIjoib0RVQjJPcXlnbjIxbkFTQ3loY1AwdyIsImlhdCI6MTYxODU0MDA1MywibmJmIjoxNjE4NTM5OTMzLCJzdWIiOiJnYWRnZXRiYWRnZXQuYXV0aC5jb20uZ2FkZ2V0YmFkZ2V0LmZ1bmRpbmciLCJ1c2VybmFtZSI6ImNvbS5nYWRnZXRiYWRnZXQuZnVuZGluZyIsInVzZXJfaWQiOiIwMDUiLCJyb2xlIjoiRk5EIn0.DZqFrrD2LnsDGb8DnE55JAoY44sdVJOVFF6BCA8QIEOfcfdkQTgHuWr3O9uaaT1sqXk_W7cl61bHsDvG9Y67oqInREP8ya1ULU7vbfAxLm8c3_H_cJb8t_87hLd3D43z45UvLz4wc_6Dlu_-h5oTDHbVzrEvGjdv-CTWABBsPaNApx5R_nStBAPKGNgiZ9dRNPqKvaKwQ4tjXwsdS8aVsQHMvjd0Jr6MsQcpIc3svxUESBAmBKvGSOBpqO-p9YU10kfGun3MgJtTWqbGvNZKAxHkN_fg8-2S6X-2q6a3ZwADBFMWYSB4q9rVtN_VL6TIfWzMZwhHx0iOvq8Q59NKZg";
	//private static final String SERVICE_TOKEN_RHB= "SVC eyJraWQiOiJKV0syIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLnNlcnZpY2VhdXRoIiwianRpIjoia0ZCZU9zaGZSYUVDbXY4TWpkSl9tUSIsImlhdCI6MTYxODUzOTk1MiwibmJmIjoxNjE4NTM5ODMyLCJzdWIiOiJnYWRnZXRiYWRnZXQuYXV0aC5jb20uZ2FkZ2V0YmFkZ2V0LnJlc2VhcmNoaHViIiwidXNlcm5hbWUiOiJjb20uZ2FkZ2V0YmFkZ2V0LnJlc2VhcmNoaHViIiwidXNlcl9pZCI6IjAwMyIsInJvbGUiOiJSSEIifQ.o99mVW0xHSOiD-CxetyXCYwUk4ps_xCT5mCTsE7fVgnxZG3M8Ba8_fybzgohW6I3xONc-ivQhr43KeDqMD5llv1X5wcbPFNM6D4JjcOs20prds5ETu_T_GAHEYIn2yAkoMoXiCKr7LmFp3ABkFwPJJ_XQARQSptwmOT00QHY27ndqetO9Xj7UuhRteJzSObozSsSockdqdZgQXQhbAhyCqED4l1OPTWX81-nM5Ce20b4vr-rSbcG1fUKeoYQCouAY4C_ZuLzLCrVqhy7RuHuCYlo1wcIEbhPoZAhtL8sJni_QketgqBiZDAYewp9pYYKi1TYAFArzJkkBinSBOxP8g";
	
	private Client client = null;
	private WebResource webRes = null;

	/**
	 * This method is used to establish service-to-service communication with the payment service. Currently configured 
	 * only to process HTTP GET requests but the method body can be extended to support POST, PUT, and DELETE HTTP methods
	 * as well since the method header accepts the type of the HTTP method along with the PAYLOAD in the type of a JSON Object. 
	 * 
	 * @param absolutePath 	absolute path of the service end-point is given here.
	 * @param httpMethod	specific HTTP Method type of the end-point specified.
	 * @param payload 		PAYLOAD to be attached to the request as a JSON Object. 
	 * @return				returns the original response of the funding service as a JSON object.  
	 */
	public JsonObject paymentIntercomms(String absolutePath, HttpMethod httpMethod, JsonObject payload)
	{
		client = Client.create();
		webRes = client.resource(PAYMENT_SERVICE_URI+absolutePath);

		if(httpMethod == HttpMethod.GET) {
			String output = webRes.header("Authorization", SERVICE_TOKEN_USR)
					.get(String.class);

			return new JsonParser().parse(output).getAsJsonObject();
		}
		return null;
	}

	/**
	 * This method is used to establish service-to-service communication with the funding service. Currently configured 
	 * only to process HTTP GET requests but the method body can be extended to support POST, PUT, and DELETE HTTP methods
	 * as well since the method header accepts the type of the HTTP method along with the PAYLOAD in the type of a JSON Object. 
	 * 
	 * @param absolutePath 	absolute path of the service end-point is given here.
	 * @param httpMethod	specific HTTP Method type of the end-point specified.
	 * @param payload 		PAYLOAD to be attached to the request as a JSON Object. 
	 * @return				returns the original response of the funding service as a JSON object.  
	 */
	public JsonObject fundingIntercomms(String absolutePath, HttpMethod httpMethod, JsonObject payload)
	{
		client = Client.create();
		webRes = client.resource(FUNDING_SERVICE_URI+absolutePath);
		
		if(httpMethod == HttpMethod.GET) {
			String output = webRes.header("Authorization", SERVICE_TOKEN_USR)
					.get(String.class);

			return new JsonParser().parse(output).getAsJsonObject();
		}
		return null;
	}

	/**
	 * This method is used to establish service-to-service communication with the research-hub service. Currently configured 
	 * only to process HTTP GET requests but the method body can be extended to support POST, PUT, and DELETE HTTP methods
	 * as well since the method header accepts the type of the HTTP method along with the PAYLOAD in the type of a JSON Object. 
	 * 
	 * @param absolutePath 	absolute path of the service end-point is given here.
	 * @param httpMethod	specific HTTP Method type of the end-point specified.
	 * @param payload 		PAYLOAD to be attached to the request as a JSON Object. 
	 * @return				returns the original response of the funding service as a JSON object.  
	 */
	public JsonObject researchHubIntercomms(String absolutePath, HttpMethod httpMethod, JsonObject payload)
	{
		client = Client.create();
		webRes = client.resource(RESEARCHHUB_SERVICE_URI+absolutePath);

		if(httpMethod == HttpMethod.GET) {
			String output = webRes.header("Authorization", SERVICE_TOKEN_USR)
					.get(String.class);

			return new JsonParser().parse(output).getAsJsonObject();
		}
		return null;
	}

	/**
	 * This method is used to establish service-to-service communication with the marketplace service. Currently configured 
	 * only to process HTTP GET requests but the method body can be extended to support POST, PUT, and DELETE HTTP methods
	 * as well since the method header accepts the type of the HTTP method along with the PAYLOAD in the type of a JSON Object. 
	 * 
	 * @param absolutePath 	absolute path of the service end-point is given here.
	 * @param httpMethod	specific HTTP Method type of the end-point specified.
	 * @param payload 		PAYLOAD to be attached to the request as a JSON Object. 
	 * @return				returns the original response of the funding service as a JSON object.  
	 */
	public JsonObject marketplaceIntercomms(String absolutePath, HttpMethod httpMethod, JsonObject payload)
	{
		client = Client.create();
		webRes = client.resource(MARKETPLACE_SERVICE_URI+absolutePath);

		if(httpMethod == HttpMethod.GET) {
			String output = webRes.header("Authorization", SERVICE_TOKEN_USR)
					.get(String.class);

			return new JsonParser().parse(output).getAsJsonObject();
		}
		return null;
	}
}
