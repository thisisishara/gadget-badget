package com.gadgetbudget.marketplace.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.gadgetbudget.marketplace.util.DBHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Product extends DBHandler{

	//insert product method
	public JsonObject insertProduct(String researcher_id, String product_name,  String product_description, String category_id, int available_items, double price ) {
		 
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
			
			String query = "INSERT INTO `product`(`researcher_id`, `product_name`, `product_description`, `category_id`,`available_items`, `price`)"
					+ " VALUES (?,?,?,?,?,?); ";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			
			
			// binding values 
			preparedStmt.setString(1, researcher_id); 
			preparedStmt.setString(2, product_name); 
			preparedStmt.setString(3, product_description);
			preparedStmt.setString(4, category_id);
			preparedStmt.setInt(5, available_items);
			preparedStmt.setDouble(6, price);

			//execute the statement
			int status = preparedStmt.executeUpdate();
			con.close();
			result = new JsonObject();
			
			//testing
			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("Message", "Product Inserted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("Message", "Unable to Insert Product.");
			}	
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("Message", "Problem with inserting product");
			System.err.println(e.getMessage());
		}
		
		return result;
	}
	
	//read all products
	public JsonObject readAllProducts() {
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
			String query = "SELECT p.`researcher_id`, p.`product_name`, p.`product_description`,p.`category_id`,p.`available_items`, p.`price`"
					+ " FROM `product` p";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()) {
				JsonObject productObject = new JsonObject();
				productObject.addProperty("product_id", rs.getString("product_id"));
				productObject.addProperty("researcher_id", rs.getString("researcher_id"));
				productObject.addProperty("product_name", rs.getString("product_name"));
				productObject.addProperty("product_description", rs.getString("product_description"));
				productObject.addProperty("category_id", rs.getString("category_id"));
				productObject.addProperty("available_items", rs.getInt("available_items"));
				productObject.addProperty("price", rs.getDouble("price"));
				resultsetArray.add(productObject);
			}
			con.close();
			
			result = new JsonObject();
			result.add("products", resultsetArray);
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("Message", "Problem with reading product_categories");
			System.err.println(e.getMessage());
		}
		return result;
	}
	
	//read product method
	public JsonObject readProduct(String product_ID) {
		JsonObject result = null;
		JsonArray resultsetArray = new JsonArray();
		
		try {
			Connection con = connect();
			if (con == null) 
			{ 
				 result = new JsonObject();
				 result.addProperty("STATUS","ERROR");
				 result.addProperty("Messege", "Error while connecting to the database");
				 return result;
			}
			
			//SQL queries
			String query = "SELECT * FROM `product` WHERE `product_id` = " + product_ID;
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","No Categories found.");
				return result;
			}
			
			while(rs.next()) {
				JsonObject productObject = new JsonObject();
				productObject.addProperty("product_id", rs.getString("product_id"));
				productObject.addProperty("researcher_id", rs.getString("researcher_id"));
				productObject.addProperty("product_name", rs.getString("product_name"));
				productObject.addProperty("product_description", rs.getString("product_description"));
				productObject.addProperty("category_id", rs.getString("category_id"));
				productObject.addProperty("available_items", rs.getInt("available_items"));
				productObject.addProperty("price", rs.getDouble("price"));
				productObject.addProperty("date_added", rs.getString("date_added"));
				resultsetArray.add(productObject);
			}
			
			con.close();
			result = new JsonObject();
			result.add("products", resultsetArray);
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("Message", "Problem with reading product");
			System.err.println(e.getMessage());
		}
		
		return result;
	}
	
	//update product method
	public JsonObject updateProduct(String product_id, String researcherID, String productName,  String productDescription, String catID, int availableItems, double price ) {
		 
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
			
			String query = "UPDATE `product` SET `researcher_id` = ?, `product_name` = ?, `product_description` = ?, `category_id` + ?,`available_items` = ?, `price` = ?"
					+ "WHERE product_id = ?;";
			PreparedStatement preparedStmt = con.prepareStatement(query);
			
			
			// binding values 
			preparedStmt.setString(1, researcherID); 
			preparedStmt.setString(2, productName); 
			preparedStmt.setString(3, productDescription);
			preparedStmt.setString(4, catID);
			preparedStmt.setInt(5, availableItems);
			preparedStmt.setDouble(6, price);
			preparedStmt.setString(7, product_id);
			

			//execute the statement
			int status = preparedStmt.executeUpdate();
			con.close();
			result = new JsonObject();
			
			//testing
			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("Message", "Product Updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("Message", "Unable to Update Product.");
			}	
		}
		catch (Exception e) {
			result = new JsonObject();
			result.addProperty("Message", "Problem with updating product");
			System.err.println(e.getMessage());
		}
		
		return result;
	}
	
	//delete product method
	public JsonObject deleteProduct(String product_id) {
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
			String query = "DELETE FROM `product` WHERE `product_id`=?;";
			PreparedStatement prpdstmt = con.prepareStatement(query);
			
			prpdstmt.setString(1, product_id);
			
			int status = prpdstmt.executeUpdate();
			con.close();
			result = new JsonObject();
			
			//testing
			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("Message", "Product deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("Message", "Unable to Delete Product.");
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
