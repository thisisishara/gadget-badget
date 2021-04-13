package com.gadgetbadget.user;	

import javax.ws.rs.PathParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.gadgetbadget.user.model.Consumer;
import com.gadgetbadget.user.model.Employee;
import com.gadgetbadget.user.model.Funder;
import com.gadgetbadget.user.model.PaymentMethod;
import com.gadgetbadget.user.model.Researcher;
import com.gadgetbadget.user.model.User;
import com.gadgetbadget.user.util.DBOpStatus;
import com.gadgetbadget.user.util.JsonResponseBuilder;
import com.gadgetbadget.user.util.UserType;
import com.gadgetbadget.user.util.ValidationHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/users")
public class UserResource {	
	User user = new User();
	Employee employee = new Employee();
	Researcher researcher = new Researcher();
	Funder funder = new Funder();
	Consumer consumer = new Consumer();
	PaymentMethod paymentMethod = new PaymentMethod();

	ResponseBuilder builder = null;

	//List of End-points for UserTypes
	//Employee End-points
	@GET
	@Path("/employees")
	@Produces(MediaType.APPLICATION_JSON)
	public String readEmployees(@Context SecurityContext securityContext) {
//		//Allow only UserType ADMIN
//		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
//			return new JsonResponseBuilder().getJsonResponse(DBOpStatus.ERROR.toString(), "You are not Authorized to Perform this action.").toString();
//		}
		return employee.readEmployees().toString();
	}

	@POST
	@Path("/employees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertEmployee(String employeeJSON, @Context SecurityContext securityContext)
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

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			//Limit multiple inserts only for ADMINs
			if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
				return new JsonResponseBuilder().getJsonResponse(Response.Status.UNAUTHORIZED.toString().toUpperCase(), "You are not Authorized to Perform this action.").toString();
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
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

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
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

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
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
	public String insertConsumer(String consumerJSON, @Context SecurityContext securityContext)
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

			//Limit multiple inserts only for ADMINs
			if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
				return new JsonResponseBuilder().getJsonResponse(Response.Status.UNAUTHORIZED.toString().toUpperCase(), "You are not Authorized to Perform this action.").toString();
			}

			int insertCount = 0;
			int elemCount = consumerJSON_parsed.get("consumers").getAsJsonArray().size();

			for (JsonElement consumerElem : consumerJSON_parsed.get("consumers").getAsJsonArray()) {
				JsonObject consumerObj = consumerElem.getAsJsonObject();
				JsonObject response = (consumer.insertConsumer(consumerObj.get("username").getAsString(), consumerObj.get("password").getAsString(), consumerObj.get("role_id").getAsString(), consumerObj.get("first_name").getAsString(), consumerObj.get("last_name").getAsString(), consumerObj.get("gender").getAsString(), consumerObj.get("primary_email").getAsString(), consumerObj.get("primary_phone").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
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

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
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
				JsonObject consumerObj = consumerElem.getAsJsonObject();
				JsonObject response = (consumer.deleteConsumer(consumerObj.get("user_id").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
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

	//Funder End-points
	@GET
	@Path("/funders")
	@Produces(MediaType.APPLICATION_JSON)
	public String readFunders() {
		return funder.readFunders().toString();
	}

	@POST
	@Path("/funders")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertFunder(String funderJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		try {

			JsonObject funderJSON_parsed = new JsonParser().parse(funderJSON).getAsJsonObject();

			//check if multiple inserts
			if(!funderJSON_parsed.has("funders")) {
				return (funder.insertFunder(funderJSON_parsed.get("username").getAsString(), funderJSON_parsed.get("password").getAsString(), funderJSON_parsed.get("role_id").getAsString(), funderJSON_parsed.get("first_name").getAsString(), funderJSON_parsed.get("last_name").getAsString(), funderJSON_parsed.get("gender").getAsString(), funderJSON_parsed.get("primary_email").getAsString(), funderJSON_parsed.get("primary_phone").getAsString(), funderJSON_parsed.get("organization").getAsString())).toString();
			} else if (!funderJSON_parsed.get("funders").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			//Limit multiple inserts only for ADMINs
			if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
				return new JsonResponseBuilder().getJsonResponse(Response.Status.UNAUTHORIZED.toString().toUpperCase(), "You are not Authorized to Perform this action.").toString();
			}

			int insertCount = 0;
			int elemCount = funderJSON_parsed.get("funders").getAsJsonArray().size();

			for (JsonElement funderElem : funderJSON_parsed.get("funders").getAsJsonArray()) {
				JsonObject funderObj = funderElem.getAsJsonObject();
				JsonObject response = (funder.insertFunder(funderObj.get("username").getAsString(), funderObj.get("password").getAsString(), funderObj.get("role_id").getAsString(), funderObj.get("first_name").getAsString(), funderObj.get("last_name").getAsString(), funderObj.get("gender").getAsString(), funderObj.get("primary_email").getAsString(), funderObj.get("primary_phone").getAsString(), funderObj.get("organization").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", insertCount + " Funders were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Funders were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Funders.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@PUT
	@Path("/funders")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateFunder(String funderJSON)
	{
		JsonObject result = null;

		try {

			JsonObject funderJSON_parsed = new JsonParser().parse(funderJSON).getAsJsonObject();

			//check if multiple inserts
			if(!funderJSON_parsed.has("funders")) {
				return (funder.updateFunder(funderJSON_parsed.get("user_id").getAsString(), funderJSON_parsed.get("username").getAsString(), funderJSON_parsed.get("password").getAsString(), funderJSON_parsed.get("first_name").getAsString(), funderJSON_parsed.get("last_name").getAsString(), funderJSON_parsed.get("gender").getAsString(), funderJSON_parsed.get("primary_email").getAsString(), funderJSON_parsed.get("primary_phone").getAsString(), funderJSON_parsed.get("organization").getAsString())).toString();
			} else if (!funderJSON_parsed.get("funders").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = funderJSON_parsed.get("funders").getAsJsonArray().size();

			for (JsonElement funderElem : funderJSON_parsed.get("funders").getAsJsonArray()) {
				JsonObject funderObj = funderElem.getAsJsonObject();
				JsonObject response = (funder.updateFunder(funderObj.get("user_id").getAsString(), funderObj.get("username").getAsString(), funderObj.get("password").getAsString(), funderObj.get("first_name").getAsString(), funderObj.get("last_name").getAsString(), funderObj.get("gender").getAsString(), funderObj.get("primary_email").getAsString(), funderObj.get("primary_phone").getAsString(), funderObj.get("organization").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", updateCount + " Funders were updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + updateCount +" Funders were Updated. Updating failed for "+ (elemCount-updateCount) + " Funders.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@DELETE
	@Path("/funders")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFunder(String funderJSON)
	{
		JsonObject result = null;

		try {

			JsonObject funderJSON_parsed = new JsonParser().parse(funderJSON).getAsJsonObject();

			//check if multiple inserts
			if(!funderJSON_parsed.has("funders")) {
				return (funder.deleteFunder(funderJSON_parsed.get("user_id").getAsString())).toString();
			} else if (!funderJSON_parsed.get("funders").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = funderJSON_parsed.get("funders").getAsJsonArray().size();

			for (JsonElement funderElem : funderJSON_parsed.get("funders").getAsJsonArray()) {
				JsonObject funderObj = funderElem.getAsJsonObject();
				JsonObject response = (funder.deleteFunder(funderObj.get("user_id").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", deleteCount + " Funders were deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + deleteCount +" Funders were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Funders.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	//Researcher End-points
	@GET
	@Path("/researchers")
	@Produces(MediaType.APPLICATION_JSON)
	public String readResearchers() {
		return researcher.readResearchers().toString();
	}

	@POST
	@Path("/researchers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertResearcher(String researcherJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		try {

			JsonObject researcherJSON_parsed = new JsonParser().parse(researcherJSON).getAsJsonObject();

			//check if multiple inserts
			if(!researcherJSON_parsed.has("researchers")) {
				return (researcher.insertResearcher(researcherJSON_parsed.get("username").getAsString(), researcherJSON_parsed.get("password").getAsString(), researcherJSON_parsed.get("role_id").getAsString(), researcherJSON_parsed.get("first_name").getAsString(), researcherJSON_parsed.get("last_name").getAsString(), researcherJSON_parsed.get("gender").getAsString(), researcherJSON_parsed.get("primary_email").getAsString(), researcherJSON_parsed.get("primary_phone").getAsString(), researcherJSON_parsed.get("institution").getAsString(), researcherJSON_parsed.get("field_of_study").getAsString(),Integer.parseInt(researcherJSON_parsed.get("years_of_exp").getAsString()))).toString();
			} else if (!researcherJSON_parsed.get("researchers").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			//Limit multiple inserts only for ADMINs
			if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
				return new JsonResponseBuilder().getJsonResponse(Response.Status.UNAUTHORIZED.toString().toUpperCase(), "You are not Authorized to Perform this action.").toString();
			}
			
			int insertCount = 0;
			int elemCount = researcherJSON_parsed.get("researchers").getAsJsonArray().size();

			for (JsonElement researcherElem : researcherJSON_parsed.get("researchers").getAsJsonArray()) {
				JsonObject researcherObj = researcherElem.getAsJsonObject();
				JsonObject response = (researcher.insertResearcher(researcherObj.get("username").getAsString(), researcherObj.get("password").getAsString(), researcherObj.get("role_id").getAsString(), researcherObj.get("first_name").getAsString(), researcherObj.get("last_name").getAsString(), researcherObj.get("gender").getAsString(), researcherObj.get("primary_email").getAsString(), researcherObj.get("primary_phone").getAsString(), researcherObj.get("institution").getAsString(), researcherObj.get("field_of_study").getAsString(),Integer.parseInt(researcherObj.get("years_of_exp").getAsString())));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", insertCount + " Researchers were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Researchers were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Researchers.");
			}

		} catch (NumberFormatException ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: Invalid input format. " + ex.getMessage());

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@PUT
	@Path("/researchers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateResearcher(String researcherJSON)
	{
		JsonObject result = null;

		try {

			JsonObject researcherJSON_parsed = new JsonParser().parse(researcherJSON).getAsJsonObject();

			//check if multiple inserts
			if(!researcherJSON_parsed.has("researchers")) {
				return (researcher.updateResearcher(researcherJSON_parsed.get("user_id").getAsString(), researcherJSON_parsed.get("username").getAsString(), researcherJSON_parsed.get("password").getAsString(), researcherJSON_parsed.get("first_name").getAsString(), researcherJSON_parsed.get("last_name").getAsString(), researcherJSON_parsed.get("gender").getAsString(), researcherJSON_parsed.get("primary_email").getAsString(), researcherJSON_parsed.get("primary_phone").getAsString(), researcherJSON_parsed.get("institution").getAsString(), researcherJSON_parsed.get("field_of_study").getAsString(), Integer.parseInt(researcherJSON_parsed.get("years_of_exp").getAsString()))).toString();
			} else if (!researcherJSON_parsed.get("researchers").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = researcherJSON_parsed.get("researchers").getAsJsonArray().size();

			for (JsonElement researcherElem : researcherJSON_parsed.get("researchers").getAsJsonArray()) {
				JsonObject researcherObj = researcherElem.getAsJsonObject();
				JsonObject response = (researcher.updateResearcher(researcherObj.get("user_id").getAsString(), researcherObj.get("username").getAsString(), researcherObj.get("password").getAsString(), researcherObj.get("first_name").getAsString(), researcherObj.get("last_name").getAsString(), researcherObj.get("gender").getAsString(), researcherObj.get("primary_email").getAsString(), researcherObj.get("primary_phone").getAsString(), researcherObj.get("institution").getAsString(), researcherObj.get("field_of_study").getAsString(),Integer.parseInt(researcherObj.get("years_of_exp").getAsString())));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", updateCount + " Researchers were updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + updateCount +" Researchers were Updated. Updating failed for "+ (elemCount-updateCount) + " Researchers.");
			}

		} catch (NumberFormatException ex) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: Invalid input format. " + ex.getMessage());

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@DELETE
	@Path("/researchers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteResearcher(String researcherJSON)
	{
		JsonObject result = null;

		try {

			JsonObject researcherJSON_parsed = new JsonParser().parse(researcherJSON).getAsJsonObject();

			//check if multiple inserts
			if(!researcherJSON_parsed.has("researchers")) {
				return (researcher.deleteResearcher(researcherJSON_parsed.get("user_id").getAsString())).toString();
			} else if (!researcherJSON_parsed.get("researchers").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = researcherJSON_parsed.get("researchers").getAsJsonArray().size();

			for (JsonElement researcherElem : researcherJSON_parsed.get("researchers").getAsJsonArray()) {
				JsonObject researcherObj = researcherElem.getAsJsonObject();
				JsonObject response = (researcher.deleteResearcher(researcherObj.get("user_id").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", deleteCount + " Researchers were deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + deleteCount +" Researchers were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Researchers.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	//List of End-points for Payment Method
	//Consumer-payment method End-points
	@GET
	@Path("/consumers/{consumer_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String readConPayMethods(@PathParam("consumer_id") String consumer_id) {
		return paymentMethod.readPaymentMethods(consumer_id, UserType.CNSMR).toString();
	}


	@GET
	@Path("/consumers/{consumer_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String readConPayMethod(@PathParam("consumer_id") String consumer_id, @QueryParam("limited") boolean limited, String paymentMethodJSON) {
		JsonObject result = null;

		//check query parameter
		if(!limited) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE", "Invalid Request detected. Reading all Payment Method(s) of " + consumer_id + " aborted.");
			return result.toString();
		}

		//verify user_type
		if(!new ValidationHandler().validateUserType(consumer_id, UserType.CNSMR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.readSpecificPaymentMethod(consumer_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int readCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();
			JsonArray resultArray = new JsonArray();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.readSpecificPaymentMethod(consumer_id, paymentMethodObj.get("creditcard_no").getAsString()));

				if (!response.has("MESSAGE")) {
					readCount++;
					resultArray.add(response);
				}
			}

			result = new JsonObject();
			result.add("payment-methods", resultArray);
			if(readCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", readCount + " Payment Methods were retrieved successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + readCount +" Payment Methods were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Payment Methods.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@POST
	@Path("/consumers/{consumer_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertConPayMethod(@PathParam("consumer_id") String consumer_id, String paymentMethodJSON)
	{
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(consumer_id, UserType.CNSMR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.insertPaymentMethod(consumer_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.insertPaymentMethod(consumer_id, paymentMethodObj.get("creditcard_type").getAsString(), paymentMethodObj.get("creditcard_no").getAsString(), paymentMethodObj.get("creditcard_security_no").getAsString(), paymentMethodObj.get("exp_date").getAsString(), paymentMethodObj.get("billing_address").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", insertCount + " Payment Methods were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Payment Methods were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Payment Methods.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@PUT
	@Path("/consumers/{consumer_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateConPayMethod(@PathParam("consumer_id") String consumer_id, String paymentMethodJSON)
	{
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(consumer_id, UserType.CNSMR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.updatePaymentMethod(consumer_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("new_creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.updatePaymentMethod(consumer_id, paymentMethodObj.get("creditcard_type").getAsString(), paymentMethodObj.get("new_creditcard_no").getAsString(), paymentMethodObj.get("creditcard_no").getAsString(), paymentMethodObj.get("creditcard_security_no").getAsString(), paymentMethodObj.get("exp_date").getAsString(), paymentMethodObj.get("billing_address").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", updateCount + " Payment Methods were updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + updateCount +" Payment Methods were Updated. Updating failed for "+ (elemCount-updateCount) + " Consumers.");
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage());
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}

	@DELETE
	@Path("/consumers/{consumer_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteConPayMethod(@PathParam("consumer_id") String consumer_id, String paymentMethodJSON)
	{
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(consumer_id, UserType.CNSMR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.deletePaymentMethod(consumer_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.deletePaymentMethod(consumer_id, paymentMethodObj.get("creditcard_no").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
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

	@DELETE
	@Path("/consumers/{consumer_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteConPayMethods(@PathParam("consumer_id") String consumer_id, @QueryParam("all") boolean isAllowed)
	{
		JsonObject result = null;

		if(!isAllowed) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE", "Invalid Request detected. Deleting all Payment Methods of " + consumer_id + " aborted.");
			return result.toString();
		}

		try {
			result = (paymentMethod.deletePaymentMethods(consumer_id, UserType.CNSMR));
		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}
		return result.toString();
	}

	//Funder-payment method End-points
	@GET
	@Path("/funders/{funder_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String readFunPayMethods(@PathParam("funder_id") String funder_id) {
		return paymentMethod.readPaymentMethods(funder_id, UserType.FUNDR).toString();
	}


	@GET
	@Path("/funders/{funder_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String readFunPayMethod(@PathParam("funder_id") String funder_id, @QueryParam("limited") boolean limited, String paymentMethodJSON) {
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(funder_id, UserType.FUNDR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		if(!limited) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE", "Invalid Request detected. Reading all Payment Method(s) of " + funder_id + " aborted.");
			return result.toString();
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.readSpecificPaymentMethod(funder_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int readCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();
			JsonArray resultArray = new JsonArray();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.readSpecificPaymentMethod(funder_id, paymentMethodObj.get("creditcard_no").getAsString()));

				if (!response.has("MESSAGE")) {
					readCount++;
					resultArray.add(response);
				}
			}

			result = new JsonObject();
			result.add("payment-methods", resultArray);
			if(readCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", readCount + " Payment Methods of " + funder_id + " were retrieved successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + readCount +" Payment Methods were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Payment Methods of " + funder_id + ".");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@POST
	@Path("/funders/{funder_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertFunPayMethod(@PathParam("funder_id") String funder_id, String paymentMethodJSON)
	{
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(funder_id, UserType.FUNDR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.insertPaymentMethod(funder_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.insertPaymentMethod(funder_id, paymentMethodObj.get("creditcard_type").getAsString(), paymentMethodObj.get("creditcard_no").getAsString(), paymentMethodObj.get("creditcard_security_no").getAsString(), paymentMethodObj.get("exp_date").getAsString(), paymentMethodObj.get("billing_address").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", insertCount + " Payment Methods of "+ funder_id +" were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Payment Methods were Inserted. Inserting failed for "+ (elemCount-insertCount) + " given Payment Methods of " + funder_id + ".");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@PUT
	@Path("/funders/{funder_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateFunPayMethod(@PathParam("funder_id") String funder_id, String paymentMethodJSON)
	{
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(funder_id, UserType.FUNDR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.updatePaymentMethod(funder_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("new_creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.updatePaymentMethod(funder_id, paymentMethodObj.get("creditcard_type").getAsString(), paymentMethodObj.get("new_creditcard_no").getAsString(), paymentMethodObj.get("creditcard_no").getAsString(), paymentMethodObj.get("creditcard_security_no").getAsString(), paymentMethodObj.get("exp_date").getAsString(), paymentMethodObj.get("billing_address").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", updateCount + " Payment Methods of " + funder_id + " were updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + updateCount +" Payment Methods were Updated. Updating failed for "+ (elemCount-updateCount) + " given Payment Methods of " + funder_id + ".");
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage());
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}

	@DELETE
	@Path("/funders/{funder_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFunPayMethod(@PathParam("funder_id") String funder_id, String paymentMethodJSON)
	{
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(funder_id, UserType.FUNDR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.deletePaymentMethod(funder_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.deletePaymentMethod(funder_id, paymentMethodObj.get("creditcard_no").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", deleteCount + " Payment Methods of "+ funder_id+ " were deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + deleteCount +" Payment Methods were deleted. Deleting failed for "+ (elemCount-deleteCount) + " given Payment Methods of "+ funder_id + ".");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}

	@DELETE
	@Path("/funders/{funder_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFunPayMethods(@PathParam("funder_id") String funder_id, @QueryParam("all") boolean isAllowed)
	{
		JsonObject result = null;

		if(!isAllowed) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE", "Invalid Request detected. Deleting all Payment Methods of " + funder_id + " aborted.");
			return result.toString();
		}

		try {
			result = (paymentMethod.deletePaymentMethods(funder_id, UserType.FUNDR));
		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}
		return result.toString();
	}

	//Researcher-payment method End-points
	@GET
	@Path("/researchers/{researcher_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String readResPayMethods(@PathParam("researcher_id") String researcher_id) {
		return paymentMethod.readPaymentMethods(researcher_id, UserType.RSCHR).toString();
	}


	@GET
	@Path("/researchers/{researcher_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String readResPayMethod(@PathParam("researcher_id") String researcher_id, @QueryParam("limited") boolean limited, String paymentMethodJSON) {
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		if(!limited) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE", "Invalid Request detected. Reading all Payment Method(s) of " + researcher_id + " aborted.");
			return result.toString();
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.readSpecificPaymentMethod(researcher_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int readCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();
			JsonArray resultArray = new JsonArray();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.readSpecificPaymentMethod(researcher_id, paymentMethodObj.get("creditcard_no").getAsString()));

				if (!response.has("MESSAGE")) {
					readCount++;
					resultArray.add(response);
				}
			}

			result = new JsonObject();
			result.add("payment-methods", resultArray);
			if(readCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", readCount + " Payment Methods of " + researcher_id + " were retrieved successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + readCount +" Payment Methods were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Payment Methods of " + researcher_id + ".");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@POST
	@Path("/researchers/{researcher_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertResPayMethod(@PathParam("researcher_id") String researcher_id, String paymentMethodJSON)
	{
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.insertPaymentMethod(researcher_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.insertPaymentMethod(researcher_id, paymentMethodObj.get("creditcard_type").getAsString(), paymentMethodObj.get("creditcard_no").getAsString(), paymentMethodObj.get("creditcard_security_no").getAsString(), paymentMethodObj.get("exp_date").getAsString(), paymentMethodObj.get("billing_address").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", insertCount + " Payment Methods of "+ researcher_id +" were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Payment Methods were Inserted. Inserting failed for "+ (elemCount-insertCount) + " given Payment Methods of " + researcher_id + ".");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@PUT
	@Path("/researchers/{researcher_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateResPayMethod(@PathParam("researcher_id") String researcher_id, String paymentMethodJSON)
	{
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.updatePaymentMethod(researcher_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("new_creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.updatePaymentMethod(researcher_id, paymentMethodObj.get("creditcard_type").getAsString(), paymentMethodObj.get("new_creditcard_no").getAsString(), paymentMethodObj.get("creditcard_no").getAsString(), paymentMethodObj.get("creditcard_security_no").getAsString(), paymentMethodObj.get("exp_date").getAsString(), paymentMethodObj.get("billing_address").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", updateCount + " Payment Methods of " + researcher_id + " were updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + updateCount +" Payment Methods were Updated. Updating failed for "+ (elemCount-updateCount) + " given Payment Methods of " + researcher_id + ".");
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage());
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}

	@DELETE
	@Path("/researchers/{researcher_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteResPayMethod(@PathParam("researcher_id") String researcher_id, String paymentMethodJSON)
	{
		JsonObject result = null;

		//verify user_type
		if(!new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR)) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE","Invalid User ID Format.");
			return result.toString(); 
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			//check if multiple inserts
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.deletePaymentMethod(researcher_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray().size();

			for (JsonElement jsonElem : paymentMethodJSON_parsed.get("payment_methods").getAsJsonArray()) {
				JsonObject paymentMethodObj = jsonElem.getAsJsonObject();
				JsonObject response = (paymentMethod.deletePaymentMethod(researcher_id, paymentMethodObj.get("creditcard_no").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", deleteCount + " Payment Methods of "+ researcher_id+ " were deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + deleteCount +" Payment Methods were deleted. Deleting failed for "+ (elemCount-deleteCount) + " given Payment Methods of "+ researcher_id + ".");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}

	@DELETE
	@Path("/researchers/{researcher_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteResPayMethods(@PathParam("researcher_id") String researcher_id, @QueryParam("all") boolean isAllowed)
	{
		JsonObject result = null;

		if(!isAllowed) {
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.ERROR.toString());
			result.addProperty("MESSAGE", "Invalid Request detected. Deleting all Payment Methods of " + researcher_id + " aborted.");
			return result.toString();
		}

		try {
			result = (paymentMethod.deletePaymentMethods(researcher_id, UserType.RSCHR));
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
		JsonObject output = resource.get(JsonObject.class);
		return "Response of Payment Server: " + output;
	}
	 */
}

