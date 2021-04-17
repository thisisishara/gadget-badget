package com.gadgetbadget.user.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;

import com.gadgetbadget.user.util.JsonResponseBuilder;
import com.gadgetbadget.user.util.UserType;
import com.gadgetbadget.user.util.ValidationHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * This class represents employees and is a type of users. Performs 
 * database operations related to employees, thus Extends the DBHandler.
 * 
 * @author Ishara_Dissanayake
 */
public class Employee extends User{

	//Insert Employee
	public JsonObject insertEmployee(String username, String password, String role_id, String first_name, String last_name, String gender, String primary_email, String primary_phone, String gb_employee_id, String department, String date_hired) {
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
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

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Employee Inserted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Insert Employee.");
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while inserting Employee. Exception Details:" + ex.getMessage());
		}
	}

	//Read Employees
	public JsonObject readEmployees() {
		JsonObject result = null;
		JsonArray resultArray = new JsonArray();
		try
		{
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "SELECT u.user_id, u.first_name, u.last_name, u.gender, u.primary_email, u.primary_phone, e.gb_employee_id, u.role_id, e.department, e.date_hired FROM `user` u, `employee` e WHERE u.user_id=e.employee_id";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("Request Processed. No employees found.");
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
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while reading employees. Exception Details:" + ex.getMessage());
		}
		return result;
	}

	//Read Employee By Id
	public JsonObject readEmployeeById(String employee_id) {
		JsonObject result = null;
		try
		{			
			// Verify requested ID Pattern
			if(!(new ValidationHandler().validateUserType(employee_id, UserType.EMPLY) || new ValidationHandler().validateUserType(employee_id, UserType.FNMGR) || new ValidationHandler().validateUserType(employee_id, UserType.ADMIN))) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format."); 
			}
			
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
			}

			String query = "SELECT u.user_id, u.first_name, u.last_name, u.gender, u.primary_email, u.primary_phone, e.gb_employee_id, u.role_id, e.department, e.date_hired FROM `user` u, `employee` e WHERE u.user_id=e.employee_id AND u.user_id = ?";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			
			preparedStmt.setString(1, employee_id);
			ResultSet rs = preparedStmt.executeQuery();

			if(!rs.isBeforeFirst()) {
				return new JsonResponseBuilder().getJsonFailedResponse("Request Processed. Employee not found.");
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
				result = recordObject;
			}
			conn.close();
			return result;

		}
		catch (Exception ex)
		{
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while reading employee. Exception Details:" + ex.getMessage());
		}
	}

	//Update Employee
	public JsonObject updateEmployee(String user_id,String username, String password, String first_name, String last_name, String gender, String primary_email, String primary_phone, String gb_employee_id, String department, String date_hired)
	{
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue.");
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

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Employee " + user_id + " Updated successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Update Employee " + user_id +".");
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while updating Employee " + user_id +". Exception Details:" + ex.getMessage());
		}
	}

	//Delete Employee
	public JsonObject deleteEmployee(String user_id) {
		int status = 0;

		try {
			Connection conn = getConnection();
			if (conn == null) {
				return new JsonResponseBuilder().getJsonErrorResponse("Operation has been terminated due to a database connectivity issue."); 
			}

			CallableStatement callableStmt = conn.prepareCall("{call sp_delete_employee(?, ?)}");

			callableStmt.registerOutParameter(2, Types.INTEGER);

			callableStmt.setString(1, user_id);

			callableStmt.execute();

			status = (int) callableStmt.getInt(2);		

			if(status > 0) {
				return new JsonResponseBuilder().getJsonSuccessResponse("Employee " + user_id + " deleted successfully.");
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Unable to Delete Employee "+ user_id +".");
			}
		}
		catch (Exception ex) {
			System.err.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Error occurred while deleting Employee. Exception Details:" + ex.getMessage());
		}
	}
}
