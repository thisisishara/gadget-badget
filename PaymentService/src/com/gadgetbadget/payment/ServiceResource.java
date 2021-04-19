package com.gadgetbadget.payment;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.gadgetbadget.payment.model.ServiceCharge;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/service-charges")
public class ServiceResource {
	ServiceCharge service = new ServiceCharge();
	
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateServiceCharges(String serviceJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		try {

			// Authorize only ADMINs & FNMGRs
			if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("FNMGR"))) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
				return result.toString();
			}

			JsonObject serviceJSON_parsed = new JsonParser().parse(serviceJSON).getAsJsonObject();

				return (service.updateServiceCharge(
						serviceJSON_parsed.get("service_code").getAsString(), 
						serviceJSON_parsed.get("service_charge_rate").getAsFloat(), 
						serviceJSON_parsed.get("effective_tax_rate").getAsFloat()).toString());

			
		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex);
		}

		return result.toString();
	}
}
