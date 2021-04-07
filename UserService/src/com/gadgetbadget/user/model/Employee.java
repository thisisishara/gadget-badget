package com.gadgetbadget.user.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import com.google.gson.JsonObject;

public class Employee extends User{
	
	//Insert Employee
	public String insertEmployee() {
		JsonObject result = null;
		int row_count = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("ERROR", "Operation has been terminated due to a database connectivity issue.");
				return result.toString(); 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_insert_employee(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

			java.util.Date utilDate = new java.util.Date();
			java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());

			//output parameter registering
			callableStmt.registerOutParameter(12, Types.INTEGER);

			//Input parameter binding
			callableStmt.setString(1, "new");
			callableStmt.setString(2, "new");
			callableStmt.setString(3, "EMPLY");
			callableStmt.setString(4, "new");
			callableStmt.setString(5, "new");
			callableStmt.setString(6, "F");
			callableStmt.setString(7, "new");
			callableStmt.setString(8, "new");
			callableStmt.setString(9, "new");
			callableStmt.setString(10, "new");
			callableStmt.setDate(11, sqlDate);

			callableStmt.execute();

			//test
			row_count = (int) callableStmt.getInt(12);
			result = new JsonObject();
			result.addProperty("RESULT", String.valueOf(row_count));

		}
		catch (Exception e) {
			result = new JsonObject();
			e.printStackTrace();
			result.addProperty("ERROR", "Error occurred while inserting user-role."+e.getMessage());
			System.err.println(e.getMessage());
		}
		return result.toString();

	}
	
	// read employees
	
	// update employees
	
	// delete employees
}
