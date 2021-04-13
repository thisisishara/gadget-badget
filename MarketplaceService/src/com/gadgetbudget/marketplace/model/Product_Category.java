package com.gadgetbudget.marketplace.model;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.gadgetbudget.marketplace.util.DBHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class Product_Category extends DBHandler{

	//insert product category
	public JsonObject insertProductCategory(String catName, String catDesc,  String lastModified) {
		 
		JsonObject result = null;
		
		try {
			Connection con = connect();
			if (con == null) 
			{ 
				 result = new JsonObject();
				 result.addProperty("STATUS","ERROR");
				 result.addProperty("Messege", "Error while connecting to the database");
				 return result;
			}
			
			String query = "INSERT INTO `product_category`( `category_name`, `category_description`, `last_modified_by`) "
					+ "VALUES (?,?,?);";

			PreparedStatement preparedStmt = con.prepareStatement(query);
			
			
			// binding values 
			preparedStmt.setString(1, catName); 
			preparedStmt.setString(2, catDesc); 
			preparedStmt.setString(3, lastModified);

			//execute the statement
			int status = preparedStmt.executeUpdate();
			con.close();
			result = new JsonObject();
			
			//testing
			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("Message", "Product_ Category Inserted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("Message", "Unable to Insert Product_Category.");
			}	
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("Message", "Problem with inserting product_category");
			System.err.println(e.getMessage());
		}
		
		return result;
	}
	
	//read all the product categories
	public JsonObject readAllProductCategory() {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();	
		
		try {
			//connection
			Connection con = connect();
			if (con == null) 
			{ 
				 result = new JsonObject();
				 result.addProperty("STATUS","ERROR");
				 result.addProperty("Messege", "Error while connecting to the database");
				 return result;
			}
			
			//SQL query
			String query = "SELECT p.`category_id`, p.`category_name`, p.`category_description`"
					+ " FROM `product_category` p";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				JsonObject categoryObject = new JsonObject();
				categoryObject.addProperty("category_id", rs.getString("category_id"));
				categoryObject.addProperty("category_name", rs.getString("category_name"));
				categoryObject.addProperty("category_description", rs.getString("category_description"));
				resultArray.add(categoryObject);
			}
			con.close();
			
			result = new JsonObject();
			result.add("products", resultArray);
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("Message", "Problem with reading product_categories");
			System.err.println(e.getMessage());
		}
		return result;
	}
	
	//read selected product_category
	public JsonObject readProductCategory(String category_id) {
		JsonObject result = null;
		JsonArray resultsetArray = new JsonArray();	
			
		try {
			//connection
			Connection con = connect();
			if (con == null) 
			{ 
				 result = new JsonObject();
				 result.addProperty("STATUS","ERROR");
				 result.addProperty("Messege", "Error while connecting to the database");
				 return result;
			}
			
			//SQL query
			String query = "SELECT * FROM `product_category` "
					+ " WHERE `category_id` = " + category_id;
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","No Categories found.");
				return result;
			}
			
			while(rs.next()) {
				JsonObject categoryObject = new JsonObject();
				categoryObject.addProperty("category_id", rs.getString("category_id"));
				categoryObject.addProperty("category_name", rs.getString("category_name"));
				categoryObject.addProperty("category_description", rs.getString("category_description"));
				categoryObject.addProperty("date_last_updated", rs.getString("date_last_updated"));
				categoryObject.addProperty("last_modified_by", rs.getString("last_modified_by"));
				resultsetArray.add(categoryObject);
			}
			con.close();
			
			result = new JsonObject();
			result.add("product categories", resultsetArray);
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("Message", "Problem with reading product_category");
			System.err.println(e.getMessage());
		}
		
		return result;
	}
	
	//update product_category
	public JsonObject updateProductCategory(String catID, String catName, String catDesc, String lastModified) {
		JsonObject result = null;
		
		try {
			//connection
			Connection con = connect();
			if (con == null) 
			{ 
				 result = new JsonObject();
				 result.addProperty("STATUS","ERROR");
				 result.addProperty("Messege", "Error while connecting to the database");
				 return result;
			}
			
			//SQL queries
			String query = "UPDATE `product_category` SET `category_name`=?,`category_description`=?, `last_modified_by`=? WHERE `category_id`=?;";
			PreparedStatement prpdstmt = con.prepareStatement(query);
			
			prpdstmt.setString(1, catName);
			prpdstmt.setString(2, catDesc);
			prpdstmt.setString(3, lastModified);
			prpdstmt.setString(4, catID);
			
			int status = prpdstmt.executeUpdate();
			con.close();
			result = new JsonObject();
			
			//testing
			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("Message", "Product_ Category updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("Message", "Unable to Update Product_Category.");
			}
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("Message", "Problem with updating product_category");
			System.err.println(e.getMessage());
		}
		return result;
	}
	
	//delete product_category
	public JsonObject deleteProductCategory(String catID) {
		JsonObject result = null;
		
		try {
			//connection
			Connection con = connect();
			if (con == null) 
			{ 
				 result = new JsonObject();
				 result.addProperty("STATUS","ERROR");
				 result.addProperty("Messege", "Error while connecting to the database");
				 return result;
			}
			
			//SQL Queries
			String query = "DELETE FROM `product_category` WHERE `category_id`=?;";
			PreparedStatement prpdstmt = con.prepareStatement(query);
			
			prpdstmt.setString(1, catID);
			
			int status = prpdstmt.executeUpdate();
			con.close();
			result = new JsonObject();
			
			//testing
			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("Message", "Product_ Category deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("Message", "Unable to delete Product_Category.");
			}
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("Message", "Problem with deleting product_category");
			System.err.println(e.getMessage());
		}
		
		return result;
		
		
	}
}


    