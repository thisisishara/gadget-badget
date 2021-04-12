package com.gadgetbadget.payment.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.gadgetbadget.payment.util.DBHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Payment extends DBHandler{

	//Read Payments
	public JsonObject readPayment() {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "SELECT p.payment_id, p.consumer_id, p.product_id, p.payment_amount, p.date_payed, p.service_charge_rate, m.creditcard_no, m.card_type p.applied_tax_rate FROM `payment` p,`paymentmethodinfo` m WHERE p.payment_id = m.payment_id";
			Statement stmt = conn.createStatement();
			ResultSet py = stmt.executeQuery(query);

			if(!py.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No payment found.");
				return result;
			}

			while (py.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("payment_id", py.getString("payment_id"));
				recordObject.addProperty("consumer_id", py.getString("consumer_id"));
				recordObject.addProperty("product_id", py.getString("product_id"));
				recordObject.addProperty("payment_amount", py.getFloat("payment_amount"));
				recordObject.addProperty("date_payed", py.getString("date_payed"));
				recordObject.addProperty("service_charge_rate", py.getFloat("service_charge_rate"));
				recordObject.addProperty("applied_tax_rate", py.getFloat("applied_tax_rate"));
				recordObject.addProperty("creditcard_no", py.getString("creditcard_no"));
				recordObject.addProperty("card_type", py.getString("card_type"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("payments", resultArray);

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading payments. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Insert Payment
	public JsonObject insertPayment(String consumer_id, String product_id, float payment_amount) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "INSERT INTO `payment`(`consumer_id`, `product_id`, `payment_amount`,) VALUES(?,?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, consumer_id);
			preparedStmt.setString(2, product_id);
			preparedStmt.setFloat(3, payment_amount);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Payment " + payment_amount + " made successfully by customer "+ consumer_id+".");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to Make the Payment " + payment_amount+" by customer "+ consumer_id+".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while making Payment " +payment_amount+ " of customer "+ consumer_id+". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}



}
