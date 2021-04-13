package com.gadgetbadget.user.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.gadgetbadget.user.util.DBHandler;
import com.gadgetbadget.user.util.DBOpStatus;
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
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "SELECT u.`user_id`, u.`role_id` FROM `user` u WHERE u.`username` = ? AND u.`password`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			
			preparedStmt.setString(1, username);
			preparedStmt.setString(2, password);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE","No Users found under the given username.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("user_id", rs.getString("user_id"));
				recordObject.addProperty("role", rs.getString("role_id"));
				recordObject.addProperty("username", username);
				recordObject.addProperty("password", password);
				
				result = recordObject;
			}
			conn.close();

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while authenticating the user. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
	
	//Deactivate User
	
}
