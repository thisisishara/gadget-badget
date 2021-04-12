package com.gadgetbadget.researchhub.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.gadgetbadget.researchhub.util.DBHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Researchproject extends DBHandler {
	//Insert Research Projects
	public JsonObject insertProject(String researcher_id, String product_name,String project_description,String category_id,String project_start_date,String project_end_date,String expected_total_budget) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS","ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "INSERT INTO `researchproject`(`researcher_id`,`product_name`,`project_description`,`category_id`,`project_start_date`,`project_end_date`,`expected_total_budget`) VALUES(?,?,?,?,?,?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, researcher_id);
			preparedStmt.setString(2, product_name);
			preparedStmt.setString(3, project_description);
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

	//Read Research Projects
		public JsonObject readProject() {
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

				String query = "SELECT * FROM `researchproject`";
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);

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
					recordObject.addProperty("product_name", rs.getString("product_name"));
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

		//Update Research Projects
		public JsonObject updateProject(String project_id, String researcher_id,String product_name, String project_description,String project_start_date,String project_end_date,String expected_total_budget)
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

				String query = "UPDATE `researchproject` SET `researcher_id`=?,`product_name`=?,`project_description`=?,`project_start_date`=?,`project_end_date`=?, `expected_total_budget`=? WHERE `project_id`=?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, researcher_id);
				preparedStmt.setString(2, product_name);
				preparedStmt.setString(3, project_description);
				preparedStmt.setString(4, project_start_date);
				preparedStmt.setString(5, project_end_date);
				preparedStmt.setString(6, expected_total_budget);
				preparedStmt.setString(7, project_id);

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
		public JsonObject deleteProject(String project_id) {
			JsonObject result = null;
			try {
				Connection conn = getConnection();
				if (conn == null) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
					return result; 
				}

				String query = "DELETE FROM `researchproject` WHERE `project_id`=?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, project_id);

				int status = preparedStmt.executeUpdate();
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
}