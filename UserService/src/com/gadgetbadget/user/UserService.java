package com.gadgetbadget.user;	

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gadgetbadget.user.model.Consumer;
import com.gadgetbadget.user.model.Employee;
import com.gadgetbadget.user.model.Funder;
import com.gadgetbadget.user.model.PaymentInfo;
import com.gadgetbadget.user.model.Researcher;
import com.gadgetbadget.user.model.User;
import com.gadgetbadget.user.util.DBOpStatus;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/users")
public class UserService {	
	User user = new User();
	Employee employee = new Employee();
	Researcher researcher = new Researcher();
	Funder funder = new Funder();
	Consumer consumer = new Consumer();
	PaymentInfo paymentInfo = new PaymentInfo();


	//Employee End-points
	@GET
	@Path("/employees")
	@Produces(MediaType.APPLICATION_JSON)
	public String readEmployees() {
		return employee.readEmployees().toString();
	}

	@POST
	@Path("/employees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertEmployee(String employeeJSON)
	{
		JsonObject result = null;

		try {

			JsonObject employeeJSON_parsed = new JsonParser().parse(employeeJSON).getAsJsonObject();

			if(!employeeJSON_parsed.has("employees")) {
				return (employee.insertEmployee(employeeJSON_parsed.get("username").getAsString(), employeeJSON_parsed.get("password").getAsString(), employeeJSON_parsed.get("role_id").getAsString(), employeeJSON_parsed.get("first_name").getAsString(), employeeJSON_parsed.get("last_name").getAsString(), employeeJSON_parsed.get("gender").getAsString(), employeeJSON_parsed.get("primary_email").getAsString(), employeeJSON_parsed.get("primary_phone").getAsString(), employeeJSON_parsed.get("gb_employee_id").getAsString(), employeeJSON_parsed.get("department").getAsString(), employeeJSON_parsed.get("date_hired").getAsString())).toString();
			} else if (!employeeJSON_parsed.get("employees").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = employeeJSON_parsed.get("employees").getAsJsonArray().size();

			for (JsonElement employeeElem : employeeJSON_parsed.get("employees").getAsJsonArray()) {
				JsonObject employeeObj = employeeElem.getAsJsonObject();
				JsonObject response = (employee.insertEmployee(employeeObj.get("username").getAsString(), employeeObj.get("password").getAsString(), employeeObj.get("role_id").getAsString(), employeeObj.get("first_name").getAsString(), employeeObj.get("last_name").getAsString(), employeeObj.get("gender").getAsString(), employeeObj.get("primary_email").getAsString(), employeeObj.get("primary_phone").getAsString(), employeeObj.get("gb_employee_id").getAsString(), employeeObj.get("department").getAsString(), employeeObj.get("date_hired").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFULL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", insertCount + " Employees were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Employees were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Employees.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}
	
	@PUT
	@Path("/employees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateEmployee(String employeeJSON)
	{
		JsonObject result = null;

		try {

			JsonObject employeeJSON_parsed = new JsonParser().parse(employeeJSON).getAsJsonObject();

			if(!employeeJSON_parsed.has("employees")) {
				return (employee.updateEmployee(employeeJSON_parsed.get("user_id").getAsString(), employeeJSON_parsed.get("username").getAsString(), employeeJSON_parsed.get("password").getAsString(), employeeJSON_parsed.get("first_name").getAsString(), employeeJSON_parsed.get("last_name").getAsString(), employeeJSON_parsed.get("gender").getAsString(), employeeJSON_parsed.get("primary_email").getAsString(), employeeJSON_parsed.get("primary_phone").getAsString(), employeeJSON_parsed.get("gb_employee_id").getAsString(), employeeJSON_parsed.get("department").getAsString(), employeeJSON_parsed.get("date_hired").getAsString())).toString();
			} else if (!employeeJSON_parsed.get("employees").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = employeeJSON_parsed.get("employees").getAsJsonArray().size();
			
			for (JsonElement employeeElem : employeeJSON_parsed.get("employees").getAsJsonArray()) {
				JsonObject employeeObj = employeeElem.getAsJsonObject();
				JsonObject response = (employee.updateEmployee(employeeObj.get("user_id").getAsString(), employeeObj.get("username").getAsString(), employeeObj.get("password").getAsString(), employeeObj.get("first_name").getAsString(), employeeObj.get("last_name").getAsString(), employeeObj.get("gender").getAsString(), employeeObj.get("primary_email").getAsString(), employeeObj.get("primary_phone").getAsString(), employeeObj.get("gb_employee_id").getAsString(), employeeObj.get("department").getAsString(), employeeObj.get("date_hired").getAsString()));
				
				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFULL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", updateCount + " Employees were updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + updateCount +" Employees were Updated. Updating failed for "+ (elemCount-updateCount) + " Employees.");
			}
			
		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}
	
	
	@DELETE
	@Path("/employees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteEmployee(String employeeJSON)
	{
		JsonObject result = null;

		try {

			JsonObject employeeJSON_parsed = new JsonParser().parse(employeeJSON).getAsJsonObject();

			if(!employeeJSON_parsed.has("employees")) {
				return (employee.deleteEmployee(employeeJSON_parsed.get("user_id").getAsString())).toString();
			} else if (!employeeJSON_parsed.get("employees").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = employeeJSON_parsed.get("employees").getAsJsonArray().size();
			
			for (JsonElement employeeElem : employeeJSON_parsed.get("employees").getAsJsonArray()) {
				JsonObject employeeObj = employeeElem.getAsJsonObject();
				JsonObject response = (employee.deleteEmployee(employeeObj.get("user_id").getAsString()));
				
				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFULL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", deleteCount + " Employees were deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + deleteCount +" Employees were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Employees.");
			}
			
		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	//Consumer End-points
	@GET
	@Path("/consumers")
	@Produces(MediaType.APPLICATION_JSON)
	public String readConsumers() {
		return consumer.readConsumers().toString();
	}

	@POST
	@Path("/consumers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertConsumer(String consumerJSON)
	{
		JsonObject result = null;

		try {

			JsonObject consumerJSON_parsed = new JsonParser().parse(consumerJSON).getAsJsonObject();

			//check if multiple inserts
			if(!consumerJSON_parsed.has("consumers")) {
				return (consumer.insertConsumer(consumerJSON_parsed.get("username").getAsString(), consumerJSON_parsed.get("password").getAsString(), consumerJSON_parsed.get("role_id").getAsString(), consumerJSON_parsed.get("first_name").getAsString(), consumerJSON_parsed.get("last_name").getAsString(), consumerJSON_parsed.get("gender").getAsString(), consumerJSON_parsed.get("primary_email").getAsString(), consumerJSON_parsed.get("primary_phone").getAsString())).toString();
			} else if (!consumerJSON_parsed.get("consumers").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = consumerJSON_parsed.get("consumers").getAsJsonArray().size();

			for (JsonElement consumerElem : consumerJSON_parsed.get("consumers").getAsJsonArray()) {
				JsonObject consumerObj = consumerElem.getAsJsonObject();
				JsonObject response = (consumer.insertConsumer(consumerObj.get("username").getAsString(), consumerObj.get("password").getAsString(), consumerObj.get("role_id").getAsString(), consumerObj.get("first_name").getAsString(), consumerObj.get("last_name").getAsString(), consumerObj.get("gender").getAsString(), consumerObj.get("primary_email").getAsString(), consumerObj.get("primary_phone").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFULL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", insertCount + " Consumers were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Consumers were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Consumers.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}
	
	
	@PUT
	@Path("/consumers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateConsumer(String consumerJSON)
	{
		JsonObject result = null;

		try {

			JsonObject consumerJSON_parsed = new JsonParser().parse(consumerJSON).getAsJsonObject();

			//check if multiple inserts
			if(!consumerJSON_parsed.has("consumers")) {
				return (consumer.updateConsumer(consumerJSON_parsed.get("user_id").getAsString(), consumerJSON_parsed.get("username").getAsString(), consumerJSON_parsed.get("password").getAsString(), consumerJSON_parsed.get("first_name").getAsString(), consumerJSON_parsed.get("last_name").getAsString(), consumerJSON_parsed.get("gender").getAsString(), consumerJSON_parsed.get("primary_email").getAsString(), consumerJSON_parsed.get("primary_phone").getAsString())).toString();
			} else if (!consumerJSON_parsed.get("consumers").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = consumerJSON_parsed.get("consumers").getAsJsonArray().size();
			
			for (JsonElement consumerElem : consumerJSON_parsed.get("consumers").getAsJsonArray()) {
				JsonObject consumerObj = consumerElem.getAsJsonObject();
				JsonObject response = (consumer.updateConsumer(consumerObj.get("user_id").getAsString(), consumerObj.get("username").getAsString(), consumerObj.get("password").getAsString(), consumerObj.get("first_name").getAsString(), consumerObj.get("last_name").getAsString(), consumerObj.get("gender").getAsString(), consumerObj.get("primary_email").getAsString(), consumerObj.get("primary_phone").getAsString()));
				
				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFULL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", updateCount + " Consumers were updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + updateCount +" Consumers were Updated. Updating failed for "+ (elemCount-updateCount) + " Consumers.");
			}
			
		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}
	
	
	@DELETE
	@Path("/consumers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteConsumer(String consumerJSON)
	{
		JsonObject result = null;

		try {

			JsonObject consumerJSON_parsed = new JsonParser().parse(consumerJSON).getAsJsonObject();

			//check if multiple inserts
			if(!consumerJSON_parsed.has("consumers")) {
				return (consumer.deleteConsumer(consumerJSON_parsed.get("user_id").getAsString())).toString();
			} else if (!consumerJSON_parsed.get("consumers").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = consumerJSON_parsed.get("consumers").getAsJsonArray().size();
			
			for (JsonElement consumerElem : consumerJSON_parsed.get("consumers").getAsJsonArray()) {
				JsonObject employeeObj = consumerElem.getAsJsonObject();
				JsonObject response = (consumer.deleteConsumer(employeeObj.get("user_id").getAsString()));
				
				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFULL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", deleteCount + " Consumers were deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + deleteCount +" Consumers were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Consumers.");
			}
			
		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	/*
	//Testing Inter-service communications with Payment Service
	@GET
	@Path("/Intercomms/")
	@Produces(MediaType.TEXT_PLAIN)
	public String intercomms()
	{
		Client c = Client.create();
		WebResource resource = c.resource("http://127.0.0.1:8080/PaymentService/PaymentService/Test/");
		String output = resource.get(String.class);
		return "Response of Payment Server: " + output;
	}*/
}

