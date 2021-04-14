package com.gadgetbadget.user.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class InterServiceCommHandler {
	//List of hard-coded Service URIs
	private static final String PROTOCOL = "http://";
	private static final String HOST = "127.0.0.1";
	private static final String PORT = "8080";
	private static final String PAYMENT_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/PaymentService/paymentservice/";
	private static final String FUNDING_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/FundingService/fundingservice/";
	private static final String RESEARCHHUB_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/ResearchHubService/researchhubservice/";
	private static final String MARKETPLACE_SERVICE_URI = PROTOCOL + HOST + ":" + PORT + "/MarketplaceService/marketplaceservice/";
	
	//List of hard-coded verified service tokens
	private static final String SERVICE_TOKEN_USR= "SVC eyJraWQiOiJKV0sxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLmF1dGgiLCJqdGkiOiJSdFVzbUFpUDhLTERjamVsbGtUQ2lnIiwiaWF0IjoxNjE4MzU4MzI5LCJuYmYiOjE2MTgzNTgyMDksInN1YiI6ImdhZGdldGJhZGdldC5hdXRoLnVzZXIiLCJ1c2VybmFtZSI6InVzZXIiLCJ1c2VyX2lkIjoiMSIsInJvbGUiOiJVU1IifQ.bDWNTZfEN7xwO9g1xf6BzYB1G22xnNDSaSMjxbK-MrqJJyyjqvfC47V-nNBLPKLzQdBVtDW6sHTtfU7SYgw5kJ8lrS4iin-PkzfDmOJR3I0eBDN5idRW0D0OzmRpkjeZDHef3LSatCl7S1ZXq84Y43QK9QSXIHQcOHFL2JA5VOgB8PrhCEYIrZSPG7da1sF6f2uTvhK4m_MZVQAdzdMCC0hq50Zcm5843qPjIY0Oq93XjSzz4H_OjdZS2g0nrSa5GAhYxvam-E7WQJpgm76cpzMcU7VSgumCdO4TyBW-sINpDn7atEVk5v7yzietJG3v1UNDg0or0ms9GNT08M7jNQ";
	private static final String SERVICE_TOKEN_PYT= "SVC eyJraWQiOiJKV0sxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLmF1dGgiLCJqdGkiOiItSTE4RjdqNk16aUo4QlF6SEh6QTRRIiwiaWF0IjoxNjE4MzU4MTIxLCJuYmYiOjE2MTgzNTgwMDEsInN1YiI6ImdhZGdldGJhZGdldC5hdXRoLnRoaXNpc2lzaGFyYXBheW1lbnRwYXltZW50IiwidXNlcm5hbWUiOiJwYXltZW50IiwidXNlcl9pZCI6IjIiLCJyb2xlIjoiUFlUIn0.nlpYoMiuxNK_iTOX8HPyFtBs-EDEJE282NHFihNaKOtmeqmdLspcEYrNcu53JtxABDixPPTciTU-M7JtO2MY_3PXHOsJ7tIcrdA_tgZ1yHUKSeLCYyn3MyxbfgWdoKuf2iFHZ3R8J7Hecv0ahP8LS2HdXroQ75CyI1q5vb4x7tulIkhJgEytN2F07dHoo7ZVtPgBluxLQeW0cv2AYYIf8ItkLw8iOB3aLgMj8jBqxJ5C2ttIRzoIicWrToGu8YiYguzWdEG3xM_MzzsChHn4NTmbqXZlny1lXHlZxUDgm9ChATjiGH52qd_MCNjgHIXp43BtWXzH7TImUj_OV8R7VQ";
	private static final String SERVICE_TOKEN_RHB= "SVC eyJraWQiOiJKV0sxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLmF1dGgiLCJqdGkiOiI0aEo1NlhpVHB3RlRKZHZmTVR5czlBIiwiaWF0IjoxNjE4MzU4NTE5LCJuYmYiOjE2MTgzNTgzOTksInN1YiI6ImdhZGdldGJhZGdldC5hdXRoLnJlc2VhcmNoaHViIiwidXNlcm5hbWUiOiJyZXNlYXJjaGh1YiIsInVzZXJfaWQiOiIzIiwicm9sZSI6IlJIQiJ9.YebCgB6M9ETwOcIKa-yJ-fH1YBIuNQMubqZtM3EqoQZCeLzSSdc7fXD2WAQdRjwmVZrFIOw9zNb3OL8xvDyoCnSASXkJ4OrdA9Naga3XoA7DanwqoTup3fPPTGn4U9BV-doy5ZLQFRwu6tBo--R-BLhk2fGk_69EGzR-uYkjylgk9ERSrGKlmJJXveze4YIuWmGLVCQrfTS-ZB7ugIznTC__82NPiiAECpCufY-qqzaaBjhiTBBg0pswgjMEYDZAArv1rwzMrMIyGausyPEJPLaLATKNrXdnBJfCan34MEXd0g4J4lT4xBR7gSGLKUYGJfG2pJCUzPMtgVuQI10OMw";
	private static final String SERVICE_TOKEN_MKT= "SVC eyJraWQiOiJKV0sxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLmF1dGgiLCJqdGkiOiJBZUN2M29NdFhnTHVJWEFmZGQxb19nIiwiaWF0IjoxNjE4MzU4NjA1LCJuYmYiOjE2MTgzNTg0ODUsInN1YiI6ImdhZGdldGJhZGdldC5hdXRoLm1hcmtldHBsYWNlIiwidXNlcm5hbWUiOiJtYXJrZXRwbGFjZSIsInVzZXJfaWQiOiI0Iiwicm9sZSI6Ik1LVCJ9.m9E-hOd1sWfPMOkpyScTcgx_ZPE3kFY1pPfvRrcpXqYgIK3_B1laQNh_PZzP9bFvW8Yd3JPjFn-VhWW_q2gL9kjAovOBk6MF14-uWvkd2pEZJU3s0g1yTJxyvrtEaLl3A4Kc3TKNA4VqmZ6A2hUYkkoBQja8lNXwYLpyvajkjwm_iEuBhsUrSQRaERehxEEYdrAJu6hwZnD8DutbZPSGAuc7jC8nKmY2jgsDJYM6rZSefNgRPxKXVXTVTA-QFn-c_r4vDF7Txn8fXFLSefY2TWkBzPX7K-Su9SQLrdBI2_YxSFlHf9bE9ISK4QMO8CwGyAbwF9P0H9ozedvS3eFI5Q";
	private static final String SERVICE_TOKEN_FND= "SVC eyJraWQiOiJKV0sxIiwiYWxnIjoiUlMyNTYifQ.eyJpc3MiOiJnYWRnZXRiYWRnZXQudXNlci5zZWN1cml0eS5KV1RIYW5kbGVyIiwiYXVkIjoiZ2FkZ2V0YmFkZ2V0LndlYnNlcnZpY2VzLmF1dGgiLCJqdGkiOiJrcjhyNFBOMFNCaDhjN0FBWHByWEt3IiwiaWF0IjoxNjE4MzU4NjgwLCJuYmYiOjE2MTgzNTg1NjAsInN1YiI6ImdhZGdldGJhZGdldC5hdXRoLmZ1bmRpbmciLCJ1c2VybmFtZSI6ImZ1bmRpbmciLCJ1c2VyX2lkIjoiNSIsInJvbGUiOiJGTkQifQ.HretsmyH4L-TGCTENLLfVBWd7DBA3teirHdujCO9KEkhDwkHPyLfHV8vUm2OAC5CV4mYHwJX1mAsXrf-aaYG5FtKBUOTuJKOdovs_ZxfRWxXF3Ej6SihGeSxiiTmd7A8B02-KjDdif_x06wk0G9Wwgso_ejtYRoDNOp3MVgGkG7K5XCNf4pHIofxsFkix92vEWcLrVvRULD3IS_93_fF7_hFJPrSA99YXflUVq1sui5XIJdmndR3okbDOUt09cv8Xhs3IqkXoIAf_tnN1puE6DQIDdLrotOXG4OXC4U0fO1qbVLPxAiPUfvwtNxVC7fkerGknseFIeWeTcj3R5xHMA";	
	
	private Client client = null;
	private WebResource webRes = null;

	//Implementing service-to-service communications
	//PAYMENT
	public JsonObject paymentIntercomms(String abstractPath)
	{
		client = Client.create();
		webRes = client.resource("http://localhost:8080/DummyPayment/paymentservicedummy/"+abstractPath);
		String output = webRes.header("Authorization", SERVICE_TOKEN_USR)
									.get(String.class);
		
		JsonObject JSONoutput = new JsonParser().parse(output).getAsJsonObject();
		return JSONoutput;
	}

	//FUNDING
	public String fundingIntercomms(String abstractPath)
	{
		client = Client.create();
		webRes = client.resource(PAYMENT_SERVICE_URI+abstractPath);
		JsonObject output = webRes.get(JsonObject.class);
		return "Response of Payment Server: " + output;
	}

	//RESEARCH-HUB
	public String researchHubIntercomms(String abstractPath)
	{
		client = Client.create();
		webRes = client.resource(PAYMENT_SERVICE_URI+abstractPath);
		JsonObject output = webRes.get(JsonObject.class);
		return "Response of Payment Server: " + output;
	}

	//MARKETPLACE
	public String marketplaceIntercomms(String abstractPath)
	{
		client = Client.create();
		webRes = client.resource(PAYMENT_SERVICE_URI+abstractPath);
		JsonObject output = webRes.get(JsonObject.class);
		return "Response of Payment Server: " + output;
	}
}
