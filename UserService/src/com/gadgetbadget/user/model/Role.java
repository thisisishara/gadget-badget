package com.gadgetbadget.user.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.gadgetbadget.user.util.DBHandler;
import com.gadgetbadget.user.util.DBOpStatus;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Role extends DBHandler {
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
				result.addProperty("MESSAGE", "Role Inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Role was not Inserted.");
			}
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while inserting user-role. Exception Details:" + e.getMessage());
			System.err.println(e.getMessage());
		}
		return result;
	}

	// Read Roles
	public JsonObject readRoles() {
		// Generating JSON Objects
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
		catch (Exception e)
		{
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while reading user-roles. Exception Details:" + e.getMessage());
			System.err.println(e.getMessage());
		}
		return result;
	}

	/*/ Update Role
	public String updateRole()
	{
		String output = "";
		try
		{
			Connection con = getConnection();
			if (con == null)
			{return "Error while connecting to the database for updating."; }
			// create a prepared statement
			String query = "UPDATE item SET itemCode=?,itemName=?,itemPrice=?,itemDesc=?WHERE itemID=?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setString(1, code);
			preparedStmt.setString(2, name);
			preparedStmt.setDouble(3, Double.parseDouble(price));
			preparedStmt.setString(4, desc);
			preparedStmt.setInt(5, Integer.parseInt(ID));
			// execute the statement
			preparedStmt.execute();
			con.close();
			output = "Updated successfully";
		}
		catch (Exception e)
		{
			output = "Error while updating the item.";
			System.err.println(e.getMessage());
		}
		return output;
	}

	//Delete Role
	public String deleteRole(String itemID) {
		String output = "";
		try
		{
			Connection con = getConnection();
			if (con == null)
			{return "Error while connecting to the database for deleting."; }
			// create a prepared statement
			String query = "delete from item where itemID=?";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			preparedStmt.setInt(1, Integer.parseInt(itemID));
			// execute the statement
			preparedStmt.execute();
			con.close();
			output = "Deleted successfully";
		}
		catch (Exception e)
		{
			output = "Error while deleting the item.";
			System.err.println(e.getMessage());
		}
		return output;
	}*/
}