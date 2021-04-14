package com.gadgetbadget.user.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
	
	public JsonObject getJsonArrayResponse(String name, JsonArray jsonArray, String status, String message) {
		jsonObject = new JsonObject();
		jsonObject.add(name, jsonArray);
		jsonObject.addProperty("STATUS", status);
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}

	public JsonObject getJsonArrayResponse(String name, JsonArray jsonArray) {
		jsonObject = new JsonObject();
		jsonObject.add(name, jsonArray);
		return jsonObject;
	}

}
