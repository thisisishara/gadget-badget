package com.gadgetbadget.user;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.gadgetbadget.user.model.Role;
import com.gadgetbadget.user.model.User;
import com.gadgetbadget.user.security.JWTHandler;
import com.gadgetbadget.user.util.DBOpStatus;
import com.gadgetbadget.user.util.JsonResponseBuilder;
import com.gadgetbadget.user.util.UserType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * This Resource class represents account security related end-points
 * Usually only ADMINs can access end-points implemented within this class.
 * 
 * @author Ishara_Dissanayake
 */
@Path("/security")
public class SecurityResource {
	Role role = new Role();
	User user = new User();

	// Authentication End-point
	@POST
	@Path("/authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String authenticate(String authJSON) {
		JsonObject result = null;
		try {

			JsonObject authJSON_parsed = new JsonParser().parse(authJSON).getAsJsonObject();

			// Check JSON elements
			if(! (authJSON_parsed.has("username") && authJSON_parsed.has("password"))) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}
			
			// Retrieve corresponding user
			result = user.getUserById(authJSON_parsed.get("username").getAsString(), authJSON_parsed.get("password").getAsString());
			
			if (result==null || !result.has("username")) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid Credentials.").toString();
			}
			
			if (result.get("is_deactivated").getAsString().equalsIgnoreCase("yes")) {
				return new JsonResponseBuilder().getJsonErrorResponse("User account has been deactivated.").toString();
			}
			
			// Obtain a JWT
			String jwt = new JWTHandler().generateToken(result.get("username").getAsString(), result.get("user_id").getAsString(), result.get("role").getAsString());
			
			if (! (jwt==null || new JWTHandler().validateToken(jwt))) {
				return new JsonResponseBuilder().getJsonErrorResponse("Failed to Issue a valid JWT Authentication Token.").toString();
			}
			
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.AUTHENTICATED.toString());
			result.addProperty("JWT Auth Token", jwt);
			return result.toString();
			
		} catch (Exception ex){
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
	}
	

	//Roles related End-points.
	@GET
	@Path("/roles")
	@Produces(MediaType.APPLICATION_JSON)
	public String readRoles(@Context SecurityContext securityContext)
	{
		//Allow only UserType ADMIN
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		return role.readRoles().toString();
	}


	@POST
	@Path("/roles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertRole(String roleJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		//Allow only UserType ADMIN
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		try {

			JsonObject roleJSON_parsed = new JsonParser().parse(roleJSON).getAsJsonObject();

			//check if multiple inserts
			if(!roleJSON_parsed.has("roles")) {
				return (role.insertRole(roleJSON_parsed.get("role_id").getAsString(), roleJSON_parsed.get("role_description").getAsString())).toString();
			} else if (!roleJSON_parsed.get("roles").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			int insertCount = 0;
			int elemCount = roleJSON_parsed.get("roles").getAsJsonArray().size();

			for (JsonElement roleElem : roleJSON_parsed.get("roles").getAsJsonArray()) {
				JsonObject roleObj = roleElem.getAsJsonObject();
				JsonObject response = (role.insertRole(roleObj.get("role_id").getAsString(), roleObj.get("role_description").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", insertCount + " Roles were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Roles were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Roles.");
			}

		} catch (Exception ex){
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}
		return result.toString();
	}

	@PUT
	@Path("/roles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateRole(String roleJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		//Allow only UserType ADMIN
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		try {

			JsonObject roleJSON_parsed = new JsonParser().parse(roleJSON).getAsJsonObject();

			//check if multiple inserts
			if(!roleJSON_parsed.has("roles")) {
				return (role.updateRole(roleJSON_parsed.get("role_id").getAsString(), roleJSON_parsed.get("role_description").getAsString())).toString();
			} else if (!roleJSON_parsed.get("roles").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			int updateCount = 0;
			int elemCount = roleJSON_parsed.get("roles").getAsJsonArray().size();

			for (JsonElement roleElem : roleJSON_parsed.get("roles").getAsJsonArray()) {
				JsonObject roleObj = roleElem.getAsJsonObject();
				JsonObject response = (role.updateRole(roleObj.get("role_id").getAsString(), roleObj.get("role_description").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", updateCount + " Roles were updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + updateCount +" Roles were Updated. Updating failed for "+ (elemCount-updateCount) + " Roles.");
			}

		} catch (Exception ex){
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}

		return result.toString();
	}

	@DELETE
	@Path("/roles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteRole(String roleJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		//Allow only UserType ADMIN
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		try {

			JsonObject roleJSON_parsed = new JsonParser().parse(roleJSON).getAsJsonObject();

			//check if multiple inserts
			if(!roleJSON_parsed.has("roles")) {
				return (role.deleteRole(roleJSON_parsed.get("role_id").getAsString())).toString();
			} else if (!roleJSON_parsed.get("roles").isJsonArray()) {
				return new JsonResponseBuilder().getJsonErrorResponse("Invalid JSON Object.").toString();
			}

			int deleteCount = 0;
			int elemCount = roleJSON_parsed.get("roles").getAsJsonArray().size();

			for (JsonElement roleElem : roleJSON_parsed.get("roles").getAsJsonArray()) {
				JsonObject roleObj = roleElem.getAsJsonObject();
				JsonObject response = (role.deleteRole(roleObj.get("role_id").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFUL.toString())) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", deleteCount + " Roles were deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + deleteCount +" Roles were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Roles.");
			}

		} catch (Exception ex){
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex.getMessage()).toString();
		}

		return result.toString();
	}
}
