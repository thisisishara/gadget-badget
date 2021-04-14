package com.gadgetbadget.user.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.gadgetbadget.user.util.DBHandler;
import com.gadgetbadget.user.util.JsonResponseBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class User extends DBHandler{
	
	//Read User by ID (without considering a specific user type)
	//for authenticating purposes
	public JsonObject getUserById(String username, String password) {
		JsonObject result = null;
		
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue."); 
			}

			String query = "SELECT u.`user_id`, u.`role_id`, u.`is_deactivated` FROM `user` u WHERE u.`username` = ? AND u.`password`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			
			preparedStmt.setString(1, username);
			preparedStmt.setString(2, password);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("No Users found under the given username.");
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("user_id", rs.getString("user_id"));
				recordObject.addProperty("role", rs.getString("role_id"));
				recordObject.addProperty("username", username);
				recordObject.addProperty("is_deactivated", rs.getString("is_deactivated"));
				
				result = recordObject;
			}
			conn.close();
		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			result = new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while authenticating the user. Exception Details:" + ex.getMessage());
		}
		return result;
	}
	
	
	//Read All User Account Statistics
	public JsonObject getUserAccountStatistics() {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue."); 
			}

			String query = "SELECT `user_id`, `username`, `role_id`, `is_deactivated`, `first_name`, `last_name`, `gender`, `primary_email`, `primary_phone` FROM `user`;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("No Users found under the given username.");
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("user_id", rs.getString("user_id"));
				recordObject.addProperty("role", rs.getString("role_id"));
				recordObject.addProperty("username", rs.getString("username"));
				recordObject.addProperty("is_deactivated", rs.getString("is_deactivated"));
				recordObject.addProperty("first_name", rs.getString("first_name"));
				recordObject.addProperty("last_name", rs.getString("last_name"));
				recordObject.addProperty("gender", rs.getString("gender"));
				recordObject.addProperty("primary_email", rs.getString("primary_email"));
				recordObject.addProperty("primary_phone", rs.getString("primary_phone"));
				resultArray.add(recordObject);
				
			}
			conn.close();
			
			result = new JsonObject();
			result.add("user-stats", resultArray);
			//get payment info
			
			//obtain statistics through service-to-service communication
			//payments
			//researches
			//products
			//funds
		}
		catch (Exception ex)
		{
			result = new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while authenticating the user. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
	
	
	//Activate or Deactivate UserAccount
	public JsonObject changeUserAccountState(String user_id, String state) {
		JsonObject result = null;
		String operation = null;
		
		if(state.equalsIgnoreCase("Yes")) {
			operation = "Deactivate";
		} else if (state.equalsIgnoreCase("No")) {
			operation = "Activate";
		}
		
		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue."); 
			}

			String query = "UPDATE `user` SET `is_deactivated`=? WHERE `user_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, state);
			preparedStmt.setString(2, user_id);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result = new JsonResponseBuilder().getJsonSuccessResponse("User Account of " + user_id + " was "+ operation +"d Successfully.");
			} else {
				result = new JsonResponseBuilder().getJsonFailedResponse("Unable to "+ operation +" the user account of " + user_id);
			}
		}
		catch (Exception ex) {
			result = new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while "+ operation +"ting user account of " +user_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
	
	
	//Change Password
	public JsonObject changePassword(String user_id, String oldPassword, String newPassword) {
		JsonObject result = null;
		try {			
			
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue."); 
			}

			//Check if the user is valid
			String queryRtr = "SELECT u.`user_id` FROM `user` u WHERE u.`user_id` = ? AND u.`password`=?;";
			PreparedStatement preparedStmtRtr = conn.prepareStatement(queryRtr);
			
			preparedStmtRtr.setString(1, user_id);
			preparedStmtRtr.setString(2, oldPassword);
			ResultSet rs = preparedStmtRtr.executeQuery();
			
			int retrCount = 0;
			while(rs.next()) {
				retrCount++;
				System.out.println(retrCount);
			}
			
			if(!(retrCount>0)) {
				return new JsonResponseBuilder().getJsonErrorResponse("Failed to validate the existing password. Password changing failed.");
			}
			
			String queryUpd = "UPDATE `user` SET `password`=? WHERE `user_id`=?;";
			PreparedStatement preparedStmtUpd = conn.prepareStatement(queryUpd);

			preparedStmtUpd.setString(1, newPassword);
			preparedStmtUpd.setString(2, user_id);

			int status = preparedStmtUpd.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result = new JsonResponseBuilder().getJsonSuccessResponse("Password Resetted Successfully.");
			} else {
				result = new JsonResponseBuilder().getJsonFailedResponse("Unable to Reset the password of " + user_id);
			}
		}
		catch (Exception ex) {
			result = new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while resetting the password of " +user_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
	
}
