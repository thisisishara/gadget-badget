package com.gadgetbadget.funding.model;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;


import com.gadgetbadget.funding.util.DBHandler;
import com.gadgetbadget.funding.util.InterServiceCommHandler;
import com.gadgetbadget.funding.util.ValidationHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Fund extends DBHandler{
	
	//Read Funds
		public JsonObject readFunds(String funder_id) {
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

				String query =  null;
				PreparedStatement stmt =  null;
				ResultSet rs = null;
				
				if (funder_id == null) {
					query = "SELECT * FROM `fund`;";
					stmt = conn.prepareStatement(query);
					
				} else {
					query = "SELECT * FROM `fund` WHERE `funder_id`=?;";
					stmt = conn.prepareStatement(query);
					stmt.setString(1, funder_id);				
				}
				
				rs = stmt.executeQuery();

				if(!rs.isBeforeFirst()) {
					result = new JsonObject();
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE","Request Processed. No funds found.");
					return result;
				}

				while (rs.next())
				{
					JsonObject recordObject = new JsonObject();
					recordObject.addProperty("fund_id", rs.getString("fund_id"));
					recordObject.addProperty("funder_id", rs.getString("funder_id"));
					recordObject.addProperty("research_id", rs.getString("research_id"));
					recordObject.addProperty("funded_amount", rs.getFloat("funded_amount"));
					recordObject.addProperty("date_funded", rs.getString("date_funded"));
					recordObject.addProperty("service_charge_rate", rs.getFloat("service_charge_rate"));
					recordObject.addProperty("creditcard_no", rs.getFloat("creditcard_no"));
					resultArray.add(recordObject);
				}
				conn.close();

				result = new JsonObject();
				result.add("funds", resultArray);

			}
			catch (Exception ex)
			{
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Error occurred while reading funds. Exception Details:" + ex.getMessage());
				System.err.println(ex.getMessage());
			}
			return result;
		}

		//Read Fund Summary using funder_id
		public JsonObject readFundSummeryByFunderId(String funder_id) {
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

				CallableStatement callableStmt = conn.prepareCall("{call sp_funding_summary_funderid(?, ?, ?, ?, ?, ?)}");

				//Input parameter binding
				callableStmt.setString(1, funder_id);
				
				//output parameter registering
				callableStmt.registerOutParameter(2, Types.INTEGER);
				callableStmt.registerOutParameter(3, Types.TIMESTAMP);
				callableStmt.registerOutParameter(4, Types.DECIMAL);
				callableStmt.registerOutParameter(5, Types.INTEGER);
				callableStmt.registerOutParameter(6, Types.TIMESTAMP);


				callableStmt.execute();

				int no_fundings_placed = (int) callableStmt.getInt(2);
				String latest_funding_date = ((Timestamp) callableStmt.getTimestamp(3)).toString();
				double total_fundings = ((BigDecimal) callableStmt.getBigDecimal(4)).doubleValue();
				int total_research_funded = (int) callableStmt.getInt(5);
				String retrieved_date = ((Timestamp)  callableStmt.getTimestamp(6)).toString();

				result = new JsonObject();			
				result.addProperty("no_fundings_placed", no_fundings_placed);
				result.addProperty("latest_funding_date", latest_funding_date);
				result.addProperty("total_fundings", total_fundings);
				result.addProperty("total_research_funded", total_research_funded);
				result.addProperty("retrieved_date", retrieved_date);
				return result;

			}
			catch (Exception ex)
			{
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Error occurred while reading fund statistics. Exception Details:" + ex);
				ex.printStackTrace();
			}
			return result;
		}

		//Read Funds By funder id and fund_id
		public JsonObject readFund(String funder_id, String fund_id) {
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

				if(funder_id == null) {

					query = "SELECT * FROM `fund` WHERE `fund_id`=?;";
					PreparedStatement preparedStmt = conn.prepareStatement(query);

					preparedStmt.setString(1, fund_id);
					rs = preparedStmt.executeQuery();

				} else {
					query = "SELECT * FROM `fund` WHERE `fund_id`=? AND `funder_id` = ?;";
					PreparedStatement preparedStmt = conn.prepareStatement(query);

					preparedStmt.setString(1, fund_id);
					preparedStmt.setString(2, funder_id);
					rs = preparedStmt.executeQuery();
				}

				if(!rs.isBeforeFirst()) {
					result = new JsonObject();
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE","Request Processed. No funds found for the given ID.");
					return result;
				}

				while (rs.next())
				{
					JsonObject recordObject = new JsonObject();
					recordObject.addProperty("fund_id", rs.getString("fund_id"));
					recordObject.addProperty("funder_id", rs.getString("funder_id"));
					recordObject.addProperty("research_id", rs.getString("research_id"));
					recordObject.addProperty("funded_amount", rs.getFloat("funded_amount"));
					recordObject.addProperty("date_funded", rs.getString("date_funded"));
					recordObject.addProperty("service_charge_rate", rs.getFloat("service_charge_rate"));
					recordObject.addProperty("creditcard_no", rs.getFloat("creditcard_no"));
					result = recordObject;
				}
				conn.close();

			}
			catch (Exception ex)
			{
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Error occurred while reading funds for the given ID. Exception Details:" + ex.getMessage());
				System.err.println(ex.getMessage());
			}
			return result;
		}
		
		//Read Funds By research id
		public JsonObject readFundsByResearchId(String research_id) {
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

				String query = "SELECT * FROM `fund` WHERE `research_id`= ?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, research_id);
				ResultSet rs = preparedStmt.executeQuery();

				if(!rs.isBeforeFirst()) {
					result = new JsonObject();
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE","Request Processed. No funds found for the given research.");
					return result;
				}

				while (rs.next())
				{
					JsonObject recordObject = new JsonObject();
					recordObject.addProperty("fund_id", rs.getString("fund_id"));
					recordObject.addProperty("funder_id", rs.getString("funder_id"));
					recordObject.addProperty("research_id", rs.getString("research_id"));
					recordObject.addProperty("funded_amount", rs.getFloat("funded_amount"));
					recordObject.addProperty("date_funded", rs.getString("date_funded"));
					recordObject.addProperty("service_charge_rate", rs.getFloat("service_charge_rate"));
					resultArray.add(recordObject);

				}
				conn.close();

				result = new JsonObject();
				result.add("funds", resultArray);

			}
			catch (Exception ex)
			{
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Error occurred while reading funds for the given product. Exception Details:" + ex.getMessage());
				System.err.println(ex.getMessage());
			}
			return result;
		}

		// fund profit calculation
		public JsonObject calculateFundProfit() {
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

				String query = "SELECT SUM( ROUND((`funded_amount` * (`service_charge_rate`)/100),2) ) AS 'fund_profit' FROM `fund` WHERE 1;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);
				
				ResultSet rs = preparedStmt.executeQuery();

				if(!rs.isBeforeFirst()) {
					result = new JsonObject();
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE","Request Processed. No funds found.");
					return result;
				}

				while (rs.next())
				{
					JsonObject recordObject = new JsonObject();
					recordObject.addProperty("funds_profit", rs.getString("fund_profit"));
					recordObject.addProperty("timestamp", new Timestamp(System.currentTimeMillis()).toString());
					result = recordObject;

				}
				conn.close();
			}
			catch (Exception ex)
			{
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Error occurred while calculating fund profit. Exception Details:" + ex.getMessage());
				System.err.println(ex.getMessage());
			}
			return result;
		}
		

		//Insert Fund
		public JsonObject insertFund(String funder_id, String research_id, float funded_amount, String creditcard_no) {
			JsonObject result = null;
			try {
				//verify fund payment using user service
				JsonObject payload = new JsonObject();
				payload.addProperty("creditcard_no", creditcard_no);

				JsonObject paymentMethodDetails = new InterServiceCommHandler().userIntercomms("users/funders/" + funder_id + "/payment-methods?retrieve=true", payload);
				if(! new ValidationHandler().verifyPaymentMethod(paymentMethodDetails)) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE", "Credit card is not valid. Try a different credit card.");
					return result; 
				}

				Connection conn = getConnection();
				if (conn == null) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
					return result; 
				}

				String query = "INSERT INTO `fund`(`funder_id`, `research_id`,`funded_amount`, `creditcard_no`) VALUES(?,?,?,?);";
				PreparedStatement preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, funder_id);
				preparedStmt.setString(2, research_id);
				preparedStmt.setFloat(3, funded_amount);
				preparedStmt.setString(4, creditcard_no);

				int status = preparedStmt.executeUpdate();
				conn.close();
				
				result = new JsonObject();

				if(status > 0) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", "Fund placed successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Unable to place the fund.");
				}
			}
			catch (Exception ex) {
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Error occurred while placing the fund. Exception Details:" + ex.getMessage());
				System.err.println(ex.getMessage());
			}
			return result;
		}

		//Update Fund
		public JsonObject updateFund(String fund_id, String funder_id, String research_id, float funded_amount, String creditcard_no)
		{
			JsonObject result = null;
			try {
				//Verify fund payment using user service
				JsonObject payload = new JsonObject();
				payload.addProperty("creditcard_no", creditcard_no);

				JsonObject paymentMethodDetails = new InterServiceCommHandler().userIntercomms("users/funders/" + funder_id + "/payment-methods?retrieve=true", payload);
				if(! new ValidationHandler().verifyPaymentMethod(paymentMethodDetails)) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE", "Credit card is not valid. Try a different credit card.");
					return result; 
				}

				Connection conn = getConnection();
				if (conn == null) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
					return result; 
				}

				String query = "UPDATE `fund` SET `funder_id` = ?, `research_id` = ?, `funded_amount` = ?, `creditcard_no` = ? WHERE `fund_id` = ?;";
				PreparedStatement preparedStmt = conn.prepareStatement(query);

				preparedStmt.setString(1, funder_id);
				preparedStmt.setString(2, research_id);
				preparedStmt.setFloat(3, funded_amount);
				preparedStmt.setString(4, creditcard_no);
				preparedStmt.setString(5, fund_id);

				int status = preparedStmt.executeUpdate();
				conn.close();

				result = new JsonObject();

				if(status > 0) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", "Fund updated successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Unable to update fund.");
				}
			}
			catch (Exception ex) {
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Error occurred while updating fund. Exception Details:" + ex.getMessage());
				System.err.println(ex.getMessage());
			}
			return result;
		}

		//Delete Fund
		public JsonObject deleteFund(String funder_id, String fund_id) {
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

				if(funder_id == null) {
					query = "DELETE FROM `fund` WHERE `fund_id` = ?;";
					preparedStmt = conn.prepareStatement(query);
					preparedStmt.setString(1, fund_id);				
					
				} else {
					query = "DELETE FROM `fund` WHERE `fund_id` = ? AND `funder_id` = ?;";
					preparedStmt = conn.prepareStatement(query);
					preparedStmt.setString(1, fund_id);
					preparedStmt.setString(2, funder_id);
				}
				
				status = preparedStmt.executeUpdate();

				result = new JsonObject();
				if(status > 0) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", "Fund deleted successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Unable to delete fund.");
				}
			}
			catch (Exception ex) {
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Error occurred while deleting fund. Exception Details:" + ex.getMessage());
				System.err.println(ex.getMessage());
			}
			return result;
		}

	}



