package com.gadgetbadget.payment.model;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

import com.gadgetbadget.payment.util.DBHandler;
import com.gadgetbadget.payment.util.InterServiceCommHandler;
import com.gadgetbadget.payment.util.ValidationHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Payment extends DBHandler{
	//Read Payments
	public JsonObject readPayments(String consumer_id) {
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

			String query =  null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			if(consumer_id == null) {
				query = "SELECT * FROM `payment` p LEFT JOIN `paymentmethodinfo` pmi on p.`payment_id` = pmi.`payment_id`;";
				stmt = conn.prepareStatement(query);
				rs = stmt.executeQuery(query);

			} else {
				query = "SELECT * FROM `payment` p LEFT JOIN `paymentmethodinfo` pmi on p.`payment_id` = pmi.`payment_id` AND p.`consumer_id`= ?;";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, consumer_id);
				rs = stmt.executeQuery(query);
			}			

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No payments found.");
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


	//Read PaymentSummaryByConsumerId
	public JsonObject readPaymentSummaryByConsumerId(String consumer_id) {
		JsonObject result = null;
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_payment_summary_consumerid(?, ?, ?, ?, ?, ?)}");

			//output parameter registering
			callableStmt.registerOutParameter(2, Types.INTEGER);
			callableStmt.registerOutParameter(3, Types.TIMESTAMP);
			callableStmt.registerOutParameter(4, Types.DECIMAL);
			callableStmt.registerOutParameter(5, Types.INTEGER);
			callableStmt.registerOutParameter(6, Types.TIMESTAMP);

			//Input parameter binding
			callableStmt.setString(1, consumer_id);

			callableStmt.execute();

			int no_payments_made = (int) callableStmt.getInt(2);
			String latest_payment_date = ((Timestamp) callableStmt.getTimestamp(3)).toString();
			double total_payments = ((BigDecimal) callableStmt.getBigDecimal(4)).doubleValue();
			int total_products = (int) callableStmt.getInt(5);
			String retrieved_date = ((Timestamp)  callableStmt.getTimestamp(6)).toString();

			result = new JsonObject();			
			result.addProperty("no_payments_made", no_payments_made);
			result.addProperty("latest_payment_date", latest_payment_date);
			result.addProperty("total_payments", total_payments);
			result.addProperty("total_products", total_products);
			result.addProperty("retrieved_date", retrieved_date);
			return result;

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading payment statistics. Exception Details:" + ex);
			ex.printStackTrace();
		}
		return result;
	}

	//Read Payments By product id
	public JsonObject readPaymentsByProductId(String product_id) {
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

			String query = "SELECT * FROM `payment` WHERE `product_id`= ?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, product_id);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No payments found for the given product.");
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
			result.addProperty("MESSAGE", "Error occurred while reading payments for the given product. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Read Payments By consumer id + payment_id
	public JsonObject getPayment(String consumer_id, String payment_id) {
		JsonObject result = null;
		try
		{			
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = null;
			ResultSet rs = null;

			if(consumer_id == null) {

				query = "SELECT * FROM `payment` p LEFT JOIN `paymentmethodinfo` pmi on p.`payment_id` = pmi.`payment_id` WHERE p.`payment_id`=?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, payment_id);
				rs = preparedStmt.executeQuery();

			} else {
				query = "SELECT * FROM `payment` p LEFT JOIN `paymentmethodinfo` pmi on p.`payment_id` = pmi.`payment_id` WHERE p.`payment_id`=? AND p.`consumer_id` = ?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, payment_id);
				preparedStmt.setString(2, consumer_id);
				rs = preparedStmt.executeQuery();
			}

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No payments found for the given ID.");
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
				result = recordObject;
			}
			conn.close();

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading payments for the given ID. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Insert Payment
	public JsonObject insertPayment(String consumer_id, String product_id, float payment_amount, String creditcard_no) {
		JsonObject result = null;
		try {
			//Verify Payment using service-to-service communication
			JsonObject payload = new JsonObject();
			payload.addProperty("creditcard_no", creditcard_no);

			JsonObject paymentMethodDetails = new InterServiceCommHandler().userIntercomms("users/consumers/" + consumer_id + "/payment-methods?retrieve=true", payload);
			if(! new ValidationHandler().verifyPaymentMethod(paymentMethodDetails)) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Invalid Payment Method. Try again with a different credit card.");
				return result; 
			}


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
			callableStmt.setString(5, paymentMethodDetails.get("creditcard_type").getAsString());

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
	public JsonObject updatePayment(String payment_id, String consumer_id, String product_id, float payment_amount, String creditcard_no)
	{
		JsonObject result = null;
		try {
			//Verify Payment using service-to-service communication
			JsonObject payload = new JsonObject();
			payload.addProperty("creditcard_no", creditcard_no);

			JsonObject paymentMethodDetails = new InterServiceCommHandler().userIntercomms("users/consumers/" + consumer_id + "/payment-methods?retrieve=true", payload);
			if(! new ValidationHandler().verifyPaymentMethod(paymentMethodDetails)) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Invalid Payment Method. Try again with a different credit card.");
				return result; 
			}


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
			callableStmt.setString(6, paymentMethodDetails.get("creditcard_type").getAsString());

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

	//Delete Payment
	public JsonObject deletePayment(String consumer_id, String payment_id) {
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

			String query = null;
			PreparedStatement preparedStmt = null;

			if(consumer_id == null) {
				query = "DELETE FROM `payment` WHERE `payment_id` = ?;";
				preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, payment_id);
				status = preparedStmt.executeUpdate();
			} else {
				query = "DELETE FROM `payment` WHERE `consumer_id` = ? AND `payment_id` = ?;";
				preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, consumer_id);
				preparedStmt.setString(2, payment_id);
				status = preparedStmt.executeUpdate();
			}

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
			result.addProperty("MESSAGE", "Error occurred while deleting payment. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	public JsonObject calculateProfit() {
		JsonObject result = null;
		try
		{			
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "SELECT SUM(round(((`payment_amount` - (`payment_amount`* (`applied_tax_rate` /100))) * (`service_charge_rate`/100)),2)) AS 'profit' FROM `payment` WHERE 1;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No payments found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("sales_profit", rs.getString("profit"));
				recordObject.addProperty("timestamp", new Timestamp(System.currentTimeMillis()).toString());
				result = recordObject;

			}
			conn.close();
		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while calculating sales profit. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

}
