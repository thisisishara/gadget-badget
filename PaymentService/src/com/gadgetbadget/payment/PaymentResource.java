package com.gadgetbadget.payment;

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

import com.gadgetbadget.payment.model.Payment;
import com.gadgetbadget.payment.util.ValidationHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/payments")
public class PaymentResource {
	Payment payment = new Payment();

	//Payment End-points
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String readPayments(@Context SecurityContext securityContext, @QueryParam("consumerid") String consumer_id, @QueryParam("summarized") boolean isSummarized, @QueryParam("productid") String product_id) {
		JsonObject result = null;

		// Authorize only ADMINs, Consumers, Researchers, and UserService(USR), MKT Service
		if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("USR") || securityContext.isUserInRole("CNSMR") || securityContext.isUserInRole("FNMGR") || securityContext.isUserInRole("MKT"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "ERROR");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Authenticated user id
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		try {
			//If User Service -> return summary after checking query parameters
			if(securityContext.isUserInRole("USR")) {
				if(consumer_id != null && isSummarized == true) {
					return payment.readPaymentSummaryByConsumerId(consumer_id).toString();
				}

				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid URL Format. Required query parameters are missing.");
				return result.toString();

			}

			if(securityContext.isUserInRole("MKT")) {
				if(product_id != null) {
					return payment.readPaymentsByProductId(product_id).toString();
				}

				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid URL Format. Required query parameters are missing.");
				return result.toString();

			}

			if(securityContext.isUserInRole("CNSMR")) {
				return payment.readPayments(current_user_id).toString();
			}

			return payment.readPayments(null).toString();

		} catch(Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex);
			return result.toString();
		}
	}

	@GET
	@Path("/{payment_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readPayment(@Context SecurityContext securityContext, @PathParam("payment_id") String payment_id) {

		JsonObject result = null;

		// Authorize only ADMINs, CNSMRs
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("CNSMR") || securityContext.isUserInRole("FNMGR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "ERROR");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if its a Single ID or Multiple IDs
		if(!payment_id.contains(",")) {
			// Allow retrieving only if the IDs are matched for NON ADMINs
			if(securityContext.isUserInRole("CNSMR")) {
				return payment.getPayment(current_user_id, payment_id).toString();
			}

			return payment.getPayment(null, payment_id).toString();
		}

		//if multiple id s
		String[] ids = payment_id.split(",");

		int readCount = 0;
		int elemCount = ids.length;
		JsonArray resultArray = new JsonArray();

		for (String id : ids) {
			JsonObject response = null;
			if(securityContext.isUserInRole("CNSMR")) {
				response = payment.getPayment(current_user_id, id);
			} else {
				response = payment.getPayment(null, id);
			}

			if (!response.has("MESSAGE")) {
				readCount++;
				resultArray.add(response);
			} 
		}

		result = new JsonObject();
		result.add("payments", resultArray);

		if(readCount == elemCount) {
			result.addProperty("STATUS", "SUCCESSFUL");
			result.addProperty("MESSAGE", readCount + " Payment were retrieved successfully.");

		} else {
			result.addProperty("STATUS", "UNSUCCESSFUL");
			result.addProperty("MESSAGE", "Only " + readCount +" Payment were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Payment(s).");
		}

		return result.toString();
	}

	@GET
	@Path("/profit")
	@Produces(MediaType.APPLICATION_JSON)
	public String readPayment(@Context SecurityContext securityContext) {

		JsonObject result = null;

		// Authorize only ADMINs, CNSMRs
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("FNMGR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "ERROR");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		try {
			return payment.calculateProfit().toString();
		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
			return result.toString();
		}
	}


	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertPayment(String paymentJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & EMPLYs
			if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("CNSMR"))) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
				return result.toString();
			}

			JsonObject paymentJSON_parsed = new JsonParser().parse(paymentJSON).getAsJsonObject();

			if(!paymentJSON_parsed.has("payments")) {

				//verify if the given id is correct
				if(! new ValidationHandler().validateUserId(paymentJSON_parsed.get("consumer_id").getAsString(), "CNSMR")) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Consumer ID given is not in the correct ID format.");
					return result.toString();
				}

				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! paymentJSON_parsed.get("consumer_id").getAsString().equals(current_user_id)){
						result = new JsonObject();
						result.addProperty("STATUS", "PROHIBITED");
						result.addProperty("MESSAGE","You are NOT Allowed to make payments on behalf of other consumers.");
						return result.toString();
					}
				}

				return (payment.insertPayment(
						paymentJSON_parsed.get("consumer_id").getAsString(), 
						paymentJSON_parsed.get("product_id").getAsString(), 
						paymentJSON_parsed.get("payment_amount").getAsFloat(), 
						paymentJSON_parsed.get("creditcard_no").getAsString()).toString());

			} else if (!paymentJSON_parsed.get("payments").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = paymentJSON_parsed.get("payments").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement paymentElem : paymentJSON_parsed.get("payments").getAsJsonArray()) {
				JsonObject paymentObj = paymentElem.getAsJsonObject();

				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! paymentObj.get("consumer_id").getAsString().equals(current_user_id)){
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("id_mismatch", "Your ID does not match with the consumer ID given in the payload. You are not allowed to make payments on behalf of other consumers.");
						errorsArr.add(errorElem);
						continue;
					}
				}

				if(! new ValidationHandler().validateUserId(paymentObj.get("consumer_id").getAsString(), "CNSMR")) {
					JsonObject errorElem = new JsonObject();
					errorElem.addProperty("id_mismatch", "Consumer ID given is not in the correct ID format.");
					errorsArr.add(errorElem);
					continue;
				}

				JsonObject response = (payment.insertPayment(
						paymentObj.get("consumer_id").getAsString(), 
						paymentObj.get("product_id").getAsString(), 
						paymentObj.get("payment_amount").getAsFloat(),
						paymentObj.get("creditcard_no").getAsString()));


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
				result.addProperty("MESSAGE", insertCount + " Payment were Made successfully.");

			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + insertCount +" Payment were Made. Making failed for "+ (elemCount-insertCount) + " Payment(s).");
				result.add("insertion_errors", errorsArr);
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex);
		}

		return result.toString();
	}

	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updatePayment(String paymentJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & EMPLYs
			if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("CNSMR"))) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
				return result.toString();
			}

			JsonObject paymentJSON_parsed = new JsonParser().parse(paymentJSON).getAsJsonObject();

			if(!paymentJSON_parsed.has("payments")) {
				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! paymentJSON_parsed.get("consumer_id").getAsString().equals(current_user_id)){
						result = new JsonObject();
						result.addProperty("STATUS", "PROHIBITED");
						result.addProperty("MESSAGE","You are NOT Allowed to make payments on behalf of other consumers.");
						return result.toString();
					}
				}

				return (payment.updatePayment(
						paymentJSON_parsed.get("payment_id").getAsString(),
						paymentJSON_parsed.get("consumer_id").getAsString(), 
						paymentJSON_parsed.get("product_id").getAsString(), 
						paymentJSON_parsed.get("payment_amount").getAsFloat(),
						paymentJSON_parsed.get("creditcard_no").getAsString()).toString()); 

			} else if (!paymentJSON_parsed.get("payments").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = paymentJSON_parsed.get("payments").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement paymentElem : paymentJSON_parsed.get("payments").getAsJsonArray()) {
				JsonObject paymentObj = paymentElem.getAsJsonObject();

				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! paymentObj.get("consumer_id").getAsString().equals(current_user_id)){
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("id_mismatch", "Your ID does not match with the consumer ID given in the payload. You are not allowed to update payments on behalf of other consumers.");
						errorsArr.add(errorElem);
						continue;
					}
				}

				JsonObject response = (payment.updatePayment(
						paymentObj.get("payment_id").getAsString(),
						paymentObj.get("consumer_id").getAsString(), 
						paymentObj.get("product_id").getAsString(), 
						paymentObj.get("payment_amount").getAsFloat(),
						paymentObj.get("creditcard_no").getAsString()));

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
				result.addProperty("MESSAGE", updateCount + " Payments were updated successfully.");

			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + updateCount +" Payments were Updated. Updating failed for "+ (elemCount-updateCount) + " Payment(s).");
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
	public String deletePayment(String paymentJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & EMPLYs
			if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("CNSMR"))) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
				return result.toString();
			}

			JsonObject paymentJSON_parsed = new JsonParser().parse(paymentJSON).getAsJsonObject();

			if(!paymentJSON_parsed.has("payments")) {
				if(! (securityContext.isUserInRole("ADMIN"))) {
					return (payment.deletePayment(
							current_user_id,
							paymentJSON_parsed.get("payment_id").getAsString())).toString();
				}

				return (payment.deletePayment(
						null,
						paymentJSON_parsed.get("payment_id").getAsString())).toString();

			} else if (!paymentJSON_parsed.get("payments").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = paymentJSON_parsed.get("payments").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement paymentElem : paymentJSON_parsed.get("payments").getAsJsonArray()) {
				JsonObject paymentObj = paymentElem.getAsJsonObject();
				JsonObject response = null;

				if(! (securityContext.isUserInRole("ADMIN"))) {
					response = (payment.deletePayment(current_user_id, paymentObj.get("payment_id").getAsString()));
				} else {
					response = (payment.deletePayment(null, paymentObj.get("payment_id").getAsString()));
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
				result.addProperty("MESSAGE", deleteCount + " Payments were deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + deleteCount +" Payments were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Payments.");
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
