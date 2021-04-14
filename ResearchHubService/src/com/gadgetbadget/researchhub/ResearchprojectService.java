package com.gadgetbadget.researchhub;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gadgetbadget.researchhub.model.Category;
import com.gadgetbadget.researchhub.model.Researchproject;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/research-projects")
public class ResearchprojectService {
	Category category = new Category();
	Researchproject project = new Researchproject();
	
	 // Categories related End-points.
		@GET
		@Path("/categories")
		@Produces(MediaType.APPLICATION_JSON)
		public String readCategory()
		{
			return category.readCategory().toString();
		}
		
		@POST
		@Path("/categories")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String insertCategory(String categoryJSON)
		{
			JsonObject result = null;

			try {

				JsonObject categoryJSON_parsed = new JsonParser().parse(categoryJSON).getAsJsonObject();

				//check if multiple inserts
				if(!categoryJSON_parsed.has("categories")) {
					return (category.insertCategory(categoryJSON_parsed.get("category_name").getAsString(),categoryJSON_parsed.get("category_description").getAsString(), categoryJSON_parsed.get("last_modified_by").getAsString())).toString();
				} else if (!categoryJSON_parsed.get("categories").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}

				int insertCount = 0;
				int elemCount = categoryJSON_parsed.get("categories").getAsJsonArray().size();

				for (JsonElement categoryElem : categoryJSON_parsed.get("categories").getAsJsonArray()) {
					JsonObject categoryObj = categoryElem.getAsJsonObject();
					JsonObject response = (category.insertCategory(categoryObj.get("category_name").getAsString(),categoryObj.get("category_description").getAsString(), categoryObj.get("last_modified_by").getAsString()));

					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					}
				}

				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Categories were inserted successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" Categories were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Categories.");
				}

			} catch (Exception ex){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
			}

			return result.toString();
		}
		
		@PUT
		@Path("/categories")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String updateCategory(String categoryJSON)
		{
			JsonObject result = null;

			try {

				JsonObject categoryJSON_parsed = new JsonParser().parse(categoryJSON).getAsJsonObject();

				//check if multiple inserts
				if(!categoryJSON_parsed.has("categories")) {
					return (category.updateCategory(categoryJSON_parsed.get("category_id").getAsString(),categoryJSON_parsed.get("category_name").getAsString(),categoryJSON_parsed.get("category_description").getAsString(), categoryJSON_parsed.get("last_modified_by").getAsString())).toString();
				} else if (!categoryJSON_parsed.get("categories").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}

				int insertCount = 0;
				int elemCount = categoryJSON_parsed.get("categories").getAsJsonArray().size();

				for (JsonElement categoryElem : categoryJSON_parsed.get("categories").getAsJsonArray()) {
					JsonObject categoryObj = categoryElem.getAsJsonObject();
					JsonObject response = (category.updateCategory(categoryObj.get("category_id").getAsString(),categoryObj.get("category_name").getAsString(),categoryObj.get("category_description").getAsString(), categoryObj.get("last_modified_by").getAsString()));

					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					}
				}

				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Categories were updated successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" Categories were Updated. Updating failed for "+ (elemCount-insertCount) + " Categories.");
				}

			} catch (Exception ex){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
			}

			return result.toString();
		}
 
		
		@DELETE
		@Path("/categories")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String deleteCategory(String categoryJSON)
		{
			JsonObject result = null;

			try {

				JsonObject categoryJSON_parsed = new JsonParser().parse(categoryJSON).getAsJsonObject();

				//check if multiple inserts
				if(!categoryJSON_parsed.has("categories")) {
					return (category.deleteCategory(categoryJSON_parsed.get("category_id").getAsString())).toString();
				} else if (!categoryJSON_parsed.get("categories").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}

				int insertCount = 0;
				int elemCount = categoryJSON_parsed.get("categories").getAsJsonArray().size();

				for (JsonElement categoryElem : categoryJSON_parsed.get("categories").getAsJsonArray()) {
					JsonObject categoryObj = categoryElem.getAsJsonObject();
					JsonObject response = (category.deleteCategory(categoryObj.get("category_id").getAsString()));

					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					}
				}

				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS","SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Categories were deleted successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" Categories were deleted. Deleting failed for "+ (elemCount-insertCount) + " Categories.");
				}

			} catch (Exception ex){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
			}

			return result.toString();
		}
		
		
		
		
		// Projects related End-points.
		@GET
		@Path("/projects")
		@Produces(MediaType.APPLICATION_JSON)
		public String readProject()
		{
			return project.readProject().toString();
		}
		
		
		@POST
		@Path("/projects")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String insertProject(String projectJSON)
		{
			JsonObject result = null;

			try {

				JsonObject projectJSON_parsed = new JsonParser().parse(projectJSON).getAsJsonObject();

				//check if multiple inserts
				if(!projectJSON_parsed.has("projects")) {
					return (project.insertProject(projectJSON_parsed.get("researcher_id").getAsString(),projectJSON_parsed.get("product_name").getAsString(),projectJSON_parsed.get("project_description").getAsString(),projectJSON_parsed.get("category_id").getAsString(),projectJSON_parsed.get("project_start_date").getAsString(),projectJSON_parsed.get("project_end_date").getAsString(), projectJSON_parsed.get("expected_total_budget").getAsString())).toString();
				} else if (!projectJSON_parsed.get("projects").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}

				int insertCount = 0;
				int elemCount = projectJSON_parsed.get("projects").getAsJsonArray().size();

				for (JsonElement projectElem : projectJSON_parsed.get("projects").getAsJsonArray()) {
					JsonObject projectObj = projectElem.getAsJsonObject();
					JsonObject response = (project.insertProject(projectObj.get("researcher_id").getAsString(),projectObj.get("product_name").getAsString(),projectObj.get("project_description").getAsString(),projectObj.get("category_id").getAsString(),projectObj.get("project_start_date").getAsString(),projectObj.get("project_end_date").getAsString(), projectObj.get("expected_total_budget").getAsString()));

					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					}
				}

				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Projects were inserted successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" Projects were Inserted. Inserting failed for "+ (elemCount-insertCount) + " projects.");
				}

			} catch (Exception ex){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
			}

			return result.toString();
		}
		
		
		@PUT
		@Path("/projects")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String updateProject(String projectJSON)
		{
			JsonObject result = null;

			try {

				JsonObject projectJSON_parsed = new JsonParser().parse(projectJSON).getAsJsonObject();

				//check if multiple inserts
				if(!projectJSON_parsed.has("projects")) {
					return (project.updateProject(projectJSON_parsed.get("project_id").getAsString(),projectJSON_parsed.get("researcher_id").getAsString(),projectJSON_parsed.get("product_name").getAsString(),projectJSON_parsed.get("project_description").getAsString(),projectJSON_parsed.get("category_id").getAsString(),projectJSON_parsed.get("project_start_date").getAsString(),projectJSON_parsed.get("project_end_date").getAsString(), projectJSON_parsed.get("expected_total_budget").getAsString())).toString();
				} else if (!projectJSON_parsed.get("projects").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}

				int insertCount = 0;
				int elemCount = projectJSON_parsed.get("projects").getAsJsonArray().size();

				for (JsonElement projectElem : projectJSON_parsed.get("projects").getAsJsonArray()) {
					JsonObject projectObj = projectElem.getAsJsonObject();
					JsonObject response = (project.updateProject(projectObj.get("project_id").getAsString(),projectObj.get("researcher_id").getAsString(),projectObj.get("product_name").getAsString(),projectObj.get("project_description").getAsString(),projectObj.get("category_id").getAsString(),projectObj.get("project_start_date").getAsString(),projectObj.get("project_end_date").getAsString(), projectObj.get("expected_total_budget").getAsString()));

					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					}
				}

				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Projects were updated successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" Projects were Updated. Updating failed for "+ (elemCount-insertCount) + " Projects.");
				}

			} catch (Exception ex){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
			}

			return result.toString();
		}


		
}
