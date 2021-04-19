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
import javax.ws.rs.WebApplicationException;
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
import com.gadgetbadget.user.util.ServiceType;
import com.gadgetbadget.user.util.UserType;
import com.gadgetbadget.user.util.ValidationHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This class represents all user resources with with base resource "users"
 * Authorized users may vary depending on the sub resources and the authorized 
 * users will be properly inspected at each end-point before granting access to 
 * perform the intended task of the end-points. SecurityContext is used to 
 * implement role based authorization.
 * 
 * @author Ishara_Dissanayake
 */
@Path("/users")
public class UserResource {	
	User user = new User();
	Employee employee = new Employee();
	Researcher researcher = new Researcher();
	Funder funder = new Funder();
	Consumer consumer = new Consumer();
	PaymentMethod paymentMethod = new PaymentMethod();

	ResponseBuilder builder = null;

	private static final String SUPER_ADMIN = "AD21000001";

	/**
	 * GET method of the "/users" resource retrieves a list of currently available users
	 * from the local database of the user service. It is only allowed to be done for ADMINs.
	 * 
	 * @param securityContext	contains authenticated user's critical information
	 * @param isStats			ADMINs can use this query parameter and set it to true to get a summarized statistics
	 * 							of each user from other services. if this is either set to false or not set at all, the
	 * 							regular set of users is returned.
	 * @return					
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String getUsers(@Context SecurityContext securityContext, @QueryParam("stats") boolean isStats) {

		//Authorize only ADMINs
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			//return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			String response = "You are not Authorized to access this End-point.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}

		return user.getUsers(isStats).toString();
	}

	//Change Account State [Activate/Deactivate]
	@PUT
	@Path("/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String changeUserAccountState(@Context SecurityContext securityContext, @PathParam("user_id") String uri_user_id, @QueryParam("deactivate") boolean isDeactivated)
	{
		try {
			// Authorize only ADMINs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				//return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
				String response = "You are not Authorized to access this End-point.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}
			
			// Prohibit deactivating the SUPER ADMIN
			if(uri_user_id.equals(SUPER_ADMIN)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("Altering state of the SUPER_ADMIN Account is NOT Allowed!.").toString();
			}

			return user.changeUserAccountState(uri_user_id, isDeactivated? "Yes":"No").toString();

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	//Change Password
	@PUT
	@Path("/{user_id}/password")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String changePassword(String passwordsJSON, @Context SecurityContext securityContext, @PathParam("user_id") String uri_user_id )
	{
		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Verify JSON Object's Validity
			JsonObject passwordJSON_parsed = new JsonParser().parse(passwordsJSON).getAsJsonObject();

			if (! (passwordJSON_parsed.has("new_password") && passwordJSON_parsed.has("old_password"))) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Prohibit NON ADMIN users from altering other user accounts
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				if (! uri_user_id.equals(current_user_id)){
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to change passwords of other users.").toString();
				}
				return user.changePassword(uri_user_id,  passwordJSON_parsed.get("old_password").getAsString(), passwordJSON_parsed.get("new_password").getAsString()).toString();
			}

			// Allow ADMINs to alter any user account's password
			return user.changePassword(uri_user_id, passwordJSON_parsed.get("old_password").getAsString(), passwordJSON_parsed.get("new_password").getAsString()).toString();

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	//Generic User Account Delete
	@DELETE
	@Path("/{user_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteUser(@PathParam("user_id") String uri_user_id, @Context SecurityContext securityContext)
	{
		try {
			// Authorize only ADMINs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				//return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
				String response = "You are not Authorized to access this End-point.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}
			
			// Prohibit deactivating the SUPER ADMIN
			if(uri_user_id.equals(SUPER_ADMIN)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("Deleting the SUPER_ADMIN Account is NOT Allowed!.").toString();
			}
			
			if(!uri_user_id.contains(",")) {
				return user.deleteUser(uri_user_id).toString();
			}

			String[] ids = uri_user_id.split(",");

			int deleteCount = 0;
			int elemCount = ids.length;

			for (String id : ids) {
				JsonObject response = user.deleteUser(id);

				if (!response.has("MESSAGE")) {
					deleteCount++;
				}
			}

			if(deleteCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(deleteCount + " Users were deleted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + deleteCount +" Users were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Users.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex).toString();
		}
	}

	//List of End-points for Specific UserTypes
	//Employee End-points
	@GET
	@Path("/employees")
	@Produces(MediaType.APPLICATION_JSON)
	public String readEmployees(@Context SecurityContext securityContext) {
		// Authorize only ADMINs
		if(!(securityContext.isUserInRole(UserType.ADMIN.toString()))) {
			//return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			String response = "You are not Authorized to access this End-point.";
			builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
			throw new WebApplicationException(builder.build());
		}
		return employee.readEmployees().toString();
	}

	@GET
	@Path("/employees/{employee_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readEmployees(@Context SecurityContext securityContext, @PathParam("employee_id") String uri_employee_id) {
		// Authorize only ADMINs, FNMGRs, and EMPLYs
		if(!(securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FNMGR.toString()) || securityContext.isUserInRole(UserType.EMPLY.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if its a Single ID or Multiple IDs
		if(!uri_employee_id.contains(",")) {
			// Allow retrieving only if the IDs are matched for NON ADMINs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				if (! uri_employee_id.equals(current_user_id)){
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to retrieve details of other Employees.").toString();
				}
			}

			return employee.readEmployeeById(uri_employee_id).toString();
		}

		// Allow only ADMINs to retrieve multiple employees at a time
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
			return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to retrieve multiple Employees!").toString();
		}

		String[] ids = uri_employee_id.split(",");

		int readCount = 0;
		int elemCount = ids.length;
		JsonArray resultArray = new JsonArray();

		for (String id : ids) {
			JsonObject response = employee.readEmployeeById(id);

			if (!response.has("MESSAGE")) {
				readCount++;
				resultArray.add(response);
			}
		}

		if(readCount == elemCount) {
			return new JsonResponseBuilder().getJsonArrayResponse("employees", resultArray, DBOpStatus.SUCCESSFUL, readCount + " Employees were retrieved successfully.").toString();
		} else {
			return new JsonResponseBuilder().getJsonArrayResponse("employees", resultArray, DBOpStatus.UNSUCCESSFUL, "Only " + readCount +" Employees were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Employees.").toString();
		}
	}

	@POST
	@Path("/employees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertEmployee(String employeeJSON, @Context SecurityContext securityContext)
	{
		try {
			// Verify JSON Object's Validity
			JsonObject employeeJSON_parsed = new JsonParser().parse(employeeJSON).getAsJsonObject();

			if(!employeeJSON_parsed.has("employees")) {
				return (employee.insertEmployee(employeeJSON_parsed.get("username").getAsString(), employeeJSON_parsed.get("password").getAsString(), employeeJSON_parsed.get("role_id").getAsString(), employeeJSON_parsed.get("first_name").getAsString(), employeeJSON_parsed.get("last_name").getAsString(), employeeJSON_parsed.get("gender").getAsString(), employeeJSON_parsed.get("primary_email").getAsString(), employeeJSON_parsed.get("primary_phone").getAsString(), employeeJSON_parsed.get("gb_employee_id").getAsString(), employeeJSON_parsed.get("department").getAsString(), employeeJSON_parsed.get("date_hired").getAsString())).toString();
			} else if (!employeeJSON_parsed.get("employees").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to add multiple employees at a time
			// by throwing WebApplication Exceptions for Unauthorized users
			if(securityContext.getUserPrincipal() == null) {
				String response = "You are not Authorized to access this End-point.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}
			
			if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
				//return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Insert Multiple Employees").toString();
				String response = "You are not Authorized to perform multiple insertions at once.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
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

			if(insertCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(insertCount + " Employees were inserted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + insertCount +" Employees were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Employees.").toString();
			}

		} catch (WebApplicationException wae) {
			builder = Response.status(wae.getResponse().getStatus()).entity(wae.getResponse().getEntity());
			throw new WebApplicationException(builder.build());
			
		}	catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@PUT
	@Path("/employees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateEmployee(String employeeJSON, @Context SecurityContext securityContext)
	{
		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & EMPLYs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.EMPLY.toString())  || securityContext.isUserInRole(UserType.FNMGR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			JsonObject employeeJSON_parsed = new JsonParser().parse(employeeJSON).getAsJsonObject();

			// Prohibit NON ADMIN EMPLY users from altering other user accounts
			if(!employeeJSON_parsed.has("employees")) {
				if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
					if (! employeeJSON_parsed.get("user_id").getAsString().equals(current_user_id)){
						return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to update details of other Employees.").toString();
					}
				}
				return (employee.updateEmployee(employeeJSON_parsed.get("user_id").getAsString(), employeeJSON_parsed.get("username").getAsString(), employeeJSON_parsed.get("password").getAsString(), employeeJSON_parsed.get("first_name").getAsString(), employeeJSON_parsed.get("last_name").getAsString(), employeeJSON_parsed.get("gender").getAsString(), employeeJSON_parsed.get("primary_email").getAsString(), employeeJSON_parsed.get("primary_phone").getAsString(), employeeJSON_parsed.get("gb_employee_id").getAsString(), employeeJSON_parsed.get("department").getAsString(), employeeJSON_parsed.get("date_hired").getAsString())).toString();
			} else if (!employeeJSON_parsed.get("employees").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to alter multiple user accounts at a time
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Update multiple Employees!").toString();
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

			if(updateCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(updateCount + " Employees were updated successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + updateCount +" Employees were Updated. Updating failed for "+ (elemCount-updateCount) + " Employees.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@DELETE
	@Path("/employees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteEmployee(String employeeJSON, @Context SecurityContext securityContext)
	{
		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			/// Authorize only ADMINs & EMPLYs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.EMPLY.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			JsonObject employeeJSON_parsed = new JsonParser().parse(employeeJSON).getAsJsonObject();

			// Prohibit NON ADMIN EMPLY users from deleting other user accounts
			if(!employeeJSON_parsed.has("employees")) {
				if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
					if (! employeeJSON_parsed.get("user_id").getAsString().equals(current_user_id)){
						return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to delete other Employees.").toString();
					}
				}				
				return (employee.deleteEmployee(employeeJSON_parsed.get("user_id").getAsString())).toString();
			} else if (!employeeJSON_parsed.get("employees").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to delete multiple user accounts at a time
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Delete multiple Employees!").toString();
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

			if(deleteCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(deleteCount + " Employees were deleted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + deleteCount +" Employees were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Employees.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	//Consumer End-points
	@GET
	@Path("/consumers")
	@Produces(MediaType.APPLICATION_JSON)
	public String readConsumers(@Context SecurityContext securityContext) {
		// Authorize only ADMINs
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}
		return consumer.readConsumers().toString();
	}

	@GET
	@Path("/consumers/{consumer_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readConsumers(@Context SecurityContext securityContext, @PathParam("consumer_id") String uri_consumer_id) {
		// Authorize only ADMINs, CNSMRs
		if(!(securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.CNSMR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if its a Single ID or Multiple IDs
		if(!uri_consumer_id.contains(",")) {
			// Allow retrieving only if the IDs are matched for NON ADMINs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				if (! uri_consumer_id.equals(current_user_id)){
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to retrieve details of other Consumers.").toString();
				}
			}

			return consumer.readConsumerById(uri_consumer_id).toString();
		}

		// Allow only ADMINs to retrieve multiple consumers at a time
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
			return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to retrieve multiple Consumers!").toString();
		}

		String[] ids = uri_consumer_id.split(",");

		int readCount = 0;
		int elemCount = ids.length;
		JsonArray resultArray = new JsonArray();

		for (String id : ids) {
			JsonObject response = consumer.readConsumerById(id);

			if (!response.has("MESSAGE")) {
				readCount++;
				resultArray.add(response);
			}
		}

		if(readCount == elemCount) {
			return new JsonResponseBuilder().getJsonArrayResponse("consumers", resultArray, DBOpStatus.SUCCESSFUL, readCount + " Consumers were retrieved successfully.").toString();
		} else {
			return new JsonResponseBuilder().getJsonArrayResponse("consumers", resultArray, DBOpStatus.UNSUCCESSFUL, "Only " + readCount +" Consumers were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Consumers.").toString();
		}
	}

	@POST
	@Path("/consumers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertConsumer(String consumerJSON, @Context SecurityContext securityContext)
	{
		try {
			// Verify JSON Object's Validity
			JsonObject consumerJSON_parsed = new JsonParser().parse(consumerJSON).getAsJsonObject();

			if(!consumerJSON_parsed.has("consumers")) {
				return (consumer.insertConsumer(consumerJSON_parsed.get("username").getAsString(), consumerJSON_parsed.get("password").getAsString(), UserType.CNSMR.toString(), consumerJSON_parsed.get("first_name").getAsString(), consumerJSON_parsed.get("last_name").getAsString(), consumerJSON_parsed.get("gender").getAsString(), consumerJSON_parsed.get("primary_email").getAsString(), consumerJSON_parsed.get("primary_phone").getAsString())).toString();
			} else if (!consumerJSON_parsed.get("consumers").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to add multiple employees at a time
			// by throwing WebApplication Exceptions for Unauthorized users
			if(securityContext.getUserPrincipal() == null) {
				String response = "You are not Authorized to access this End-point.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}
			
			if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
				//return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Insert Multiple Employees").toString();
				String response = "You are not Authorized to perform multiple insertions at once.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}

			int insertCount = 0;
			int elemCount = consumerJSON_parsed.get("consumers").getAsJsonArray().size();

			for (JsonElement consumerElem : consumerJSON_parsed.get("consumers").getAsJsonArray()) {
				JsonObject consumerObj = consumerElem.getAsJsonObject();
				JsonObject response = (consumer.insertConsumer(consumerObj.get("username").getAsString(), consumerObj.get("password").getAsString(), UserType.CNSMR.toString(), consumerObj.get("first_name").getAsString(), consumerObj.get("last_name").getAsString(), consumerObj.get("gender").getAsString(), consumerObj.get("primary_email").getAsString(), consumerObj.get("primary_phone").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			if(insertCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(insertCount + " Consumers were inserted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + insertCount +" Consumers were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Consumers.").toString();
			}

		} catch (WebApplicationException wae) {
			builder = Response.status(wae.getResponse().getStatus()).entity(wae.getResponse().getEntity());
			throw new WebApplicationException(builder.build());
			
		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}


	@PUT
	@Path("/consumers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateConsumer(String consumerJSON, @Context SecurityContext securityContext)
	{
		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & CNSMRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.CNSMR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			JsonObject consumerJSON_parsed = new JsonParser().parse(consumerJSON).getAsJsonObject();

			// Prohibit NON ADMIN users from altering other user accounts
			if(!consumerJSON_parsed.has("consumers")) {
				if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
					if (! consumerJSON_parsed.get("user_id").getAsString().equals(current_user_id)){
						return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to update details of other Consumers.").toString();
					}
				}
				return (consumer.updateConsumer(consumerJSON_parsed.get("user_id").getAsString(), consumerJSON_parsed.get("username").getAsString(), consumerJSON_parsed.get("password").getAsString(), consumerJSON_parsed.get("first_name").getAsString(), consumerJSON_parsed.get("last_name").getAsString(), consumerJSON_parsed.get("gender").getAsString(), consumerJSON_parsed.get("primary_email").getAsString(), consumerJSON_parsed.get("primary_phone").getAsString())).toString();
			} else if (!consumerJSON_parsed.get("consumers").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to update multiple consumers at a time
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Update multiple Consumers!").toString();
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

			if(updateCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(updateCount + " Consumers were updated successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + updateCount +" Consumers were Updated. Updating failed for "+ (elemCount-updateCount) + " Consumers.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}


	@DELETE
	@Path("/consumers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteConsumer(String consumerJSON, @Context SecurityContext securityContext)
	{
		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & CNSMRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.CNSMR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			JsonObject consumerJSON_parsed = new JsonParser().parse(consumerJSON).getAsJsonObject();

			// Prohibit NON ADMIN users from deleting other user accounts
			if(!consumerJSON_parsed.has("consumers")) {
				if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
					if (! consumerJSON_parsed.get("user_id").getAsString().equals(current_user_id)){
						return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to delete other Consumers.").toString();
					}
				}
				return (consumer.deleteConsumer(consumerJSON_parsed.get("user_id").getAsString())).toString();
			} else if (!consumerJSON_parsed.get("consumers").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to delete multiple consumers at a time
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Delete multiple Consumers!").toString();
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

			if(deleteCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(deleteCount + " Consumers were deleted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + deleteCount +" Consumers were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Consumers.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	//Funder End-points
	@GET
	@Path("/funders")
	@Produces(MediaType.APPLICATION_JSON)
	public String readFunders(@Context SecurityContext securityContext) {
		// Authorize only ADMINs
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}
		return funder.readFunders().toString();
	}

	@GET
	@Path("/funders/{funder_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readFunders(@Context SecurityContext securityContext, @PathParam("funder_id") String uri_funder_id) {
		// Authorize only ADMINs, FUNDRs
		if(!(securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FUNDR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if its a Single ID or Multiple IDs
		if(!uri_funder_id.contains(",")) {
			// Allow retrieving only if the IDs are matched for NON ADMINs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				if (! uri_funder_id.equals(current_user_id)){
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to retrieve details of other Funders.").toString();
				}
			}

			return funder.readFunderById(uri_funder_id).toString();
		}

		// Allow only ADMINs to retrieve multiple funders at a time
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
			return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to retrieve multiple Funders!").toString();
		}

		String[] ids = uri_funder_id.split(",");

		int readCount = 0;
		int elemCount = ids.length;
		JsonArray resultArray = new JsonArray();

		for (String id : ids) {
			JsonObject response = funder.readFunderById(id);

			if (!response.has("MESSAGE")) {
				readCount++;
				resultArray.add(response);
			}
		}

		if(readCount == elemCount) {
			return new JsonResponseBuilder().getJsonArrayResponse("funders", resultArray, DBOpStatus.SUCCESSFUL, readCount + " Funders were retrieved successfully.").toString();
		} else {
			return new JsonResponseBuilder().getJsonArrayResponse("funders", resultArray, DBOpStatus.UNSUCCESSFUL, "Only " + readCount +" Funders were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Funders.").toString();
		}
	}

	@POST
	@Path("/funders")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertFunder(String funderJSON, @Context SecurityContext securityContext)
	{
		try {

			JsonObject funderJSON_parsed = new JsonParser().parse(funderJSON).getAsJsonObject();

			// Verify JSON Object's Validity
			if(!funderJSON_parsed.has("funders")) {
				return (funder.insertFunder(funderJSON_parsed.get("username").getAsString(), funderJSON_parsed.get("password").getAsString(), UserType.FUNDR.toString(), funderJSON_parsed.get("first_name").getAsString(), funderJSON_parsed.get("last_name").getAsString(), funderJSON_parsed.get("gender").getAsString(), funderJSON_parsed.get("primary_email").getAsString(), funderJSON_parsed.get("primary_phone").getAsString(), funderJSON_parsed.get("organization").getAsString())).toString();
			} else if (!funderJSON_parsed.get("funders").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to add multiple employees at a time
			// by throwing WebApplication Exceptions for Unauthorized users
			if(securityContext.getUserPrincipal() == null) {
				String response = "You are not Authorized to access this End-point.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}
			
			if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
				//return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Insert Multiple Employees").toString();
				String response = "You are not Authorized to perform multiple insertions at once.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}

			int insertCount = 0;
			int elemCount = funderJSON_parsed.get("funders").getAsJsonArray().size();

			for (JsonElement funderElem : funderJSON_parsed.get("funders").getAsJsonArray()) {
				JsonObject funderObj = funderElem.getAsJsonObject();
				JsonObject response = (funder.insertFunder(funderObj.get("username").getAsString(), funderObj.get("password").getAsString(), UserType.FUNDR.toString(), funderObj.get("first_name").getAsString(), funderObj.get("last_name").getAsString(), funderObj.get("gender").getAsString(), funderObj.get("primary_email").getAsString(), funderObj.get("primary_phone").getAsString(), funderObj.get("organization").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			if(insertCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(insertCount + " Funders were inserted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + insertCount +" Funders were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Funders.").toString();
			}

		} catch (WebApplicationException wae) {
			builder = Response.status(wae.getResponse().getStatus()).entity(wae.getResponse().getEntity());
			throw new WebApplicationException(builder.build());
			
		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@PUT
	@Path("/funders")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateFunder(String funderJSON, @Context SecurityContext securityContext)
	{
		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & FUNDRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FUNDR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			JsonObject funderJSON_parsed = new JsonParser().parse(funderJSON).getAsJsonObject();

			// Prohibit NON ADMIN users from altering other user accounts
			if(!funderJSON_parsed.has("funders")) {
				if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
					if (! funderJSON_parsed.get("user_id").getAsString().equals(current_user_id)){
						return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to update details of other Funders.").toString();
					}
				}
				return (funder.updateFunder(funderJSON_parsed.get("user_id").getAsString(), funderJSON_parsed.get("username").getAsString(), funderJSON_parsed.get("password").getAsString(), funderJSON_parsed.get("first_name").getAsString(), funderJSON_parsed.get("last_name").getAsString(), funderJSON_parsed.get("gender").getAsString(), funderJSON_parsed.get("primary_email").getAsString(), funderJSON_parsed.get("primary_phone").getAsString(), funderJSON_parsed.get("organization").getAsString())).toString();
			} else if (!funderJSON_parsed.get("funders").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to update funders employees at a time
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Update multiple Funders!").toString();
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

			if(updateCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(updateCount + " Funders were updated successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + updateCount +" Funders were Updated. Updating failed for "+ (elemCount-updateCount) + " Funders.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@DELETE
	@Path("/funders")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFunder(String funderJSON, @Context SecurityContext securityContext)
	{
		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & FUNDRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FUNDR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			JsonObject funderJSON_parsed = new JsonParser().parse(funderJSON).getAsJsonObject();

			// Prohibit NON ADMIN users from deleting other user accounts
			if(!funderJSON_parsed.has("funders")) {
				if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
					if (! funderJSON_parsed.get("user_id").getAsString().equals(current_user_id)){
						return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to delete other Funders.").toString();
					}
				}
				return (funder.deleteFunder(funderJSON_parsed.get("user_id").getAsString())).toString();
			} else if (!funderJSON_parsed.get("funders").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to delete multiple funders at a time
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Delete multiple Funders!").toString();
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

			if(deleteCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(deleteCount + " Funders were deleted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + deleteCount +" Funders were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Funders.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	//Researcher End-points
	@GET
	@Path("/researchers")
	@Produces(MediaType.APPLICATION_JSON)
	public String readResearchers(@Context SecurityContext securityContext) {
		// Authorize only ADMINs
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}
		return researcher.readResearchers().toString();
	}

	@GET
	@Path("/researchers/{researcher_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readResearchers(@Context SecurityContext securityContext, @PathParam("researcher_id") String uri_researcher_id) {
		// Authorize only ADMINs, RSCHRs
		if(!(securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.RSCHR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if its a Single ID or Multiple IDs
		if(!uri_researcher_id.contains(",")) {
			// Allow retrieving only if the IDs are matched for NON ADMINs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				if (! uri_researcher_id.equals(current_user_id)){
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to retrieve details of other Researcher.").toString();
				}
			}

			return researcher.readResearcherById(uri_researcher_id).toString();
		}

		// Allow only ADMINs to retrieve multiple researchers at a time
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
			return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to retrieve multiple Researchers!").toString();
		}

		String[] ids = uri_researcher_id.split(",");

		int readCount = 0;
		int elemCount = ids.length;
		JsonArray resultArray = new JsonArray();

		for (String id : ids) {
			JsonObject response = researcher.readResearcherById(id);

			if (!response.has("MESSAGE")) {
				readCount++;
				resultArray.add(response);
			}
		}

		if(readCount == elemCount) {
			return new JsonResponseBuilder().getJsonArrayResponse("researchers", resultArray, DBOpStatus.SUCCESSFUL, readCount + " Researchers were retrieved successfully.").toString();
		} else {
			return new JsonResponseBuilder().getJsonArrayResponse("researchers", resultArray, DBOpStatus.UNSUCCESSFUL, "Only " + readCount +" Researchers were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Researchers.").toString();
		}
	}

	@POST
	@Path("/researchers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertResearcher(String researcherJSON, @Context SecurityContext securityContext)
	{
		try {

			JsonObject researcherJSON_parsed = new JsonParser().parse(researcherJSON).getAsJsonObject();

			// Verify JSON Object's Validity
			if(!researcherJSON_parsed.has("researchers")) {
				return (researcher.insertResearcher(researcherJSON_parsed.get("username").getAsString(), researcherJSON_parsed.get("password").getAsString(), UserType.RSCHR.toString(), researcherJSON_parsed.get("first_name").getAsString(), researcherJSON_parsed.get("last_name").getAsString(), researcherJSON_parsed.get("gender").getAsString(), researcherJSON_parsed.get("primary_email").getAsString(), researcherJSON_parsed.get("primary_phone").getAsString(), researcherJSON_parsed.get("institution").getAsString(), researcherJSON_parsed.get("field_of_study").getAsString(),Integer.parseInt(researcherJSON_parsed.get("years_of_exp").getAsString()))).toString();
			} else if (!researcherJSON_parsed.get("researchers").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to add multiple employees at a time
			// by throwing WebApplication Exceptions for Unauthorized users
			if(securityContext.getUserPrincipal() == null) {
				String response = "You are not Authorized to access this End-point.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}
			
			if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
				//return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Insert Multiple Employees").toString();
				String response = "You are not Authorized to perform multiple insertions at once.";
				builder = Response.status(Response.Status.UNAUTHORIZED).entity(response);
				throw new WebApplicationException(builder.build());
			}

			int insertCount = 0;
			int elemCount = researcherJSON_parsed.get("researchers").getAsJsonArray().size();

			for (JsonElement researcherElem : researcherJSON_parsed.get("researchers").getAsJsonArray()) {
				JsonObject researcherObj = researcherElem.getAsJsonObject();
				JsonObject response = (researcher.insertResearcher(researcherObj.get("username").getAsString(), researcherObj.get("password").getAsString(), UserType.RSCHR.toString(), researcherObj.get("first_name").getAsString(), researcherObj.get("last_name").getAsString(), researcherObj.get("gender").getAsString(), researcherObj.get("primary_email").getAsString(), researcherObj.get("primary_phone").getAsString(), researcherObj.get("institution").getAsString(), researcherObj.get("field_of_study").getAsString(),Integer.parseInt(researcherObj.get("years_of_exp").getAsString())));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			if(insertCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(insertCount + " Researchers were inserted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + insertCount +" Researchers were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Researchers.").toString();
			}
			
		} catch (WebApplicationException wae) {
			builder = Response.status(wae.getResponse().getStatus()).entity(wae.getResponse().getEntity());
			throw new WebApplicationException(builder.build());
			
		} catch (NumberFormatException ex) {
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}


	@PUT
	@Path("/researchers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateResearcher(String researcherJSON, @Context SecurityContext securityContext)
	{
		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & RSCHRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.RSCHR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			JsonObject researcherJSON_parsed = new JsonParser().parse(researcherJSON).getAsJsonObject();

			// Prohibit NON ADMIN users from altering other user accounts
			if(!researcherJSON_parsed.has("researchers")) {
				if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
					if (! researcherJSON_parsed.get("user_id").getAsString().equals(current_user_id)){
						return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to update details of other Researchers.").toString();
					}
				}
				return (researcher.updateResearcher(researcherJSON_parsed.get("user_id").getAsString(), researcherJSON_parsed.get("username").getAsString(), researcherJSON_parsed.get("password").getAsString(), researcherJSON_parsed.get("first_name").getAsString(), researcherJSON_parsed.get("last_name").getAsString(), researcherJSON_parsed.get("gender").getAsString(), researcherJSON_parsed.get("primary_email").getAsString(), researcherJSON_parsed.get("primary_phone").getAsString(), researcherJSON_parsed.get("institution").getAsString(), researcherJSON_parsed.get("field_of_study").getAsString(), Integer.parseInt(researcherJSON_parsed.get("years_of_exp").getAsString()))).toString();
			} else if (!researcherJSON_parsed.get("researchers").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to update multiple researchers at a time
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Update multiple Researchers!").toString();
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

			if(updateCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(updateCount + " Researchers were updated successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + updateCount +" Researchers were Updated. Updating failed for "+ (elemCount-updateCount) + " Researchers.").toString();
			}

		} catch (NumberFormatException ex) {
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}


	@DELETE
	@Path("/researchers")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteResearcher(String researcherJSON, @Context SecurityContext securityContext)
	{
		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & RSCHRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.RSCHR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			JsonObject researcherJSON_parsed = new JsonParser().parse(researcherJSON).getAsJsonObject();

			// Prohibit NON ADMIN users from deleting other user accounts
			if(!researcherJSON_parsed.has("researchers")) {
				if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
					if (! researcherJSON_parsed.get("user_id").getAsString().equals(current_user_id)){
						return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to delete other Researchers.").toString();
					}
				}
				return (researcher.deleteResearcher(researcherJSON_parsed.get("user_id").getAsString())).toString();
			} else if (!researcherJSON_parsed.get("researchers").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			// Allow only ADMINs to delete multiple researchers at a time
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()))) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are not Allowed to Delete multiple Researchers!").toString();
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

			if(deleteCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(deleteCount + " Researchers were deleted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + deleteCount +" Researchers were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Researchers.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	//List of End-points for Payment Method
	//Consumer-payment method End-points
	@GET
	@Path("/consumers/{consumer_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String readConPayMethods(@PathParam("consumer_id") String consumer_id, @Context SecurityContext securityContext) {
		//verify user_type	
		if(!new ValidationHandler().validateUserType(consumer_id, UserType.CNSMR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString();
		}

		// Authorize only ADMINs & RSCHRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FNMGR.toString()) || securityContext.isUserInRole(UserType.CNSMR.toString()) || securityContext.isUserInRole(ServiceType.PYT.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit CNSMRs from viewing other users' payment details
		if(securityContext.isUserInRole(UserType.CNSMR.toString())) {
			if(! current_user_id.equals(consumer_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to view other users' payment details.").toString();
			}
		}

		return paymentMethod.readPaymentMethods(consumer_id).toString();
	}

	@POST
	@Path("/consumers/{consumer_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertConPayMethod(@PathParam("consumer_id") String consumer_id, @QueryParam("retrieve") boolean isRetrieving, String paymentMethodJSON, @Context SecurityContext securityContext)
	{
		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(consumer_id, UserType.CNSMR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Check if a Insert or a secured Read
		if(!isRetrieving) {
			// Authorize only ADMINs & CNSMRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.CNSMR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Prohibit CNSMRs from inserting other users' payment details
			if(securityContext.isUserInRole(UserType.CNSMR.toString())) {
				if(! current_user_id.equals(consumer_id)) {
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to add other users' payment details.").toString();
				}
			}

			try {

				JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

				// Verify JSON Object's Validity
				if(!paymentMethodJSON_parsed.has("payment_methods")) {
					return (paymentMethod.insertPaymentMethod(consumer_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
				} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
					return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

				if(insertCount == elemCount) {
					return new JsonResponseBuilder().getJsonSuccessResponse(insertCount + " Payment Methods were inserted successfully.").toString();
				} else {
					return new JsonResponseBuilder().getJsonFailedResponse("Only " + insertCount +" Payment Methods were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Payment Methods.").toString();
				}

			} catch (Exception ex){
				System.out.println(ex.getMessage()); // Error Logging
				return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
			}

		} else {
			// If a secured read request;
			// Authorize only ADMINs, CNSMRs, PYT Service, and FNMGRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FNMGR.toString()) || securityContext.isUserInRole(UserType.CNSMR.toString()) || securityContext.isUserInRole(ServiceType.PYT.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Prohibit CNSMRs from viewing other users' payment details
			if(securityContext.isUserInRole(UserType.CNSMR.toString())) {
				if(! current_user_id.equals(consumer_id)) {
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to view other users' payment details.").toString();
				}
			}

			try {

				JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

				// Verify JSON Object's Validity
				if(!paymentMethodJSON_parsed.has("payment_methods")) {
					return (paymentMethod.readSpecificPaymentMethod(consumer_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
				} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
					return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

				if(readCount == elemCount) {
					return new JsonResponseBuilder().getJsonArrayResponse("payment-methods", resultArray, DBOpStatus.SUCCESSFUL, readCount + " Payment Methods were retrieved successfully.").toString();
				} else {
					return new JsonResponseBuilder().getJsonArrayResponse("payment-methods", resultArray, DBOpStatus.UNSUCCESSFUL, "Only " + readCount +" Payment Methods were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Payment Methods.").toString();
				}

			} catch (Exception ex){
				System.out.println(ex.getMessage()); // Error Logging
				return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
			}
		}
	}

	@PUT
	@Path("/consumers/{consumer_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateConPayMethod(@PathParam("consumer_id") String consumer_id, String paymentMethodJSON, @Context SecurityContext securityContext) {
		// Authorize only ADMINs & CNSMRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.CNSMR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(consumer_id, UserType.CNSMR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit CNSMRs from updating other users' payment details
		if(securityContext.isUserInRole(UserType.CNSMR.toString())) {
			if(! current_user_id.equals(consumer_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to alter other users' payment details.").toString();
			}
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			// Verify JSON Object's Validity
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.updatePaymentMethod(consumer_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("new_creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

			if(updateCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(updateCount + " Payment Methods were updated successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + updateCount +" Payment Methods were Updated. Updating failed for "+ (elemCount-updateCount) + " Consumers.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@DELETE
	@Path("/consumers/{consumer_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteConPayMethod(@PathParam("consumer_id") String consumer_id, String paymentMethodJSON, @Context SecurityContext securityContext) {
		// Authorize only ADMINs & CNSMRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.CNSMR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(consumer_id, UserType.CNSMR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit CNSMRs from deleting other users' payment details
		if(securityContext.isUserInRole(UserType.CNSMR.toString())) {
			if(! current_user_id.equals(consumer_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to remove other users' payment details.").toString();
			}
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			// Verify JSON Object's Validity
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.deletePaymentMethod(consumer_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

			if(deleteCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(deleteCount + " Consumers were deleted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + deleteCount +" Consumers were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Consumers.").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@DELETE
	@Path("/consumers/{consumer_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteConPayMethods(@PathParam("consumer_id") String consumer_id, @QueryParam("all") boolean isAllowed, @Context SecurityContext securityContext) {
		// Check Query Parameter
		if(!isAllowed) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid Request detected. Deleting all Payment Methods of " + consumer_id + " aborted.").toString();
		}

		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(consumer_id, UserType.CNSMR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Authorize only ADMINs & CNSMRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.CNSMR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit CNSMRs from deleting other users' payment details
		if(securityContext.isUserInRole(UserType.CNSMR.toString())) {
			if(! current_user_id.equals(consumer_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to remove other users' payment details.").toString();
			}
		}

		try {
			return (paymentMethod.deletePaymentMethods(consumer_id, UserType.CNSMR)).toString();
		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	//Funder-payment method End-points
	@GET
	@Path("/funders/{funder_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String readFunPayMethods(@PathParam("funder_id") String funder_id, @Context SecurityContext securityContext) {
		//verify user_type	
		if(!new ValidationHandler().validateUserType(funder_id, UserType.FUNDR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString();
		}

		// Authorize only ADMINs, FNMGRs, FUNDRs, and FND Service
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FNMGR.toString())  || securityContext.isUserInRole(UserType.FUNDR.toString()) || securityContext.isUserInRole(ServiceType.FND.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];


		// Prohibit FUNDRs from viewing other users' payment details
		if(securityContext.isUserInRole(UserType.FUNDR.toString())) {
			if(! current_user_id.equals(funder_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to view other users' payment details.").toString();
			}
		}

		return paymentMethod.readPaymentMethods(funder_id).toString();
	}


	@POST
	@Path("/funders/{funder_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertFunPayMethod(@PathParam("funder_id") String funder_id, String paymentMethodJSON, @Context SecurityContext securityContext, @QueryParam("retrieve") boolean isRetrieving) {
		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(funder_id, UserType.FUNDR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		if(!isRetrieving) {
			// Authorize only ADMINs & FUNDRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FUNDR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Prohibit CNSMRs from inserting other users' payment details
			if(securityContext.isUserInRole(UserType.FUNDR.toString())) {
				if(! current_user_id.equals(funder_id)) {
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to add other users' payment details.").toString();
				}
			}

			try {

				JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

				// Verify JSON Object's Validity
				if(!paymentMethodJSON_parsed.has("payment_methods")) {
					return (paymentMethod.insertPaymentMethod(funder_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
				} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
					return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

				if(insertCount == elemCount) {
					return new JsonResponseBuilder().getJsonSuccessResponse(insertCount + " Payment Methods of "+ funder_id +" were inserted successfully.").toString();
				} else {
					return new JsonResponseBuilder().getJsonFailedResponse("Only " + insertCount +" Payment Methods were Inserted. Inserting failed for "+ (elemCount-insertCount) + " given Payment Methods of " + funder_id + ".").toString();
				}

			} catch (Exception ex){
				System.out.println(ex.getMessage()); // Error Logging
				return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
			}
		} else {
			// Authorize only ADMINs, FNMGRs, FUNDRs, and FND Service
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FNMGR.toString())  || securityContext.isUserInRole(UserType.FUNDR.toString()) || securityContext.isUserInRole(ServiceType.FND.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Prohibit CNSMRs from viewing other users' payment details
			if(securityContext.isUserInRole(UserType.FUNDR.toString())) {
				if(! current_user_id.equals(funder_id)) {
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to view other users' payment details.").toString();
				}
			}

			try {

				JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

				// Verify JSON Object's Validity
				if(!paymentMethodJSON_parsed.has("payment_methods")) {
					return (paymentMethod.readSpecificPaymentMethod(funder_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
				} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
					return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

				if(readCount == elemCount) {
					return new JsonResponseBuilder().getJsonArrayResponse("payment-methods", resultArray, DBOpStatus.SUCCESSFUL, readCount + " Payment Methods of " + funder_id + " were retrieved successfully.").toString();
				} else {
					return new JsonResponseBuilder().getJsonArrayResponse("payment-methods", resultArray, DBOpStatus.UNSUCCESSFUL, "Only " + readCount +" Payment Methods were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Payment Methods of " + funder_id + ".").toString();
				}

			} catch (Exception ex){
				System.out.println(ex.getMessage()); // Error Logging
				return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
			}
		}
	}


	@PUT
	@Path("/funders/{funder_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateFunPayMethod(@PathParam("funder_id") String funder_id, String paymentMethodJSON, @Context SecurityContext securityContext) {
		// Authorize only ADMINs & FUNDRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FUNDR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(funder_id, UserType.FUNDR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit FUNDRs from updating other users' payment details
		if(securityContext.isUserInRole(UserType.FUNDR.toString())) {
			if(! current_user_id.equals(funder_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to alter other users' payment details.").toString();
			}
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			// Verify JSON Object's Validity
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.updatePaymentMethod(funder_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("new_creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

			if(updateCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(updateCount + " Payment Methods of " + funder_id + " were updated successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + updateCount +" Payment Methods were Updated. Updating failed for "+ (elemCount-updateCount) + " given Payment Methods of " + funder_id + ".").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@DELETE
	@Path("/funders/{funder_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFunPayMethod(@PathParam("funder_id") String funder_id, String paymentMethodJSON, @Context SecurityContext securityContext) {
		// Authorize only ADMINs & FUNDRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FUNDR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(funder_id, UserType.FUNDR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit FUNDRs from deleting other users' payment details
		if(securityContext.isUserInRole(UserType.FUNDR.toString())) {
			if(! current_user_id.equals(funder_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to remove other users' payment details.").toString();
			}
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			// Verify JSON Object's Validity
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.deletePaymentMethod(funder_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

			if(deleteCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(deleteCount + " Payment Methods of "+ funder_id+ " were deleted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + deleteCount +" Payment Methods were deleted. Deleting failed for "+ (elemCount-deleteCount) + " given Payment Methods of "+ funder_id + ".").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@DELETE
	@Path("/funders/{funder_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteFunPayMethods(@PathParam("funder_id") String funder_id, @QueryParam("all") boolean isAllowed, @Context SecurityContext securityContext) {
		// Check Query Parameter
		if(!isAllowed) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid Request detected. Deleting all Payment Methods of " + funder_id + " aborted.").toString();
		}

		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(funder_id, UserType.FUNDR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Authorize only ADMINs & FUNDRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FUNDR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit FUNDRs from deleting other users' payment details
		if(securityContext.isUserInRole(UserType.FUNDR.toString())) {
			if(! current_user_id.equals(funder_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to remove other users' payment details.").toString();
			}
		}

		try {
			return (paymentMethod.deletePaymentMethods(funder_id, UserType.FUNDR)).toString();
		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	//Researcher-payment method End-points
	@GET
	@Path("/researchers/{researcher_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String readResPayMethods(@PathParam("researcher_id") String researcher_id, @Context SecurityContext securityContext) {
		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Authorize only ADMINs, RSCHRs, PYT Service, and FND Service
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FNMGR.toString()) || securityContext.isUserInRole(UserType.RSCHR.toString()) || securityContext.isUserInRole(ServiceType.PYT.toString()) || securityContext.isUserInRole(ServiceType.FND.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit RSCHRs from viewing other users' payment details
		if(securityContext.isUserInRole(UserType.RSCHR.toString())) {
			if(! current_user_id.equals(researcher_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to view other users' payment details.").toString();
			}
		}

		return paymentMethod.readPaymentMethods(researcher_id).toString();
	}

	@POST
	@Path("/researchers/{researcher_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertResPayMethod(@PathParam("researcher_id") String researcher_id, String paymentMethodJSON, @QueryParam("retrieve") boolean isRetrieving, @Context SecurityContext securityContext) {
		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		if(!isRetrieving) {
			// Authorize only ADMINs & RSCHRs
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.RSCHR.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Prohibit FUNDRs from inserting other users' payment details
			if(securityContext.isUserInRole(UserType.RSCHR.toString())) {
				if(! current_user_id.equals(researcher_id)) {
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to add other users' payment details.").toString();
				}
			}

			try {

				JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

				// Verify JSON Object's Validity
				if(!paymentMethodJSON_parsed.has("payment_methods")) {
					return (paymentMethod.insertPaymentMethod(researcher_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
				} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
					return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

				if(insertCount == elemCount) {
					return new JsonResponseBuilder().getJsonSuccessResponse(insertCount + " Payment Methods of "+ researcher_id +" were inserted successfully.").toString();
				} else {
					return new JsonResponseBuilder().getJsonFailedResponse("Only " + insertCount +" Payment Methods were Inserted. Inserting failed for "+ (elemCount-insertCount) + " given Payment Methods of " + researcher_id + ".").toString();
				}

			} catch (Exception ex){
				System.out.println(ex.getMessage()); // Error Logging
				return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
			}
		} else {
			// Authorize only ADMINs, RSCHRs, PYT Service, and FND Service
			if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.FNMGR.toString()) || securityContext.isUserInRole(UserType.RSCHR.toString()) || securityContext.isUserInRole(ServiceType.PYT.toString()) || securityContext.isUserInRole(ServiceType.FND.toString()))) {
				return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
			}

			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Prohibit RSCHRs from viewing other users' payment details
			if(securityContext.isUserInRole(UserType.RSCHR.toString())) {
				if(! current_user_id.equals(researcher_id)) {
					return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to view other users' payment details.").toString();
				}
			}

			try {

				JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

				// Verify JSON Object's Validity
				if(!paymentMethodJSON_parsed.has("payment_methods")) {
					return (paymentMethod.readSpecificPaymentMethod(researcher_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
				} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
					return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

				if(readCount == elemCount) {
					return new JsonResponseBuilder().getJsonArrayResponse("payment-methods", resultArray, DBOpStatus.SUCCESSFUL, readCount + " Payment Methods of " + researcher_id + " were retrieved successfully.").toString();
				} else {
					return new JsonResponseBuilder().getJsonArrayResponse("payment-methods", resultArray, DBOpStatus.UNSUCCESSFUL, "Only " + readCount +" Payment Methods were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Payment Methods of " + researcher_id + ".").toString();
				}

			} catch (Exception ex){
				System.out.println(ex.getMessage()); // Error Logging
				return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
			}
		}
	}


	@PUT
	@Path("/researchers/{researcher_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateResPayMethod(@PathParam("researcher_id") String researcher_id, String paymentMethodJSON, @Context SecurityContext securityContext) {
		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Authorize only ADMINs & RSCHRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.RSCHR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit FUNDRs from updating other users' payment details
		if(securityContext.isUserInRole(UserType.RSCHR.toString())) {
			if(! current_user_id.equals(researcher_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to alter other users' payment details.").toString();
			}
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			// Verify JSON Object's Validity
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.updatePaymentMethod(researcher_id, paymentMethodJSON_parsed.get("creditcard_type").getAsString(), paymentMethodJSON_parsed.get("new_creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_no").getAsString(), paymentMethodJSON_parsed.get("creditcard_security_no").getAsString(), paymentMethodJSON_parsed.get("exp_date").getAsString(), paymentMethodJSON_parsed.get("billing_address").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

			if(updateCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(updateCount + " Payment Methods of " + researcher_id + " were updated successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + updateCount +" Payment Methods were Updated. Updating failed for "+ (elemCount-updateCount) + " given Payment Methods of " + researcher_id + ".").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage());
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@DELETE
	@Path("/researchers/{researcher_id}/payment-methods")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteResPayMethod(@PathParam("researcher_id") String researcher_id, String paymentMethodJSON, @Context SecurityContext securityContext) {
		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Authorize only ADMINs & RSCHRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.RSCHR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit FUNDRs from deleting other users' payment details
		if(securityContext.isUserInRole(UserType.RSCHR.toString())) {
			if(! current_user_id.equals(researcher_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to remove other users' payment details.").toString();
			}
		}

		try {

			JsonObject paymentMethodJSON_parsed = new JsonParser().parse(paymentMethodJSON).getAsJsonObject();

			// Verify JSON Object's Validity
			if(!paymentMethodJSON_parsed.has("payment_methods")) {
				return (paymentMethod.deletePaymentMethod(researcher_id, paymentMethodJSON_parsed.get("creditcard_no").getAsString())).toString();
			} else if (!paymentMethodJSON_parsed.get("payment_methods").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
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

			if(deleteCount == elemCount) {
				return new JsonResponseBuilder().getJsonSuccessResponse(deleteCount + " Payment Methods of "+ researcher_id+ " were deleted successfully.").toString();
			} else {
				return new JsonResponseBuilder().getJsonFailedResponse("Only " + deleteCount +" Payment Methods were deleted. Deleting failed for "+ (elemCount-deleteCount) + " given Payment Methods of "+ researcher_id + ".").toString();
			}

		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}

	@DELETE
	@Path("/researchers/{researcher_id}/payment-methods")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteResPayMethods(@PathParam("researcher_id") String researcher_id, @QueryParam("all") boolean isAllowed, @Context SecurityContext securityContext)
	{
		// Check Query Parameter
		if(!isAllowed) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid Request detected. Deleting all Payment Methods of " + researcher_id + " aborted.").toString();
		}

		// Verify requested ID Pattern
		if(!new ValidationHandler().validateUserType(researcher_id, UserType.RSCHR)) {
			return new JsonResponseBuilder().getJsonErrorResponse("Invalid User ID Format.").toString(); 
		}

		// Authorize only ADMINs & RSCHRs
		if(! (securityContext.isUserInRole(UserType.ADMIN.toString()) || securityContext.isUserInRole(UserType.RSCHR.toString()))) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Prohibit FUNDRs from deleting other users' payment details
		if(securityContext.isUserInRole(UserType.RSCHR.toString())) {
			if(! current_user_id.equals(researcher_id)) {
				return new JsonResponseBuilder().getJsonProhibitedResponse("You are NOT Allowed to remove other users' payment details.").toString();
			}
		}

		try {
			return (paymentMethod.deletePaymentMethods(researcher_id, UserType.RSCHR)).toString();
		} catch (Exception ex){
			System.out.println(ex.getMessage()); // Error Logging
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}
}