package com.gadgetbudget.marketplace.model;


import java.sql.Connection;
import java.sql.PreparedStatement;

import com.gadgetbudget.marketplace.util.DBHandler;
import com.google.gson.JsonObject;


public class Product_Category extends DBHandler{

	//insert product category
	public JsonObject insertProductCategory(String catName, String catDesc,  String lastModified) {
		 
		JsonObject result = null;
		String output = "";
		try {
			Connection con = connect();
			if (con == null) 
			{ 
				 result = new JsonObject();
				 result.addProperty("Messege", "Error while connecting to the database");
			}
			
			String query = "INSERT INTO `product_category`( `category_name`, `category_description`, `last_modified_by`) "
					+ "VALUES ([value-name],[value-description],[always give AD21000001(admin id)])";

			PreparedStatement preparedStmt = con.prepareStatement(query);
			
			
			// binding values 
			preparedStmt.setString(1, catName); 
			preparedStmt.setString(2, catDesc); 
			preparedStmt.setString(3, lastModified);

			//execute the statement
			preparedStmt.execute(); 
			
			
			output = "Inserted Successfully";
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
