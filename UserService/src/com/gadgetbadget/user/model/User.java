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

public class User extends DBHandler{
	/*//Read Users (without considering a specific user type)
	public JsonObject readResearchers() {
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

			String query = "SELECT u.user_id, u.role_id, u.first_name, u.last_name, u.gender, u.primary_email, u.primary_phone, r.institution, r.field_of_study , r.years_of_exp  FROM `user` u, `researcher` r WHERE u.user_id = r.researcher_id;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE","No Researchers found.");
				return result;
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
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while reading researchers. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Update Password
	public JsonObject updateResearcher(String user_id,String username, String password, String first_name, String last_name, String gender, String primary_email, String primary_phone, String institution, String field_of_study, int years_of_exp)
	{
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
			status = (int) callableStmt.getInt(9);
			result = new JsonObject();			

			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Researcher " + user_id + " Updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Update Researcher " + user_id +".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while updating Researcher " + user_id +". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Authenticate User
	public JsonObject deleteResearcher(String user_id) {
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
				result.addProperty("MESSAGE", "Researcher " + user_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Delete Researcher "+ user_id +".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while deleting Researcher. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Authorize User
	public JsonObject authorizeUser(String user_id) {
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
				result.addProperty("MESSAGE", "Researcher " + user_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Delete Researcher "+ user_id +".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while deleting Researcher. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}



	//Activate Account
	public JsonObject activateAccount(String user_id) {
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
				result.addProperty("MESSAGE", "Researcher " + user_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Delete Researcher "+ user_id +".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while deleting Researcher. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Deactivate Account
	public JsonObject deactivateAccount(String user_id) {
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
				result.addProperty("MESSAGE", "Researcher " + user_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Delete Researcher "+ user_id +".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while deleting Researcher. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}*/
}
