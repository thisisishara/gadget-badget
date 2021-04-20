package com.gadgetbadget.researchhub.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import com.gadgetbadget.researchhub.util.DBHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Collaborator extends DBHandler {
	//Insert Collaborators
	public JsonObject insertCollaborator( String project_id,String full_name,String institution) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS","ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "INSERT INTO `collaborator`(`project_id`,`full_name`,`institution`) VALUES(?,?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1,project_id);
			preparedStmt.setString(2,full_name);
			preparedStmt.setString(3,institution);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Collaborators Inserted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to Insert Collaborators.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while inserting Collaborator. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Read Collaborators
	public JsonObject readCollaborators(String project_id) {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS","ERROR");
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "SELECT * FROM `collaborator` WHERE `project_id`=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, project_id);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","No Collaborators found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("project_id", rs.getString("project_id"));
				recordObject.addProperty("full_name", rs.getString("full_name"));
				recordObject.addProperty("institution", rs.getString("institution"));
				recordObject.addProperty("date_joined", rs.getString("date_joined"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("collaborators", resultArray);

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading collaborators. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Read Collaborators
	public JsonObject readCollaborator(String project_id, String full_name) {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS","ERROR");
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "SELECT * FROM `collaborator` WHERE `project_id`=? AND `full_name`=?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, project_id);
			preparedStmt.setString(2, full_name);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","No Collaborators found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("project_id", rs.getString("project_id"));
				recordObject.addProperty("full_name", rs.getString("full_name"));
				recordObject.addProperty("institution", rs.getString("institution"));
				recordObject.addProperty("date_joined", rs.getString("date_joined"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("collaborators", resultArray);

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading collaborators. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Update Collaborators
	public JsonObject updateCollaborator(String project_id, String full_name, String institution)
	{
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS","ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "UPDATE `collaborator` SET `institution`=? WHERE `project_id`=? AND `full_name`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, institution);
			preparedStmt.setString(2, project_id);
			preparedStmt.setString(3, full_name);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Collaborator " + project_id + " Updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to update collaborator " + project_id);
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS","EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while updating collaborators " + project_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Delete Collaborators
	public JsonObject deleteCollaborator(String project_id,String full_name) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "DELETE FROM `collaborator` WHERE `project_id`=? AND `full_name`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, project_id);
			preparedStmt.setString(2, full_name);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "project " + project_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS","UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to delete Collaborators " + project_id);
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while deleting Collaborator " + project_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
}



