package com.gadgetbadget.funding;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;


import com.gadgetbadget.funding.model.Fund;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/funds")

public class FundResource {
	Fund fund = new Fund();

	//Funds End-points
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String readFunds(@Context SecurityContext securityContext, @QueryParam("funderid") String funder_id, @QueryParam("researchid") String research_id, @QueryParam("summarized") boolean isSummarized) {
		JsonObject result = null;

		// Authorize only ADMINs, Funders and User service, RHB service
		if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("USR") || securityContext.isUserInRole("FUNDR") || securityContext.isUserInRole("FNMGR") || securityContext.isUserInRole("RHB"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "ERROR");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Authenticated user id
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		try {

			if(securityContext.isUserInRole("USR")) {
				if(funder_id != null && isSummarized == true) {
					return fund.readFundSummeryByFunderId(funder_id).toString();
				}

				result = new JsonObject();
				result.addProperty("STATUS", "PROHIBITED");
				result.addProperty("MESSAGE","You are NOT Allowed to retrieve summarized fund details.");
				return result.toString();

			}

			if(securityContext.isUserInRole("RHB")) {
				if(research_id != null) {
					return fund.readFundsByResearchId(research_id).toString();
				}

				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid URL Format. Required query parameters are missing.");
				return result.toString();

			}

			if(!(securityContext.isUserInRole("ADMIN"))) {
				return fund.readFundSummeryByFunderId(current_user_id).toString();
			}

			return fund.readFundsByResearchId(null).toString();

		} catch(Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex);
			return result.toString();
		}
	}


}
