package com.gadgetbadget.user.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.gadgetbadget.user.util.DBHandler;
import com.gadgetbadget.user.util.JsonResponseBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * This class represents roles of users and contains user role related methods 
 * and database operations that are common to all users. Extends the DBHandler.
 * 
 * @author Ishara_Dissanayake
 */
public class Role extends DBHandler {
	
	/**
	 * This method Inserts a single Role to the database according to the data
	 * sent by the end-point.
	 * 
	 * @param role_id			a 5 char long unique identifier created for each user role
	 * @param role_description	the description of the role given
	 * @return					returns a JSON response based on the database operation performed
	 */
	public JsonObject insertRole(String role_id, String role_description) {
		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "INSERT INTO `role`(`role_id`, `role_description`) VALUES(?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, role_id);
			preparedStmt.setString(2, role_description);

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Role " + role_id + " Inserted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Insert Role " + role_id);
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while inserting Role " +role_id + ". Exception Details:" + ex.getMessage());
		}
	}

	/**
	 * This method Reads Roles and sends it over as a JSON object containing a JSON array
	 * of roles.
	 * 
	 * @return	an array of role JSON elements wrapped in a parent JSON object or a 
	 * 			JSON response based on the database operation result
	 */
	public JsonObject readRoles() {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "SELECT * FROM `role`";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Request Processed. No Roles found.");
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("role_id", rs.getString("role_id"));
				recordObject.addProperty("role_description", rs.getString("role_description"));
				recordObject.addProperty("role_last_updated", rs.getString("role_last_updated"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("roles", resultArray);
			return result;
		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while reading Roles. Exception Details:" + ex.getMessage());
		}
	}

	/**
	 * This method Updates a specific Role based on the information forwarded by the end-point
	 * 
	 * @param role_id			a 5 char long unique identifier created for each user role
	 * @param role_description	the updated description of the role given
	 * @return					returns a JSON response based on the database operation performed
	 */
	public JsonObject updateRole(String role_id, String role_description)
	{
		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "UPDATE `role` SET `role_description`=? WHERE `role_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, role_description);
			preparedStmt.setString(2, role_id);

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Role " + role_id + " Updated successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to update Role " + role_id);
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while updating Role " + role_id + ". Exception Details:" + ex.getMessage());
		}
	}

	/**
	 * THis method Deletes Role based on the id sent by the end-point
	 * 
	 * @param role_id	the role id consumed and sent over by the end-point
	 * @return			a JSON response based on the database operations performed
	 */
	public JsonObject deleteRole(String role_id) {
		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "DELETE FROM `role` WHERE `role_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, role_id);

			int status = preparedStmt.executeUpdate();
			conn.close();

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Role " + role_id + " deleted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to delete Role " + role_id);
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while deleting Role " + role_id + ". Exception Details:" + ex.getMessage());
		}
	}
}