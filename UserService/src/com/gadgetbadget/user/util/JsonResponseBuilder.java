package com.gadgetbadget.user.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * JsonResponseBuilder class is responsible for generate status messages as a JSON object.
 * Status Types have been defined in DBOpStatus class as an ENUM class which eliminates typos 
 * when comparing string values within the project classes.
 * Any class can create a JSON response by passing the status message as an argument for the 
 * appropriate method which returns a JSON object that can be returned to the calling method.
 * 
 * @author Ishara_Dissanayake
 */
public class JsonResponseBuilder {
	private JsonObject jsonObject = null;

	public JsonResponseBuilder() {

	}

	public JsonObject getJsonErrorResponse( String message) {
		jsonObject = new JsonObject();
		jsonObject.addProperty("STATUS", DBOpStatus.ERROR.toString());
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}
	
	public JsonObject getJsonExceptionResponse( String message) {
		jsonObject = new JsonObject();
		jsonObject.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}
	
	public JsonObject getJsonUnknownResponse( String message) {
		jsonObject = new JsonObject();
		jsonObject.addProperty("STATUS", DBOpStatus.UNKNOWN.toString());
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}
	
	public JsonObject getJsonUnauthorizedResponse( String message) {
		jsonObject = new JsonObject();
		jsonObject.addProperty("STATUS", DBOpStatus.UNAUTHORIZED.toString());
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}
	
	public JsonObject getJsonSuccessResponse( String message) {
		jsonObject = new JsonObject();
		jsonObject.addProperty("STATUS", DBOpStatus.SUCCESSFUL.toString());
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}
	
	public JsonObject getJsonFailedResponse( String message) {
		jsonObject = new JsonObject();
		jsonObject.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}
	
	public JsonObject getJsonProhibitedResponse( String message) {
		jsonObject = new JsonObject();
		jsonObject.addProperty("STATUS", DBOpStatus.PROHIBITED.toString());
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}
	
	/**
	 * This method returns a single JSON object which acts as a wrapper object to a passed JSON Array along
	 * with status of the operation. 
	 * 
	 * @param name 			name given for the JSON array
	 * @param jsonArray 	original JSON array that needs to be wrapped by a JSON object
	 * @param dbOpStatus	type of the status caused by the operation while obtaining the JSON array
	 * @param message		description of the status
	 * @return				returns a single JSON object which contains a named JSON array and status details along with it
	 */
	public JsonObject getJsonArrayResponse(String name, JsonArray jsonArray, DBOpStatus dbOpStatus, String message) {
		jsonObject = new JsonObject();
		jsonObject.add(name, jsonArray);
		jsonObject.addProperty("STATUS", dbOpStatus.toString());
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}

	/**
	 * This method returns a single JSON object which acts as a wrapper object to a passed JSON Array.
	 * 
	 * @param name name of the JSON array
	 * @param jsonArray	the original JSON array which needs to be wrapped by a JSON Object
	 * @return a JSON object which wraps a JSON Array
	 */
	public JsonObject getJsonArrayResponse(String name, JsonArray jsonArray) {
		jsonObject = new JsonObject();
		jsonObject.add(name, jsonArray);
		return jsonObject;
	}

}
