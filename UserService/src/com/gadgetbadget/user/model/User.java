package com.gadgetbadget.user.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.gadgetbadget.user.util.DBHandler;
import com.gadgetbadget.user.util.DBOpStatus;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class User extends DBHandler{
	//login
	
	//read users

	//update password
	
	//update username	
	
	/*
	// Insert Role
	public JsonObject insertRole(String role_id, String role_description) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "INSERT INTO `role`(`role_id`, `role_description`) VALUES(?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			// binding values
			preparedStmt.setString(1, role_id);
			preparedStmt.setString(2, role_description);

			// execute the statement
			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", "Role " + role_id + " Inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Insert Role " + role_id);
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while inserting Role " +role_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	// Read Roles
	public JsonObject readRoles() {
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

			// Retrieving roles
			String query = "SELECT * FROM `role`";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			// check if no data 
			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result;
			}

			// Iterate through the rows in the result set
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

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while reading user-roles. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	// Update Role
	public JsonObject updateRole(String role_id, String role_description)
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

			String query = "UPDATE `role` SET `role_description`=? WHERE `role_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			// binding values
			preparedStmt.setString(1, role_description);
			preparedStmt.setString(2, role_id);

			// execute the statement
			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", "Role " + role_id + " Updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to update Role " + role_id);
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while updating Role " + role_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Delete Role
	public JsonObject deleteRole(String role_id) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "DELETE FROM `role` WHERE `role_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			// binding values
			preparedStmt.setString(1, role_id);

			// execute the statement
			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", "Role " + role_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to delete Role " + role_id);
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while deleting Role " + role_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
	*/
}
