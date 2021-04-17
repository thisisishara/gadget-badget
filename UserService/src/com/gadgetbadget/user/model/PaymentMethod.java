package com.gadgetbadget.user.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.gadgetbadget.user.util.DBHandler;
import com.gadgetbadget.user.util.JsonResponseBuilder;
import com.gadgetbadget.user.util.UserType;
import com.gadgetbadget.user.util.ValidationHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * This class represents payment methods of all users except for employees.
 * Performs database operations related to payment methods, thus Extends the DBHandler.
 * 
 * @author Ishara_Dissanayake
 */
public class PaymentMethod extends DBHandler{
	//Insert a PaymentMethod
	public JsonObject insertPaymentMethod(String user_id, String creaditcard_type, String creditcard_no, String creditcard_security_no, String exp_date, String billing_address) {

		try {

			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "INSERT INTO `paymentmethod`(`user_id`, `creditcard_type`, `creditcard_no`, `creditcard_security_no`, `exp_date`, `billing_address`) VALUES(?,?,?,?,?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, user_id);
			preparedStmt.setString(2, creaditcard_type);
			preparedStmt.setString(3, creditcard_no);
			preparedStmt.setString(4, creditcard_security_no);
			preparedStmt.setString(5, exp_date);
			preparedStmt.setString(6, billing_address);

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Payment Method of user " + user_id + " Inserted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Insert Payment Method of user " + user_id + ".");
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonFailedResponse("Error occurred while inserting Payment Method of user " + user_id + ". Exception Details:" + ex.getMessage());
		}
	}

	//Read PaymentMethods
	public JsonObject readPaymentMethods(String user_id) {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();

		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "SELECT * FROM `paymentmethod` WHERE `user_id`= ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, user_id);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("Request Processed. No Payment Method found for user " + user_id + ".");
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("user_id", rs.getString("user_id"));
				recordObject.addProperty("creditcard_type", rs.getString("creditcard_type"));
				recordObject.addProperty("creditcard_no", rs.getString("creditcard_no"));
				recordObject.addProperty("creditcard_security_no", rs.getString("creditcard_security_no"));
				recordObject.addProperty("exp_date", rs.getString("exp_date"));
				recordObject.addProperty("billing_address", rs.getString("billing_address"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("payment_methods", resultArray);

		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while retrieving Payment Method(s) of user " + user_id + ". Exception Details:" + ex.getMessage());
		}
		return result;
	}
	
	//Read a specific PaymentMethod
	public JsonObject readSpecificPaymentMethod(String user_id, String creditcard_no) {
		JsonObject result = null;

		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "SELECT * FROM `paymentmethod` WHERE `user_id`= ? AND `creditcard_no`=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, user_id);
			preparedStmt.setString(2, creditcard_no);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("Request Processed. No Payment Method found under " + creditcard_no + " for user " + user_id + ".");
			}

			if (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("user_id", rs.getString("user_id"));
				recordObject.addProperty("creditcard_type", rs.getString("creditcard_type"));
				recordObject.addProperty("creditcard_no", rs.getString("creditcard_no"));
				recordObject.addProperty("creditcard_security_no", rs.getString("creditcard_security_no"));
				recordObject.addProperty("exp_date", rs.getString("exp_date"));
				recordObject.addProperty("billing_address", rs.getString("billing_address"));
				result = recordObject;
			}
			conn.close();
		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while retrieving Payment Method of user " + user_id + ". Exception Details:" + ex.getMessage());
		}
		return result;
	}

	//Update a PaymentMethod
	public JsonObject updatePaymentMethod(String user_id, String creditcard_type, String new_creditcard_no, String creditcard_no, String creditcard_security_no, String exp_date, String billing_address)
	{
		try {			

			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "UPDATE `paymentmethod` SET  `creditcard_no`=?, `creditcard_type`=?, `creditcard_security_no`=?, `exp_date`=?, `billing_address`=? WHERE `user_id`=? AND `creditcard_no`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, new_creditcard_no);
			preparedStmt.setString(2, creditcard_type);
			preparedStmt.setString(3, creditcard_security_no);
			preparedStmt.setString(4, exp_date);
			preparedStmt.setString(5, billing_address);
			preparedStmt.setString(6, user_id);
			preparedStmt.setString(7, creditcard_no);

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse( "Payment Method of user " + user_id + " Updated successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Update Payment Method of user " + user_id + ".");
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while updating Payment Method of user " + user_id + ". Exception Details:" + ex.getMessage());
		}
	}

	//Delete a Specific PaymentMethod
	public JsonObject deletePaymentMethod(String user_id, String creditcard_no) {

		try {

			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "DELETE FROM `paymentmethod` WHERE `user_id`=? AND `creditcard_no`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, user_id);
			preparedStmt.setString(2, creditcard_no);

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Payment Method of user " + user_id + " was deleted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to delete Payment Method of user " + user_id);
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while deleting Payment Method of user " + user_id + ". Exception Details:" + ex.getMessage());
		}
	}

	//Delete all PaymentMethod
	public JsonObject deletePaymentMethods(String user_id, UserType user_type) {

		try {

			//verify user_type	
			if(!new ValidationHandler().validateUserType(user_id, user_type)) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.");
			}

			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "DELETE FROM `paymentmethod` WHERE `user_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, user_id);

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("All Payment Methods of user " + user_id + " was deleted successfully. Total number of Payment Methods deleted: " + status + ".");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("There are no Payment Methods of user " + user_id + " to delete.");
			}
		}
		catch (Exception ex) {
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while deleting all Payment Methods of user " + user_id + ". Exception Details:" + ex.getMessage());
		}
	}
}
