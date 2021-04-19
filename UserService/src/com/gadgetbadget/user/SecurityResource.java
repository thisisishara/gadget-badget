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
 * Usually only ADMINs can access end-points implemented within this class
 * except the authenticate sub resource which is used to signing in users
 * and provide a JWT after authenticating a user.
 * 
 * @author Ishara_Dissanayake
 */
@Path("/security")
public class SecurityResource {
	Role role = new Role();
	User user = new User();

	/**
	 * This API End-point is used to sign in a particular user when they have provided a user-name and
	 * a password in the form of raw JSON in the request PAYLOAD. The authentication filter releases a
	 * request made to this end-point immediately after identifying the path.
	 * 
	 * @param authJSON	JSON object containing user-name and password of the signing in user
	 * @return			returns a JSON formatted string with a JWT included if authenticated. If failed to authenticate, a
	 * 					JSON response will be returned with appropriate information on what went exactly wrong.
	 */
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
			
			// Return the result including the generated JWT
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.AUTHENTICATED.toString());
			result.addProperty("JWT Auth Token", jwt);
			return result.toString();
			
		} catch (Exception ex){
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex).toString();
		}
	}
	

	/**
	 * GET method of sub resource roles can be only accessed by users with administrator privileges, meaning that the role id
	 * of the user must be UserType.ADMIN to get authenticated to read the list of roles provided by GadgetBadget.
	 * 
	 * @param securityContext	The role, user-name, and the user ID are attached into securityContext when the users authenticate
	 * 							themselves by providing a valid JWT obtained when called "/authenticate" end-point. The Authorization 
	 * 							filter does the heavy work when processing the user requests and populates the security context of the
	 * 							request made with decoded user information included in the JWT provided by the user.
	 * @return					returns a JSON formatted string containing the list of roles if the requester is an ADMIN or else returns
	 * 							a JSON formatted string with the status of the operation and a description of the status.
	 */
	@GET
	@Path("/roles")
	@Produces(MediaType.APPLICATION_JSON)
	public String readRoles(@Context SecurityContext securityContext)
	{
		// Allow only UserType ADMIN
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		return role.readRoles().toString();
	}

	/**
	 * POST method of the "/roles" end-point takes a list of new role details in the form of a JSON formatted string and allows
	 * it to be saved if the user who sent the request is authorized to do so. User must be in ADMIN role to perform this task.
	 * The input JSON formatted string can be of two types depending on the structure and a simple JSON object represents a single role
	 * while a JSON object can also contain a JSON array named "roles" which may contain multiple roles.
	 *  
	 * @param roleJSON			JSON formatted string containing the new role details.
	 * @param securityContext	contains authenticated user's critical information
	 * @return					returns a JSON formatted string with status and status message of saving the role.
	 */
	@POST
	@Path("/roles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertRole(String roleJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		// Allow only UserType ADMIN
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		try {

			JsonObject roleJSON_parsed = new JsonParser().parse(roleJSON).getAsJsonObject();

			// Check if multiple inserts
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

			// Include how many roles have been inserted from the given role id list in the response
			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", insertCount + " Roles were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Roles were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Roles.");
			}

		} catch (Exception ex){
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex).toString();
		}
		return result.toString();
	}

	/**
	 * PUT method of "/roles" end-point allows the authorized users to submit a JSON formatted string containing an existing role id
	 * and new information of fields that needs to be updated in that specific role. User must be an ADMIN in this case and the role id 
	 * should be an existing id in the roles table of the local database of the user service. The input JSON formatted string can be of 
	 * two types depending on the structure and a simple JSON object represents a single role while a JSON object can also contain a 
	 * JSON array named "roles" which may contain multiple roles.
	 * 
	 * @param roleJSON			contains the new information of an existing role to be updated in the form of a JSON formatted string
	 * 							with its role id specified.
	 * @param securityContext	contains authenticated user's critical information
	 * @return					returns a JSON formatted string with status and status message of updating the role/roles.
	 */
	@PUT
	@Path("/roles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateRole(String roleJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		// Allow only UserType ADMIN
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		try {

			JsonObject roleJSON_parsed = new JsonParser().parse(roleJSON).getAsJsonObject();

			// Check if multiple inserts
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

			// Include how many roles have been updated from the given role id list in the response
			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", updateCount + " Roles were updated successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + updateCount +" Roles were Updated. Updating failed for "+ (elemCount-updateCount) + " Roles.");
			}

		} catch (Exception ex){
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex).toString();
		}

		return result.toString();
	}

	/**
	 * DELETE method of "/roles" end-point allows deleting a role that already exists in the local database table.The input JSON formatted string can be of 
	 * two types depending on the structure and a simple JSON object represents a single role while a JSON object can also contain a 
	 * JSON array named "roles" which may contain multiple roles. roleJSON only contains the role ID/s.
	 * 
	 * @param roleJSON			contains the id of an existing role/set of IDs of existing roles in the form of a JSON formatted string.
	 * @param securityContext	contains authenticated user's critical information.
	 * @return					returns a JSON formatted string with status and status message of deleting the role/roles.
	 */
	@DELETE
	@Path("/roles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteRole(String roleJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		// Allow only UserType ADMIN
		if(!securityContext.isUserInRole(UserType.ADMIN.toString())) {
			return new JsonResponseBuilder().getJsonUnauthorizedResponse("You are not Authorized to access this End-point!").toString();
		}

		try {

			JsonObject roleJSON_parsed = new JsonParser().parse(roleJSON).getAsJsonObject();

			// Check if multiple inserts
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

			// Include how many roles have been deleted from the given role id list in the response
			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
				result.addProperty("MESSAGE", deleteCount + " Roles were deleted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + deleteCount +" Roles were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Roles.");
			}

		} catch (Exception ex){
			return new JsonResponseBuilder().getJsonExceptionResponse("Exception Details: " + ex).toString();
		}

		return result.toString();
	}
}
