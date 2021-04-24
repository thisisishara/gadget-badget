package com.gadgetbadget.researchhub.model;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.Timestamp;
import java.sql.Types;

import com.gadgetbadget.researchhub.util.DBHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Researchproject extends DBHandler{
	//Insert Research Projects
	public JsonObject insertProject(String researcher_id, String project_name,String project_description,String category_id,String project_start_date,String project_end_date,String expected_total_budget) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS","ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "INSERT INTO `researchproject`(`researcher_id`,`project_name`,`project_description`,`category_id`,`project_start_date`,`project_end_date`,`expected_total_budget`) VALUES(?,?,?,?,?,?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);


			preparedStmt.setString(1,researcher_id);
			preparedStmt.setString(2, project_name);
			preparedStmt.setString(3,project_description);
			preparedStmt.setString(4, category_id);
			preparedStmt.setString(5, project_start_date);
			preparedStmt.setString(6, project_end_date);
			preparedStmt.setString(7, expected_total_budget);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Projects Inserted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to Insert Projects.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while inserting Project. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Read Research Project
	public JsonObject readProjects(String researcher_id) {
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
			
			String query = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			if(researcher_id == null) {
				query = "SELECT * FROM `researchproject`";
				stmt = conn.prepareStatement(query);
			} else {
				query = "SELECT * FROM `researchproject` WHERE `researcher_id`=?;";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, researcher_id);
			}

			rs = stmt.executeQuery();		

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","No Projects found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("project_id", rs.getString("project_id"));
				recordObject.addProperty("researcher_id", rs.getString("researcher_id"));
				recordObject.addProperty("project_name", rs.getString("project_name"));
				recordObject.addProperty("project_description", rs.getString("project_description"));
				recordObject.addProperty("project_start_date", rs.getString("project_start_date"));
				recordObject.addProperty("project_end_date", rs.getString("project_end_date"));
				recordObject.addProperty("expected_total_budget", rs.getString("expected_total_budget"));
				recordObject.addProperty("date_added", rs.getString("date_added"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("projects", resultArray);

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading projects. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Read Research Projects
	public JsonObject readProject(String researcher_id, String project_id) {
		JsonObject result = null;
		
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS","ERROR");
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}
			
			String query = null;
			PreparedStatement preparedStmt = null;
			ResultSet rs = null;

			if(researcher_id == null) {

				query = "SELECT * FROM `researchproject` WHERE `project_id`=?;";
				preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, project_id);
				rs = preparedStmt.executeQuery();

			} else {
				query = "SELECT * FROM `researchproject` WHERE `project_id`=? AND `researcher_id`=?;";
				preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, project_id);
				preparedStmt.setString(2, researcher_id);
				rs = preparedStmt.executeQuery();
			}

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","No Projects found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("project_id", rs.getString("project_id"));
				recordObject.addProperty("researcher_id", rs.getString("researcher_id"));
				recordObject.addProperty("project_name", rs.getString("project_name"));
				recordObject.addProperty("project_description", rs.getString("project_description"));
				recordObject.addProperty("project_start_date", rs.getString("project_start_date"));
				recordObject.addProperty("project_end_date", rs.getString("project_end_date"));
				recordObject.addProperty("expected_total_budget", rs.getString("expected_total_budget"));
				recordObject.addProperty("date_added", rs.getString("date_added"));
				result = recordObject;
			}
			conn.close();
		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading projects. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Read Project Summary of researcher_id
	public JsonObject readProjectSummeryByResearcherId(String researcher_id) {
		JsonObject result = null;
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_researchproject_summary_resid(?, ?, ?, ?, ?, ?)}");

			//Input parameter binding
			callableStmt.setString(1, researcher_id);

			//output parameter registering
			callableStmt.registerOutParameter(2, Types.INTEGER);
			callableStmt.registerOutParameter(3, Types.TIMESTAMP);
			callableStmt.registerOutParameter(4, Types.DECIMAL);
			callableStmt.registerOutParameter(5, Types.INTEGER);
			callableStmt.registerOutParameter(6, Types.TIMESTAMP);


			callableStmt.execute();
			
			int no_researchprojects = (int) callableStmt.getInt(2);
			String latest_research_uploaded_date = ((Timestamp) callableStmt.getTimestamp(3)).toString();
			double total_budget_of_ongoing_projects = ((BigDecimal) callableStmt.getBigDecimal(4)).doubleValue();
			int no_of_categories = (int) callableStmt.getInt(5);
			String retrieved_date = ((Timestamp)  callableStmt.getTimestamp(6)).toString();

			result = new JsonObject();			
			result.addProperty("no_researchprojects", no_researchprojects);
			result.addProperty("latest_research_uploaded_date", latest_research_uploaded_date);
			result.addProperty("total_budget_of_ongoing_projects", total_budget_of_ongoing_projects);
			result.addProperty("no_of_categories", no_of_categories);
			result.addProperty("retrieved_date", retrieved_date);
			return result;

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading research projects statistics of the given researcher. Exception Details:" + ex);
			ex.printStackTrace();
		}
		return result;
	}

	//Update Research Projects
	public JsonObject updateProject(String project_id, String researcher_id,String project_name, String project_description,String category_id,String project_start_date,String project_end_date,String expected_total_budget)
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

			String query = "UPDATE `researchproject` SET `researcher_id`=?,`project_name`=?,`project_description`=?,`category_id`=?,`project_start_date`=?,`project_end_date`=?, `expected_total_budget`=? WHERE `project_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, researcher_id);
			preparedStmt.setString(2, project_name);
			preparedStmt.setString(3, project_description);
			preparedStmt.setString(4, category_id);
			preparedStmt.setString(5, project_start_date);
			preparedStmt.setString(6, project_end_date);
			preparedStmt.setString(7, expected_total_budget);
			preparedStmt.setString(8, project_id);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Project " + project_id + " Updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to update project " + project_id);
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS","EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while updating project " + project_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Delete Research Projects
	public JsonObject deleteProject(String researcher_id, String project_id) {
		JsonObject result = null;
		int status = 0;
		
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = null;
			PreparedStatement preparedStmt = null;

			if(researcher_id == null) {
				query = "DELETE FROM `researchproject` WHERE `project_id`=?;";
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, project_id);				

			} else {
				query = "DELETE FROM `researchproject` WHERE `project_id`=? AND `researcher_id` = ?;";
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, project_id);
				preparedStmt.setString(2, researcher_id);
			}

			status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "project " + project_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS","UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to delete Project " + project_id);
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while deleting Project " + project_id + ". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	// Get product ownership of a project
	public boolean isOwner(String researcher_id, String project_id) {
		try
		{			
			Connection conn = getConnection();
			if (conn == null) {
				return false; 
			}

			String query = "SELECT * FROM `researchproject` WHERE `project_id`=? AND `researcher_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, project_id);
			preparedStmt.setString(2, researcher_id);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				return false;
			}

			int count = 0;
			
			while (rs.next())
			{
				count++;
			}
			conn.close();

			if(count>0) {
				return true;
			}
			
			return false;
		}
		catch (Exception ex)
		{
			return false;
		}
	}
}