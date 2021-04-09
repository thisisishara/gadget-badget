package com.gadgetbadget.user.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import com.gadgetbadget.user.util.DBOpStatus;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Employee extends User{

	//Insert Employee
	public JsonObject insertEmployee(String username, String password, String role_id, String first_name, String last_name, String gender, String primary_email, String primary_phone, String gb_employee_id, String department, String date_hired) {
		JsonObject result = null;
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_insert_employee(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

			callableStmt.registerOutParameter(12, Types.INTEGER);

			callableStmt.setString(1, username);
			callableStmt.setString(2, password);
			callableStmt.setString(3, role_id);
			callableStmt.setString(4, first_name);
			callableStmt.setString(5, last_name);
			callableStmt.setString(6, gender);
			callableStmt.setString(7, primary_email);
			callableStmt.setString(8, primary_phone);
			callableStmt.setString(9, gb_employee_id);
			callableStmt.setString(10, department);
			callableStmt.setString(11, date_hired);

			callableStmt.execute();

			status = (int) callableStmt.getInt(12);
			result = new JsonObject();			
			
			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", "Employee Inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Insert Employee.");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while inserting Employee. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	// read employees
	public JsonObject readEmployees() {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			String query = "SELECT u.user_id, u.first_name, u.last_name, u.gender, u.primary_email, u.primary_phone, e.gb_employee_id, u.role_id, e.department, e.date_hired FROM `user` u, `employee` e WHERE u.user_id=e.employee_id";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if(!rs.isBeforeFirst()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE","No employees found.");
				return result;
			}

			while (rs.next())
			{
				JsonObject recordObject = new JsonObject();
				recordObject.addProperty("user_id", rs.getString("user_id"));
				recordObject.addProperty("role_id", rs.getString("role_id"));
				recordObject.addProperty("first_name", rs.getString("first_name"));
				recordObject.addProperty("last_name", rs.getString("last_name"));
				recordObject.addProperty("gender", rs.getString("gender"));
				recordObject.addProperty("primary_email", rs.getString("primary_email"));
				recordObject.addProperty("primary_phone", rs.getString("primary_phone"));
				recordObject.addProperty("gb_employee_id", rs.getString("gb_employee_id"));
				recordObject.addProperty("department", rs.getString("department"));
				recordObject.addProperty("date_hired", rs.getString("date_hired"));
				resultArray.add(recordObject);
			}
			conn.close();

			result = new JsonObject();
			result.add("employees", resultArray);

		}
		catch (Exception ex)
		{
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while reading employees. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}

	
	// update employees
	public JsonObject updateEmployee(String user_id,String username, String password, String first_name, String last_name, String gender, String primary_email, String primary_phone, String gb_employee_id, String department, String date_hired)
	{
		JsonObject result = null;
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_update_employee(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");

			callableStmt.registerOutParameter(12, Types.INTEGER);

			callableStmt.setString(1, user_id);
			callableStmt.setString(2, username);
			callableStmt.setString(3, password);
			callableStmt.setString(4, first_name);
			callableStmt.setString(5, last_name);
			callableStmt.setString(6, gender);
			callableStmt.setString(7, primary_email);
			callableStmt.setString(8, primary_phone);
			callableStmt.setString(9, gb_employee_id);
			callableStmt.setString(10, department);
			callableStmt.setString(11, date_hired);

			callableStmt.execute();

			status = (int) callableStmt.getInt(12);
			result = new JsonObject();			
			
			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", "Employee " + user_id + " Updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Update Employee " + user_id +".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while updating Employee " + user_id +". Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
	

	// delete employees
	public JsonObject deleteEmployee(String user_id) {
		JsonObject result = null;
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE", "Operation has been terminated due to a database connectivity issue.");
				return result; 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_delete_employee(?, ?)}");

			callableStmt.registerOutParameter(2, Types.INTEGER);

			callableStmt.setString(1, user_id);

			callableStmt.execute();

			status = (int) callableStmt.getInt(2);
			result = new JsonObject();			
			
			if(status > 0) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", "Employee " + user_id + " deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Unable to Delete Employee "+ user_id +".");
			}
		}
		catch (Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Error occurred while deleting Employee. Exception Details:" + ex.getMessage());
			System.err.println(ex.getMessage());
		}
		return result;
	}
}
