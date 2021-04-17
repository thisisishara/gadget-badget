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
 * This class represents researchers and is a type of users. Performs 
 * database operations related to researchers, thus Extends the DBHandler.
 * 
 * @author Ishara_Dissanayake
 */
public class Researcher  extends User{
	//Insert Researcher
	public JsonObject insertResearcher(String username, String password, String role_id, String first_name, String last_name, String gender, String primary_email, String primary_phone, String institution, String field_of_study, int years_of_exp) {
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_insert_researcher(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

			callableStmt.registerOutParameter(12, Types.INTEGER);

			callableStmt.setString(1, username);
			callableStmt.setString(2, password);
			callableStmt.setString(3, role_id);
			callableStmt.setString(4, first_name);
			callableStmt.setString(5, last_name);
			callableStmt.setString(6, gender);
			callableStmt.setString(7, primary_email);
			callableStmt.setString(8, primary_phone);
			callableStmt.setString(9, institution);
			callableStmt.setString(10, field_of_study);
			callableStmt.setInt(11, years_of_exp);

			callableStmt.execute();

			status = (int) callableStmt.getInt(12);		

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Researcher Inserted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Insert Researcher.");
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while inserting Researcher. Exception Details:" + ex.getMessage());
		}
	}

	//Read Researchers
	public JsonObject readResearchers() {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "SELECT u.user_id, u.role_id, u.first_name, u.last_name, u.gender, u.primary_email, u.primary_phone, r.institution, r.field_of_study , r.years_of_exp  FROM `user` u, `researcher` r WHERE u.user_id = r.researcher_id;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("Request Processed. No Researchers found.");
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
				recordObject.addProperty("institution", rs.getString("institution"));
				recordObject.addProperty("field_of_study", rs.getString("field_of_study"));
				recordObject.addProperty("years_of_exp", rs.getString("years_of_exp"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("researchers", resultArray);

		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while reading researchers. Exception Details:" + ex.getMessage());
		}
		return result;
	}

	//Read Researcher By Id
	public JsonObject readResearcherById(String researcher_id) {
		JsonObject result = null;
		try
		{			
			// Verify requested ID Pattern
			if(!(new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR))) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format."); 
			}
			
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "SELECT u.user_id, u.role_id, u.first_name, u.last_name, u.gender, u.primary_email, u.primary_phone, r.institution, r.field_of_study , r.years_of_exp  FROM `user` u, `researcher` r WHERE u.user_id = r.researcher_id AND u.user_id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			
			preparedStmt.setString(1, researcher_id);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("Request Processed. Researcher not found.");
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
				recordObject.addProperty("institution", rs.getString("institution"));
				recordObject.addProperty("field_of_study", rs.getString("field_of_study"));
				recordObject.addProperty("years_of_exp", rs.getString("years_of_exp"));
				result = recordObject;
			}
			conn.close();

			return result;

		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while reading researcher. Exception Details:" + ex.getMessage());
		}
	}

	//Update Researcher
	public JsonObject updateResearcher(String user_id,String username, String password, String first_name, String last_name, String gender, String primary_email, String primary_phone, String institution, String field_of_study, int years_of_exp)
	{
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_update_researcher(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

			//output parameter registering
			callableStmt.registerOutParameter(12, Types.INTEGER);

			//Input parameter binding
			callableStmt.setString(1, user_id);
			callableStmt.setString(2, username);
			callableStmt.setString(3, password);
			callableStmt.setString(4, first_name);
			callableStmt.setString(5, last_name);
			callableStmt.setString(6, gender);
			callableStmt.setString(7, primary_email);
			callableStmt.setString(8, primary_phone);
			callableStmt.setString(9, institution);
			callableStmt.setString(10, field_of_study);
			callableStmt.setInt(11, years_of_exp);

			callableStmt.execute();

			//test
			status = (int) callableStmt.getInt(12);

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Researcher " + user_id + " Updated successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Update Researcher " + user_id +".");
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while updating Researcher " + user_id +". Exception Details:" + ex.getMessage());
		}
	}

	//Delete Researcher
	public JsonObject deleteResearcher(String user_id) {
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue."); 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_delete_researcher(?, ?)}");

			//output parameter registering
			callableStmt.registerOutParameter(2, Types.INTEGER);

			//Input parameter binding
			callableStmt.setString(1, user_id);

			callableStmt.execute();

			//test
			status = (int) callableStmt.getInt(2);	

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Researcher " + user_id + " deleted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Delete Researcher "+ user_id +".");
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while deleting Researcher. Exception Details:" + ex.getMessage());
		}
	}
}
