package com.gadgetbadget.user.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import com.gadgetbadget.user.util.JsonResponseBuilder;
import com.gadgetbadget.user.util.UserType;
import com.gadgetbadget.user.util.ValidationHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * This class represents funders and is a type of users. Performs 
 * database operations related to funders, thus Extends the DBHandler.
 * 
 * @author Ishara_Dissanayake
 */
public class Funder  extends User{
	//Insert Funder
	public JsonObject insertFunder(String username, String password, String role_id, String first_name, String last_name, String gender, String primary_email, String primary_phone, String organization) {
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_insert_funder(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

			callableStmt.registerOutParameter(10, Types.INTEGER);

			callableStmt.setString(1, username);
			callableStmt.setString(2, password);
			callableStmt.setString(3, role_id);
			callableStmt.setString(4, first_name);
			callableStmt.setString(5, last_name);
			callableStmt.setString(6, gender);
			callableStmt.setString(7, primary_email);
			callableStmt.setString(8, primary_phone);
			callableStmt.setString(9, organization);

			callableStmt.execute();

			status = (int) callableStmt.getInt(10);	

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Funder Inserted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Insert Funder.");
			}
		}
		catch (Exception ex) {
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while inserting Funder. Exception Details:" + ex.getMessage());
		}
	}

	//Read Funders
	public JsonObject readFunders() {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "SELECT u.user_id, u.role_id, u.first_name, u.last_name, u.gender, u.primary_email, u.primary_phone, f.organization FROM `user` u, `funder` f WHERE u.user_id=f.funder_id;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("Request Processed. Funder not found.");
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("user_id", rs.getString("user_id"));
				recordObject.addProperty("first_name", rs.getString("first_name"));
				recordObject.addProperty("last_name", rs.getString("last_name"));
				recordObject.addProperty("gender", rs.getString("gender"));
				recordObject.addProperty("primary_email", rs.getString("primary_email"));
				recordObject.addProperty("primary_phone", rs.getString("primary_phone"));
				recordObject.addProperty("organization", rs.getString("organization"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("funders", resultArray);

		}
		catch (Exception ex)
		{
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while reading funder. Exception Details:" + ex.getMessage());
		}
		return result;
	}

	//Read Funders By Id
	public JsonObject readFunderById(String funder_id) {
		JsonObject result = null;
		try
		{			
			// Verify requested ID Pattern
			if(!(new ValidationHandler().validateUserType(funder_id, UserType.FUNDR))) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format."); 
			}
			
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "SELECT u.user_id, u.role_id, u.first_name, u.last_name, u.gender, u.primary_email, u.primary_phone, f.organization FROM `user` u, `funder` f WHERE u.user_id=f.funder_id AND u.user_id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			
			preparedStmt.setString(1, funder_id);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("Request Processed. No Funders found.");
			}


			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("user_id", rs.getString("user_id"));
				recordObject.addProperty("first_name", rs.getString("first_name"));
				recordObject.addProperty("last_name", rs.getString("last_name"));
				recordObject.addProperty("gender", rs.getString("gender"));
				recordObject.addProperty("primary_email", rs.getString("primary_email"));
				recordObject.addProperty("primary_phone", rs.getString("primary_phone"));
				recordObject.addProperty("organization", rs.getString("organization"));
				result = recordObject;
			}
			conn.close();

			return result;

		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while reading funder. Exception Details:" + ex.getMessage());
		}
	}

	//Update Funder
	public JsonObject updateFunder(String user_id,String username, String password, String first_name, String last_name, String gender, String primary_email, String primary_phone, String organization)
	{
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_update_funder(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

			//output parameter registering
			callableStmt.registerOutParameter(10, Types.INTEGER);

			//Input parameter binding
			callableStmt.setString(1, user_id);
			callableStmt.setString(2, username);
			callableStmt.setString(3, password);
			callableStmt.setString(4, first_name);
			callableStmt.setString(5, last_name);
			callableStmt.setString(6, gender);
			callableStmt.setString(7, primary_email);
			callableStmt.setString(8, primary_phone);
			callableStmt.setString(9, organization);

			callableStmt.execute();

			//test
			status = (int) callableStmt.getInt(10);		

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Funder " + user_id + " Updated successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Update Funder " + user_id +".");
			}
		}
		catch (Exception ex) {
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while updating Funder " + user_id +". Exception Details:" + ex.getMessage());
		}
	}

	//Delete Funder
	public JsonObject deleteFunder(String user_id) {
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue."); 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_delete_funder(?, ?)}");

			//output parameter registering
			callableStmt.registerOutParameter(2, Types.INTEGER);

			//Input parameter binding
			callableStmt.setString(1, user_id);

			callableStmt.execute();

			//test
			status = (int) callableStmt.getInt(2);	

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Funder " + user_id + " deleted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Delete Funder "+ user_id +".");
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while deleting Funder. Exception Details:" + ex.getMessage());
		}
	}
}
