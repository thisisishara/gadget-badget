package com.gadgetbadget.user;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gadgetbadget.user.model.Role;
import com.gadgetbadget.user.util.DBOpStatus;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/security")
public class SecurityService {
	Role role = new Role();

	// Signing End-point
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String login() {
		return "Login is not implemented yet.";
	}

	// Authenticating End-point
	@POST
	@Path("/authentication")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String authentication() {
		return "Login is not implemented yet.";
	}

	// Roles related End-points.
	@GET
	@Path("/roles")
	@Produces(MediaType.APPLICATION_JSON)
	public String readItems()
	{
		return role.readRoles().toString();
	}

	@POST
	@Path("/roles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertItem(String roleJSON)
	{
		JsonObject result = null;

		try {

			JsonObject roleJSON_parsed = new JsonParser().parse(roleJSON).getAsJsonObject();

			//check if multiple inserts
			if(!roleJSON_parsed.has("roles")) {
				return (role.insertRole(roleJSON_parsed.get("role_id").getAsString(), roleJSON_parsed.get("role_description").getAsString())).toString();
			}

			int insertCount = 0;
			int elemCount = roleJSON_parsed.get("roles").getAsJsonArray().size();
			
			for (JsonElement roleElem : roleJSON_parsed.get("roles").getAsJsonArray()) {
				JsonObject roleObj = roleElem.getAsJsonObject();
				JsonObject response = (role.insertRole(roleObj.get("role_id").getAsString(), roleObj.get("role_description").getAsString()));
				
				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFULL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", insertCount + " Roles Inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Roles were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Roles.");
			}
			
		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}
	/*
	@PUT
	@Path("/roles")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateRole(String itemData)
	{
		//Convert the input string to a JSON object
		JsonObject itemObject = new JsonParser().parse(itemData).getAsJsonObject();
		//Read the values from the JSON object
		String itemID = itemObject.get("itemID").getAsString();
		String itemCode = itemObject.get("itemCode").getAsString();
		String itemName = itemObject.get("itemName").getAsString();
		String itemPrice = itemObject.get("itemPrice").getAsString();
		String itemDesc = itemObject.get("itemDesc").getAsString();
		String output = itemObj.updateItem(itemID, itemCode, itemName, itemPrice, itemDesc);
		return output;
	}

	
	@DELETE
	@Path("/roles")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteItem(String itemData)
	{
		//Convert the input string to an XML document
		Document doc = Jsoup.parse(itemData, "", Parser.xmlParser());
		//Read the value from the element <itemID>
		String itemID = doc.select("itemID").text();
		String output = itemObj.deleteItem(itemID);
		return output;
	}*/
}
