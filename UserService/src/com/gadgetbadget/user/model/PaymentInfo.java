package com.gadgetbadget.user.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import com.gadgetbadget.user.util.DBHandler;
import com.gadgetbadget.user.util.DBOpStatus;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PaymentInfo extends DBHandler{
	//Insert PaymentInfo
	public JsonObject insertPaymentInfo(String user_id, String creaditcard_type, int creditcard_no, int creditcard_security_no, String exp_date, String billing_address) {
		JsonObject result = null;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "INSERT INTO `paymentinfo`(`user_id`, `creaditcard_type`, `creditcard_no`, `creditcard_security_no`, `exp_date`, `billing_address`) VALUES(?,?,?,?,?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, user_id);
			preparedStmt.setString(2, creaditcard_type);
			preparedStmt.setInt(3, creditcard_no);
			preparedStmt.setInt(4, creditcard_security_no);
			preparedStmt.setString(5, exp_date);
			preparedStmt.setString(6, billing_address);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Payment information of user " + user_id + " Inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Insert Payment Information of user " + user_id + ".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while inserting Payment information of user " + user_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Read PaymentInfo
	public JsonObject readPaymentInfo(String user_id) {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "SELECT * FROM `paymentinfo` WHERE user_id = ?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, user_id);
			ResultSet rs = preparedStmt.executeQuery(query);

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE","No Payment Information found for user " + user_id + ".");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("user_id", rs.getString("user_id"));
				recordObject.addProperty("creaditcard_type", rs.getString("creaditcard_type"));
				recordObject.addProperty("creditcard_no", String.valueOf(rs.getInt("creditcard_no")));
				recordObject.addProperty("creditcard_security_no", String.valueOf(rs.getInt("creditcard_security_no")));
				recordObject.addProperty("exp_date", rs.getString("exp_date"));
				recordObject.addProperty("billing_address", rs.getString("billing_address"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("paymentinfo", resultArray);

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while reading Payment Information of user" + user_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Update PaymentInfo
	public JsonObject updatePaymentInfo(String user_id, String creaditcard_type, int creditcard_no, int creditcard_security_no, String exp_date, String billing_address)
	{
		JsonObject result = null;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "UPDATE `paymentinfo` SET `creaditcard_type`=?, `creditcard_no`=?, `creditcard_security_no`=?, `exp_date`=?, `billing_address`=? WHERE `user_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(6, user_id);
			preparedStmt.setString(1, creaditcard_type);
			preparedStmt.setInt(2, creditcard_no);
			preparedStmt.setInt(3, creditcard_security_no);
			preparedStmt.setString(4, exp_date);
			preparedStmt.setString(5, billing_address);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Payment information of user " + user_id + " Updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Update Payment Information of user " + user_id + ".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while updating Payment information of user " + user_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Delete PaymentInfo
	public JsonObject deletePaymentInfo(String user_id) {
		JsonObject result = null;
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_delete_researcher(?, ?)}");

			//output parameter registering
			callableStmt.registerOutParameter(2, Types.INTEGER);

			//Input parameter binding
			callableStmt.setString(1, user_id);

			callableStmt.execute();

			//test
			status = (int) callableStmt.getInt(2);
			result = new JsonObject();			

			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", "PaymentInfo " + user_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Delete PaymentInfo "+ user_id +".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while deleting PaymentInfo. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
}
