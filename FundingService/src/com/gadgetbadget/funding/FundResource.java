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

				if(securityContext.isUserInRole("FUNDR")) {
					return fund.readFunds(current_user_id).toString();
				}

				return fund.readFunds(null).toString();

			} catch(Exception ex) {
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex);
				return result.toString();
			}
		}

		
		@GET
		@Path("/profit")
		@Produces(MediaType.APPLICATION_JSON)
		public String getProfit(@Context SecurityContext securityContext) {

			JsonObject result = null;

			// Authorize only ADMINs, CNSMRs
			if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("FNMGR"))) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
				return result.toString();
			}

			try {
				return fund.calculateFundProfit().toString();
			} catch (Exception ex){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
				return result.toString();
			}
		}

		@GET
		@Path("/{fund_id}")
		@Produces(MediaType.APPLICATION_JSON)
		public String readFund(@Context SecurityContext securityContext, @PathParam("fund_id") String fund_id) {

			JsonObject result = null;

			// Authorize only ADMINs, FUNDRs
			if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("FUNDR") || securityContext.isUserInRole("FNMGR"))) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
				return result.toString();
			}

			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Check if its a Single ID or Multiple IDs
			if(!fund_id.contains(",")) {
				// Allow retrieving only if the IDs are matched for NON ADMINs
				if(securityContext.isUserInRole("FUNDR")) {
					return fund.readFund(current_user_id, fund_id).toString();
				}

				return fund.readFund(null, fund_id).toString();
			}

			//if multiple id s
			String[] ids = fund_id.split(",");

			int readCount = 0;
			int elemCount = ids.length;
			JsonArray resultArray = new JsonArray();

			for (String id : ids) {
				JsonObject response = null;
				if(securityContext.isUserInRole("FUNDR")) {
					response = fund.readFund(current_user_id, id);
				} else {
					response = fund.readFund(null, id);
				}

				if (!response.has("MESSAGE")) {
					readCount++;
					resultArray.add(response);
				} 
			}

			result = new JsonObject();
			result.add("funds", resultArray);

			if(readCount == elemCount) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", readCount + " Funds were retrieved successfully.");

			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + readCount +" Funds were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Funds.");
			}

			return result.toString();
		}
		
		@POST
		@Path("/")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String insertFund(String fundJSON, @Context SecurityContext securityContext)
		{
			JsonObject result = null;

			try {
				// Get Current User's ID
				String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

				// Authorize only ADMINs & EMPLYs
				if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("FUNDR"))) {
					result = new JsonObject();
					result.addProperty("STATUS", "UNAUTHORIZED");
					result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
					return result.toString();
				}

				JsonObject fundJSON_parsed = new JsonParser().parse(fundJSON).getAsJsonObject();

				if(!fundJSON_parsed.has("funds")) {
					if(! (securityContext.isUserInRole("ADMIN"))) {
						if (! fundJSON_parsed.get("funder_id").getAsString().equals(current_user_id)){
							result = new JsonObject();
							result.addProperty("STATUS", "PROHIBITED");
							result.addProperty("MESSAGE","You are NOT Allowed to place funds on behalf of other funders.");
							return result.toString();
						}
					}

					return (fund.insertFund(fundJSON_parsed.get("funder_id").getAsString(), fundJSON_parsed.get("research_id").getAsString(), fundJSON_parsed.get("funded_amount").getAsFloat(), fundJSON_parsed.get("creditcard_no").getAsString()).toString());

				} else if (!fundJSON_parsed.get("funds").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}

				int insertCount = 0;
				int elemCount = fundJSON_parsed.get("funds").getAsJsonArray().size();

				JsonArray errorsArr = new JsonArray();

				for (JsonElement fundElem : fundJSON_parsed.get("funds").getAsJsonArray()) {
					JsonObject fundObj = fundElem.getAsJsonObject();

					if(! (securityContext.isUserInRole("ADMIN"))) {
						if (! fundObj.get("funder_id").getAsString().equals(current_user_id)){
							JsonObject errorElem = new JsonObject();
							errorElem.addProperty("id_mismatch", "Your user id does not match with the funder id given in the payload. You are not allowed to place funds on behalf of other funders.");
							errorsArr.add(errorElem);
							continue;
						}
					}

					JsonObject response = (fund.insertFund(fundObj.get("funder_id").getAsString(), fundObj.get("research_id").getAsString(), fundObj.get("funded_amount").getAsFloat(), fundObj.get("creditcard_no").getAsString()));

					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					} else {
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("exception", response.get("MESSAGE").getAsString());
						errorsArr.add(errorElem);
					}
				}

				result = new JsonObject();

				if(insertCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Funds were placed successfully.");

				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" Funds were placed. failed to place "+ (elemCount-insertCount) + " Funds.");
					result.add("insertion_errors", errorsArr);
				}

			} catch (Exception ex){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
			}

			return result.toString();
		}

		@PUT
		@Path("/")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String updateFund(String fundJSON, @Context SecurityContext securityContext)
		{
			JsonObject result = null;

			try {
				// Get Current User's ID
				String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

				// Authorize only ADMINs & Funders
				if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("FUNDR"))) {
					result = new JsonObject();
					result.addProperty("STATUS", "UNAUTHORIZED");
					result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
					return result.toString();
				}

				JsonObject fundJSON_parsed = new JsonParser().parse(fundJSON).getAsJsonObject();

				if(!fundJSON_parsed.has("funds")) {
					if(! (securityContext.isUserInRole("ADMIN"))) {
						if (! fundJSON_parsed.get("funder_id").getAsString().equals(current_user_id)){
							result = new JsonObject();
							result.addProperty("STATUS", "PROHIBITED");
							result.addProperty("MESSAGE","You are NOT Allowed to place funds on behalf of other consumers.");
							return result.toString();
						}
					}

					return (fund.updateFund(fundJSON_parsed.get("fund_id").getAsString(), fundJSON_parsed.get("funder_id").getAsString(),  fundJSON_parsed.get("research_id").getAsString(),  fundJSON_parsed.get("funded_amount").getAsFloat(), fundJSON_parsed.get("creditcard_no").getAsString()).toString()); 

				} else if (!fundJSON_parsed.get("funds").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}

				int updateCount = 0;
				int elemCount = fundJSON_parsed.get("funds").getAsJsonArray().size();

				JsonArray errorsArr = new JsonArray();

				for (JsonElement fundElem : fundJSON_parsed.get("funds").getAsJsonArray()) {
					JsonObject fundObj = fundElem.getAsJsonObject();

					if(! (securityContext.isUserInRole("ADMIN"))) {
						if (! fundObj.get("funder_id").getAsString().equals(current_user_id)){
							JsonObject errorElem = new JsonObject();
							errorElem.addProperty("id_mismatch", " You are not allowed to update funds on behalf of other funders.");
							errorsArr.add(errorElem);
							continue;
						}
					}

					JsonObject response = (fund.updateFund(fundObj.get("fund_id").getAsString(), fundObj.get("funder_id").getAsString(), fundObj.get("research_id").getAsString(), fundObj.get("funded_amount").getAsFloat(), fundObj.get("creditcard_no").getAsString()));

					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						updateCount++;
					} else {
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("exception", response.get("MESSAGE").getAsString());
						errorsArr.add(errorElem);
					}
				}

				result = new JsonObject();

				if(updateCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", updateCount + " Funds were updated successfully.");

				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + updateCount +" Funds were Updated. Updating failed for "+ (elemCount-updateCount) + " Funds.");
					result.add("updating_errors", errorsArr);
				}

			} catch (Exception ex){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
			}

			return result.toString();
		}

		@DELETE
		@Path("/")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String deleteFund(String fundJSON, @Context SecurityContext securityContext)
		{
			JsonObject result = null;

			try {
				// Get Current User's ID
				String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

				// Authorize only ADMINs & EMPLYs
				if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("FUNDR"))) {
					result = new JsonObject();
					result.addProperty("STATUS", "UNAUTHORIZED");
					result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
					return result.toString();
				}

				JsonObject fundJSON_parsed = new JsonParser().parse(fundJSON).getAsJsonObject();

				if(!fundJSON_parsed.has("funds")) {
					if(! (securityContext.isUserInRole("ADMIN"))) {	
						if(! fundJSON_parsed.get("funder_id").getAsString().equals(current_user_id)) {
							result = new JsonObject();
							result.addProperty("STATUS", "PROHIBITED");
							result.addProperty("MESSAGE","You are NOT Allowed to delete products uploaded by others");
							return result.toString();
						}
						
						return (fund.deleteFund(current_user_id, fundJSON_parsed.get("fund_id").getAsString())).toString();
					}				
					return (fund.deleteFund(null, fundJSON_parsed.get("fund_id").getAsString())).toString();

				} else if (!fundJSON_parsed.get("funds").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}

				int deleteCount = 0;
				int elemCount = fundJSON_parsed.get("funds").getAsJsonArray().size();

				JsonArray errorsArr = new JsonArray();

				for (JsonElement fundElem : fundJSON_parsed.get("funds").getAsJsonArray()) {
					JsonObject fundObj = fundElem.getAsJsonObject();
					JsonObject response = null;
					
					if(! (securityContext.isUserInRole("ADMIN"))) {
						response = (fund.deleteFund(current_user_id, fundObj.get("fund_id").getAsString()));
					} else {
						response = (fund.deleteFund(null, fundObj.get("fund_id").getAsString()));
					}

					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						deleteCount++;
					} else {
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("exception", response.get("MESSAGE").getAsString());
						errorsArr.add(errorElem);
					}
				}

				result = new JsonObject();
				if(deleteCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", deleteCount + " Funds were deleted successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + deleteCount +" Funds were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Funds.");
					result.add("updating_errors", errorsArr);
				}

			} catch (Exception ex){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
			}

			return result.toString();
		}

	}
		
		

	


