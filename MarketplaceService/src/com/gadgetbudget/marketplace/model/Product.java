package com.gadgetbudget.marketplace.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.gadgetbudget.marketplace.util.DBHandler;
import com.google.gson.JsonObject;

public class Product extends DBHandler{

	public JsonObject insertProduct(String researcherID, String productName,  String productDescription, String catID, int availableItems, double price ) {
		 
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
			preparedStmt.setString(1, researcherID); 
			preparedStmt.setString(2, productName); 
			preparedStmt.setString(3, productDescription);
			preparedStmt.setString(4, catID);
			preparedStmt.setInt(5, availableItems);
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
	
}
