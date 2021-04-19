package com.gadgetbadget.researchhub.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import com.gadgetbadget.researchhub.util.DBHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Category extends DBHandler {

	//Read Categories
	public JsonObject readCategories() {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{			
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "SELECT * FROM `research_category`;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No categories found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("category_id", rs.getString("category_id"));
				recordObject.addProperty("category_name", rs.getString("category_name"));
				recordObject.addProperty("category_description", rs.getString("category_description"));
				recordObject.addProperty("date_last_updated", rs.getString("date_last_updated"));
				recordObject.addProperty("last_modified_by", rs.getString("last_modified_by"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("product_categories", resultArray);

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading categories of products. Exception Details:" + ex);
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Read Category
	public JsonObject readCategory(String category_id) {
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

			String query = "SELECT * FROM `research_category` WHERE `category_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, category_id);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No categories found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("category_id", rs.getString("fund_id"));
				recordObject.addProperty("category_name", rs.getString("category_name"));
				recordObject.addProperty("category_description", rs.getString("category_description"));
				recordObject.addProperty("date_last_updated", rs.getString("date_last_updated"));
				recordObject.addProperty("last_modified_by", rs.getString("last_modified_by"));
				result = recordObject;
			}
			conn.close();

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading categories of products. Exception Details:" + ex);
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Insert Category
	public JsonObject insertCategory(String category_name, String category_description, String last_modified_by) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "INSERT INTO `research_category`(`category_name`, `category_description`,`last_modified_by`) VALUES(?,?,?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, category_name);
			preparedStmt.setString(2, category_description);
			preparedStmt.setString(3, last_modified_by);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Category inserted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to insert the Category.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while inserting the category. Exception Details:" + ex);
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Update Category
	public JsonObject updateCategory(String category_id, String category_name, String category_description, String last_modified_by)
	{
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "UPDATE `research_category` SET `category_name` = ?, `category_description` = ?, `last_modified_by` = ? WHERE `category_id` = ?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, category_name);
			preparedStmt.setString(2, category_description);
			preparedStmt.setString(3, last_modified_by);
			preparedStmt.setString(4, category_id);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Category updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to update Category.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while updating Category. Exception Details:" + ex);
			System.err.println(ex.getMessage());
		}
		return result;
	}

	//Delete Category
	public JsonObject deleteCategory(String category_id) {
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

			String query = "DELETE FROM `research_category` WHERE `category_id` = ?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, category_id);
			status = preparedStmt.executeUpdate();

			result = new JsonObject();
			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Category deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to delete Category.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while deleting Category. Exception Details:" + ex);
			System.err.println(ex.getMessage());
		}
		return result;
	}
}