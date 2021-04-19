package com.gadgetbadget.marketplace.model;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

import com.gadgetbadget.marketplace.util.DBHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Product extends DBHandler{

	//insert product method
	public JsonObject insertProduct(String researcher_id, String product_name, String product_description, String category_id, int available_items, Float price) {
		JsonObject result = null;
		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "INSERT INTO `product`(`researcher_id`, `product_name`, `product_description`,`category_id`,`available_items`, `price`) VALUES(?, ?, ?, ?, ?, ?);";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, researcher_id);
			preparedStmt.setString(2, product_name);
			preparedStmt.setString(3, product_description);
			preparedStmt.setString(4, category_id);
			preparedStmt.setInt(5, available_items);
			preparedStmt.setFloat(6, price);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Product inserted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to insert the Product.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while inserting the product. Exception Details:" + ex);
			System.err.println(ex.getMessage());
		}
		return result;
	}
	
	//read all products
	public JsonObject readProducts(String researcher_id) {
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

			String query = null;
			PreparedStatement stmt = null;
			ResultSet rs = null;

			if(researcher_id == null) {
				query = "SELECT * FROM `product`;";
				stmt = conn.prepareStatement(query);
			} else {
				query = "SELECT * FROM `product` WHERE `researcher_id`=?;";
				stmt = conn.prepareStatement(query);
				stmt.setString(1, researcher_id);
			}

			rs = stmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No products found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("product_id", rs.getString("product_id"));
				recordObject.addProperty("researcher_id", rs.getString("researcher_id"));
				recordObject.addProperty("product_name", rs.getString("product_name"));
				recordObject.addProperty("product_description", rs.getString("product_description"));
				recordObject.addProperty("category_id", rs.getString("category_id"));
				recordObject.addProperty("available_items", rs.getString("available_items"));
				recordObject.addProperty("price", rs.getString("price"));
				recordObject.addProperty("date_added", rs.getString("date_added"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("products", resultArray);

		}
		catch (Exception e)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading products of products. Exception Details:" + e);
			System.err.println(e.getMessage());
		}
		return result;
	}
	
	//read product method
	public JsonObject readProduct(String researcher_id, String product_id) {
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

			String query = null;
			ResultSet rs = null;

			if(researcher_id == null) {

				query = "SELECT * FROM `product` WHERE `product_id`=?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, product_id);
				rs = preparedStmt.executeQuery();

			} else {
				query = "SELECT * FROM `product` WHERE `product_id`=? AND `researcher_id`=?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, product_id);
				preparedStmt.setString(2, researcher_id);
				rs = preparedStmt.executeQuery();
			}

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE","Request Processed. No products found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("product_id", rs.getString("product_id"));
				recordObject.addProperty("researcher_id", rs.getString("researcher_id"));
				recordObject.addProperty("product_name", rs.getString("product_name"));
				recordObject.addProperty("product_description", rs.getString("product_description"));
				recordObject.addProperty("category_id", rs.getString("category_id"));
				recordObject.addProperty("available_items", rs.getString("available_items"));
				recordObject.addProperty("price", rs.getString("price"));
				recordObject.addProperty("date_added", rs.getString("date_added"));
				result = recordObject;
			}
			conn.close();

		}
		catch (Exception e)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading products of products. Exception Details:" + e);
			System.err.println(e.getMessage());
		}
		return result;
	}
	
	//read Product Summary of researcher_id
	public JsonObject readProductSummeryByResearcherId(String researcher_id) {
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

			CallableStatement callableStmt = conn.prepareCall("{call sp_product_summary_resid(?, ?, ?, ?, ?, ?)}");

			//Input parameter binding
			callableStmt.setString(1, researcher_id);

			//output parameter registering
			callableStmt.registerOutParameter(2, Types.INTEGER);
			callableStmt.registerOutParameter(3, Types.TIMESTAMP);
			callableStmt.registerOutParameter(4, Types.DECIMAL);
			callableStmt.registerOutParameter(5, Types.INTEGER);
			callableStmt.registerOutParameter(6, Types.TIMESTAMP);


			callableStmt.execute();

			int no_products_in_market = (int) callableStmt.getInt(2);
			String latest_product_released_date = ((Timestamp) callableStmt.getTimestamp(3)).toString();
			double networth_of_products = ((BigDecimal) callableStmt.getBigDecimal(4)).doubleValue();
			int no_prod_categories = (int) callableStmt.getInt(5);
			String retrieved_date = ((Timestamp)  callableStmt.getTimestamp(6)).toString();

			result = new JsonObject();			
			result.addProperty("no_products_in_market", no_products_in_market);
			result.addProperty("latest_product_released_date", latest_product_released_date);
			result.addProperty("networth_of_products", networth_of_products);
			result.addProperty("no_prod_categories", no_prod_categories);
			result.addProperty("retrieved_date", retrieved_date);
			return result;

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading product statistics of the given researcher. Exception Details:" + ex);
			ex.printStackTrace();
		}
		return result;
	}
	
	//update product method
	public JsonObject updateProduct(String product_id, String researcher_id, String product_name, String product_description, String category_id, int available_items, Float price)
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

			String query = "UPDATE `product` SET `researcher_id` = ?, `product_name` = ?, `product_description` = ?, `category_id` = ?, `available_items` = ?, `price` = ? WHERE `product_id` = ?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);

			preparedStmt.setString(1, researcher_id);
			preparedStmt.setString(2, product_name);
			preparedStmt.setString(3, product_description);
			preparedStmt.setString(4, category_id);
			preparedStmt.setInt(5, available_items);
			preparedStmt.setFloat(6, price);
			preparedStmt.setString(7, product_id);

			int status = preparedStmt.executeUpdate();
			conn.close();

			result = new JsonObject();

			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Product updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to update Product.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while updating Product. Exception Details:" + ex);
			System.err.println(ex.getMessage());
		}
		return result;
	}
	
	//delete product method
	public JsonObject deleteProduct(String researcher_id, String product_id) {
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
				query = "DELETE FROM `product` WHERE `product_id` = ?;";
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, product_id);				

			} else {
				query = "DELETE FROM `product` WHERE `product_id` = ? AND `researcher_id` = ?;";
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.setString(1, product_id);
				preparedStmt.setString(2, researcher_id);
			}

			status = preparedStmt.executeUpdate();

			result = new JsonObject();
			if(status > 0) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", "Product deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Unable to delete Product.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while deleting Product. Exception Details:" + ex);
			System.err.println(ex.getMessage());
		}
		return result;
	}
	
	// Get product ownership
	public boolean isOwner(String researcher_id, String product_id) {
		try
		{			
			Connection conn = getConnection();
			if (conn == null) {
				return false; 
			}

			String query = "SELECT * FROM `product` WHERE `product_id`=? AND `researcher_id`=?;";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.setString(1, product_id);
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
