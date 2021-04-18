package com.gadgetbadget.user.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.gadgetbadget.user.util.DBHandler;
import com.gadgetbadget.user.util.HttpMethod;
import com.gadgetbadget.user.util.InterServiceCommHandler;
import com.gadgetbadget.user.util.JsonResponseBuilder;
import com.gadgetbadget.user.util.UserType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * This class represents users as a whole and contains user account related methods and database
 * operations that are common to all users. Extends the DBHandler.
 * 
 * @author Ishara_Dissanayake
 */
public class User extends DBHandler {

	/**
	 * This method reads user by ID (without considering a specific user type) 
	 * for authenticating purposes
	 * 
	 * @param username	user-name consumed at the end-point
	 * @param password	password consumed at the end-point
	 * @return			returns a JSON object containing the user details or an JSON status response
	 */
	public JsonObject getUserById(String username, String password) {
		JsonObject result = null;
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue."); 
			}

			String query = "SELECT u.`user_id`, u.`role_id`, u.`is_deactivated` FROM `user` u WHERE (u.`username` = ? OR u.`user_id`= ?) AND u.`password`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, username);
			preparedStmt.setString(2, username);
			preparedStmt.setString(3, password);
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

	/**
	 * This method reads all User Account Statistics with the help of InterServiceCommHandler class
	 * in order to create a summarized version of user statistics.
	 * 
	 * @return	a JSON object which has the user account details and statistics obtained by other services as JSON
	 * 			objects within the wrapper JSON object.
	 */
	public JsonObject getUsers(boolean isSummarized) {
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
			
			if(!isSummarized) {
				result.add("users", resultArray);
			} else {
				result.add("user_stats", resultArray);
				
				//obtain statistics through service-to-service communication
				//consumer - payments
				for(JsonElement jsonElem : result.get("user_stats").getAsJsonArray()) {
					String user_id = jsonElem.getAsJsonObject().get("user_id").getAsString();
					String role_id = jsonElem.getAsJsonObject().get("role").getAsString();

					if(role_id.equalsIgnoreCase(UserType.CNSMR.toString())){
						JsonObject interRes = new InterServiceCommHandler().paymentIntercomms("payments?consumerid=" + user_id + "&summarized=true", HttpMethod.GET, null);
						if(interRes.entrySet().size() >2) {
							jsonElem.getAsJsonObject().add("payment_stats", interRes);
						} else {
							jsonElem.getAsJsonObject().addProperty("payment_stats", "NOT FOUND");
						}
					}
					
					if(role_id.equalsIgnoreCase(UserType.FUNDR.toString())){
						JsonObject interRes = new InterServiceCommHandler().fundingIntercomms("funds?funderid=" + user_id + "&summarized=true", HttpMethod.GET, null);
						if(interRes.entrySet().size() >2) {
							jsonElem.getAsJsonObject().add("funding_stats", interRes);
						} else {
							jsonElem.getAsJsonObject().addProperty("funding_stats", "NOT FOUND");
						}
					}
					
					if(role_id.equalsIgnoreCase(UserType.RSCHR.toString())){
						JsonObject interRes = null;
						interRes = new InterServiceCommHandler().researchHubIntercomms("research-projects?researcherid=" + user_id + "&summarized=true", HttpMethod.GET, null);
						if(interRes.entrySet().size() >2) {
							jsonElem.getAsJsonObject().add("research_stats", interRes);
						} else {
							jsonElem.getAsJsonObject().addProperty("research_stats", "NOT FOUND");
						}
						
						interRes = new InterServiceCommHandler().marketplaceIntercomms("products?researcherid=" + user_id + "&summarized=true", HttpMethod.GET, null);
						if(interRes.entrySet().size() >2) {
							jsonElem.getAsJsonObject().add("products_stats", interRes);
						} else {
							jsonElem.getAsJsonObject().addProperty("products_stats", "NOT FOUND");
						}
					}
				}
			}						
		}
		catch (Exception ex)
		{
			result = new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while retrieving user statistics. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	/**
	 * This method activates or deactivates UserAccounts with give user_id
	 * A deactivated user account cannot authenticate and obtain a JWT until
	 * it is re-activated.
	 * 
	 * @param user_id	user id consumed at the end-point
	 * @param state		new state of the user account that is about to get updated in the local database
	 * @return			returns a JSON response based on the database operation performed
	 */
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

	/**
	 * This method is used to change Password of an already authenticated user.
	 * That means that, to change the password the user must be either an administrator 
	 * or an already logged in user. A non administrator user cannot change passwords 
	 * except his own.
	 * 
	 * @param user_id		user id of the logged in user
	 * @param oldPassword	previous password that is already saved in the database
	 * @param newPassword	new password that is yet to be updated in the database
	 * @return				returns a JSON response based on the result of the database operation/ password validation
	 */
	public JsonObject changePassword(String user_id, String oldPassword, String newPassword) {
		JsonObject result = null;
		try {			

			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue."); 
			}

			// check if the user is valid by retrieving the user using the old password given
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
			

			System.out.println("PASS:::"+retrCount);

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
	
	/**
	 * This method is used to delete a specific user account from the local database of the user service. All
	 * payment methods, specific user account types will all get deleted when this method is called as the sub
	 * tables are set to delete on the user table's delete through foreign key cascading in the back-end database.
	 * 
	 * @param user_id	user id of the user account to be deleted
	 * @return			returns a JSON response which has the status of the operation and a detailed message about the particular status. 
	 */
	public JsonObject deleteUser(String user_id) {
		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "DELETE FROM `user` WHERE `user_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, user_id);

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("User " + user_id + " deleted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to find the User " + user_id);
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while deleting User " + user_id + ". Exception Details:" + ex);
		}
	}

}
