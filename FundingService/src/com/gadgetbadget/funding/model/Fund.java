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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Fund extends DBHandler{
	
	//Read Funds
	public JsonObject readFunds() {
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

			String query = "SELECT * FROM `fund`;";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

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
			result.add("payments", resultArray);

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
				recordObject.addProperty("creditcard_no", rs.getFloat("creditcard_no"));
				resultArray.add(recordObject);

			}
			conn.close();

			result = new JsonObject();
			result.add("payments", resultArray);

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Error occurred while reading payments for the given product. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
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

			String query = "SELECT ROUND((`funded_amount` * (`service_charge_rate`)/100),2) AS 'fund_profit' FROM `fund` WHERE 1;";
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
				recordObject.addProperty("sales_profit", rs.getString("fund_profit"));
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


}
