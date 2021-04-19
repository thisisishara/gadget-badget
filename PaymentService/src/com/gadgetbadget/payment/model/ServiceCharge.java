package com.gadgetbadget.payment.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.gadgetbadget.payment.util.DBHandler;
import com.google.gson.JsonObject;

public class ServiceCharge extends DBHandler{
	//Update Service Charge
	public JsonObject updateServiceCharge(String service_code, Float service_charge_rate, Float effective_tax_rate)
	{
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "UPDATE `service_charges` SET `service_charge_rate`=?, `effective_tax_rate`=?  WHERE `service_code`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setFloat(1, service_charge_rate);
			preparedStmt.setFloat(2, effective_tax_rate);
			preparedStmt.setString(3, service_code);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Service Charges Updated for " + service_code + ".");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to Update Service Charges.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while updating Service Charges. Exception Details:" + ex);
			ex.printStackTrace();
		}
		return result;
	}

}
