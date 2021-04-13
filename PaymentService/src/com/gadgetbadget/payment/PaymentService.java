package com.gadgetbadget.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gadgetbadget.payment.model.Payment;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/payments")
public class PaymentService {
	Payment payment = new Payment();
	
	//Payment End-points
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String readPayments() {
		return payment.readPayment().toString();
	}

	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertPayment(String paymentJSON)
	{
		JsonObject result = null;

		try {

			JsonObject paymentJSON_parsed = new JsonParser().parse(paymentJSON).getAsJsonObject();

			if(!paymentJSON_parsed.has("payments")) {
				return (payment.insertPayment(
						paymentJSON_parsed.get("consumer_id").getAsString(), 
						paymentJSON_parsed.get("product_id").getAsString(), 
						paymentJSON_parsed.get("payment_amount").getAsFloat()).toString());
			} else if (!paymentJSON_parsed.get("payments").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = paymentJSON_parsed.get("payments").getAsJsonArray().size();

			for (JsonElement paymentElem : paymentJSON_parsed.get("payments").getAsJsonArray()) {
				JsonObject paymentObj = paymentElem.getAsJsonObject();
				JsonObject response = (payment.insertPayment(
						paymentObj.get("consumer_id").getAsString(), 
						paymentObj.get("product_id").getAsString(), 
						paymentObj.get("payment_amount").getAsFloat()));
						

				if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", insertCount + " Payment were Made successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + insertCount +" Payment were Made. Making failed for "+ (elemCount-insertCount) + " Payments.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


}
