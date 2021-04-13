package com.gadgetbadget.user.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonResponseBuilder {
	private JsonObject jsonObject = null;

	public JsonResponseBuilder() {

	}

	public JsonObject getJsonResponse(String name, JsonArray jsonArray) {
		jsonObject = new JsonObject();
		jsonObject.add(name, jsonArray);
		return jsonObject;
	}

	public JsonObject getJsonResponse(String status, String message) {
		jsonObject = new JsonObject();
		jsonObject.addProperty("STATUS", status);
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}
	
	public JsonObject getJsonResponse(String name, JsonArray jsonArray, String status, String message) {
		jsonObject = new JsonObject();
		jsonObject.add(name, jsonArray);
		jsonObject.addProperty("STATUS", status);
		jsonObject.addProperty("MESSAGE", message);
		return jsonObject;
	}

}
