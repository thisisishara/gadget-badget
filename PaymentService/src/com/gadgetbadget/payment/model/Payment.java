package com.gadgetbadget.payment.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

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

			String query = "SELECT * FROM `payment` p LEFT JOIN `paymentmethodinfo` pmi on p.`payment_id` = pmi.`payment_id`;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No payment found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("payment_id", rs.getString("payment_id"));
				recordObject.addProperty("consumer_id", rs.getString("consumer_id"));
				recordObject.addProperty("product_id", rs.getString("product_id"));
				recordObject.addProperty("payment_amount", rs.getFloat("payment_amount"));
				recordObject.addProperty("date_payed", rs.getString("date_payed"));
				recordObject.addProperty("service_charge_rate", rs.getFloat("service_charge_rate"));
				recordObject.addProperty("applied_tax_rate", rs.getFloat("applied_tax_rate"));
				recordObject.addProperty("creditcard_no", rs.getString("creditcard_no"));
				recordObject.addProperty("card_type", rs.getString("card_type"));
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
	public JsonObject insertPayment(String consumer_id, String product_id, float payment_amount, String creditcard_no, String card_type) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_insert_payment(?, ?, ?, ?, ?, ?)}");

			callableStmt.registerOutParameter(6, Types.INTEGER);

			callableStmt.setString(1, consumer_id);
			callableStmt.setString(2, product_id);
			callableStmt.setFloat(3, payment_amount);
			callableStmt.setString(4, creditcard_no);
			callableStmt.setString(5, card_type);

			callableStmt.execute();

			int status = (int) callableStmt.getInt(6);
			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Payment Rs.," + payment_amount + " made successfully by customer-"+ consumer_id+".");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to Make the Payment Rs." + payment_amount+" by customer-"+ consumer_id+".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while making Payment Rs." +payment_amount+ " of customer-"+ consumer_id+". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Update Payment
	public JsonObject updatePayment(String payment_id, String consumer_id, String product_id, float payment_amount, String creditcard_no, String card_type)
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

			CallableStatement callableStmt = conn.prepareCall("{call sp_update_payment(?, ?, ?, ?, ?, ?, ?)}");

			callableStmt.registerOutParameter(7, Types.INTEGER);

			callableStmt.setString(1, payment_id);
			callableStmt.setString(2, consumer_id);
			callableStmt.setString(3, product_id);
			callableStmt.setFloat(4, payment_amount);
			callableStmt.setString(5, creditcard_no);
			callableStmt.setString(6, card_type);

			callableStmt.execute();

			int status = (int) callableStmt.getInt(7);
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Payment amount Rs." + payment_amount +" of ID-"+payment_id+ " Updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to update Payment amount Rs." + payment_amount +" of ID: "+payment_id);
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while updating Payment amount Rs." + payment_amount  +" of ID-"+payment_id+ ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
	//Delete Consumer
	public JsonObject deletePayment(String payment_id) {
		JsonObject result = null;
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_delete_payment(?, ?,?)}");

			//output parameter registering
			callableStmt.registerOutParameter(3, Types.INTEGER);

			//Input parameter binding
			callableStmt.setString(1, payment_id);

			callableStmt.execute();

			//test
			status = (int) callableStmt.getInt(3);
			result = new JsonObject();			

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Payment " + payment_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to Delete Payment "+ payment_id +".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while deleting consumer. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

}
