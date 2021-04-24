package com.gadgetbadget.researchhub;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.gadgetbadget.researchhub.model.Category;
import com.gadgetbadget.researchhub.model.Collaborator;
import com.gadgetbadget.researchhub.model.Researchproject;
import com.gadgetbadget.researchhub.util.InterServiceCommHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/research-projects")
public class ResearchProjectResource {
	Category category = new Category();
	Researchproject project = new Researchproject();
	Collaborator collaborator=new Collaborator();

	// Projects related End-points.
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String readProject(@Context SecurityContext securityContext, @QueryParam("researcherid") String researcher_id, @QueryParam("summarized") boolean isSummarized, @QueryParam("filtered") boolean isFiltered) {
		JsonObject result = null;

		// Authorize only ADMINs,Funder, Researchers and User service
		if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("USR") || securityContext.isUserInRole("RSCHR") || securityContext.isUserInRole("FUNDR") || securityContext.isUserInRole("FNMGR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Authenticated user id
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		try {

			if(securityContext.isUserInRole("USR")) {
				if(researcher_id != null && isSummarized == true) {
					return project.readProjectSummeryByResearcherId(researcher_id).toString();
				}

				result = new JsonObject();
				result.addProperty("STATUS", "PROHIBITED");
				result.addProperty("MESSAGE","You are NOT Allowed to retrieve summarized research project details.");
				return result.toString();

			}

			if(securityContext.isUserInRole("RSCHR") && isFiltered) {
				return project.readProjects(current_user_id).toString();
			}

			return project.readProjects(null).toString();

		} catch(Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex);
			return result.toString();
		}
	}


	@GET
	@Path("/{project_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readProduct(@Context SecurityContext securityContext, @PathParam("project_id") String project_id, @QueryParam("filtered") boolean isFiltered) {

		JsonObject result = null;

		// User Authorization
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR") || securityContext.isUserInRole("FNMGR") || securityContext.isUserInRole("FUNDR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if its a Single ID or Multiple IDs
		if(!project_id.contains(",")) {
			// Allow retrieving only if the IDs are matched for NON ADMINs
			if(securityContext.isUserInRole("RSCHR") && isFiltered) {
				return project.readProject(current_user_id, project_id).toString();
			}

			return project.readProject(null, project_id).toString();
		}

		//if multiple id s
		String[] ids = project_id.split(",");

		int readCount = 0;
		int elemCount = ids.length;
		JsonArray resultArray = new JsonArray();

		for (String id : ids) {
			JsonObject response = null;
			if(securityContext.isUserInRole("RSCHR") && isFiltered) {
				response = project.readProject(current_user_id, id);
			} else {
				response = project.readProject(null, id);
			}

			if (!response.has("MESSAGE")) {
				readCount++;
				resultArray.add(response);
			} 
		}

		result = new JsonObject();
		result.add("projects", resultArray);

		if(readCount == elemCount) {
			result.addProperty("STATUS", "SUCCESSFUL");
			result.addProperty("MESSAGE", readCount + " Projects were retrieved successfully.");

		} else {
			result.addProperty("STATUS", "UNSUCCESSFUL");
			result.addProperty("MESSAGE", "Only " + readCount +" Projects were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Projects.");
		}

		return result.toString();
	}

	//Inter-service communication is used here with funding service
	@GET
	@Path("/{project_id}/funds")
	@Produces(MediaType.APPLICATION_JSON)
	public String readProductFunds(@Context SecurityContext securityContext, @PathParam("project_id") String project_id) {

		JsonObject result = null;

		// Authorize only ADMINs, FUNDRs
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR") || securityContext.isUserInRole("FNMGR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		if(securityContext.isUserInRole("RSCHR")) {
			if(!project.isOwner(current_user_id, project_id)) {
				result = new JsonObject();
				result.addProperty("STATUS", "PROHIBITED");
				result.addProperty("MESSAGE","You are not Allowed to view list of payments received by other researchers' projects.");
				return result.toString();
			}
		}

		return (new InterServiceCommHandler().fundingIntercomms("funds?researchid=" + project_id)).toString();
	}


	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertProject(String projectJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & EMPLYs
			if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR"))) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
				return result.toString();
			}

			JsonObject projectJSON_parsed = new JsonParser().parse(projectJSON).getAsJsonObject();

			//check if multiple inserts
			if(!projectJSON_parsed.has("projects")) {
				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! projectJSON_parsed.get("researcher_id").getAsString().equals(current_user_id)){
						result = new JsonObject();
						result.addProperty("STATUS", "PROHIBITED");
						result.addProperty("MESSAGE","You are NOT Allowed to add research projects on behalf of other researchers.");
						return result.toString();
					}
				}

				return (project.insertProject(projectJSON_parsed.get("researcher_id").getAsString(),projectJSON_parsed.get("project_name").getAsString(),projectJSON_parsed.get("project_description").getAsString(),projectJSON_parsed.get("category_id").getAsString(),projectJSON_parsed.get("project_start_date").getAsString(),projectJSON_parsed.get("project_end_date").getAsString(), projectJSON_parsed.get("expected_total_budget").getAsString())).toString();
			} else if (!projectJSON_parsed.get("projects").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = projectJSON_parsed.get("projects").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement projectElem : projectJSON_parsed.get("projects").getAsJsonArray()) {
				JsonObject projectObj = projectElem.getAsJsonObject();

				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! projectObj.get("researcher_id").getAsString().equals(current_user_id)){
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("id_mismatch", "Your user id does not match with the researcher id given in the payload. You are not allowed to add research projects on behalf of other researchers.");
						errorsArr.add(errorElem);
						continue;
					}
				}


				JsonObject response = (project.insertProject(projectObj.get("researcher_id").getAsString(),projectObj.get("project_name").getAsString(),projectObj.get("project_description").getAsString(),projectObj.get("category_id").getAsString(),projectObj.get("project_start_date").getAsString(),projectObj.get("project_end_date").getAsString(), projectObj.get("expected_total_budget").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
					insertCount++;
				} else {
					JsonObject errorElem = new JsonObject();
					errorElem.addProperty("exception", response.get("MESSAGE").getAsString());
					errorsArr.add(errorElem);
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
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateProject(String projectJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & Researchers
			if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR"))) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
				return result.toString();
			}

			JsonObject projectJSON_parsed = new JsonParser().parse(projectJSON).getAsJsonObject();

			//check if multiple inserts
			if(!projectJSON_parsed.has("projects")) {

				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! projectJSON_parsed.get("researcher_id").getAsString().equals(current_user_id)){
						result = new JsonObject();
						result.addProperty("STATUS", "PROHIBITED");
						result.addProperty("MESSAGE","You are NOT Allowed to add research projects on behalf of other researchers.");
						return result.toString();
					}
				}

				return (project.updateProject(projectJSON_parsed.get("project_id").getAsString(),projectJSON_parsed.get("researcher_id").getAsString(),projectJSON_parsed.get("project_name").getAsString(),projectJSON_parsed.get("project_description").getAsString(),projectJSON_parsed.get("category_id").getAsString(),projectJSON_parsed.get("project_start_date").getAsString(),projectJSON_parsed.get("project_end_date").getAsString(), projectJSON_parsed.get("expected_total_budget").getAsString())).toString();
			} else if (!projectJSON_parsed.get("projects").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = projectJSON_parsed.get("projects").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement projectElem : projectJSON_parsed.get("projects").getAsJsonArray()) {
				JsonObject projectObj = projectElem.getAsJsonObject();
				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! projectObj.get("researcher_id").getAsString().equals(current_user_id)){
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("id_mismatch", " You are not allowed to update other researchers' research projects.");
						errorsArr.add(errorElem);
						continue;
					}
				}
				JsonObject response = (project.updateProject(projectObj.get("project_id").getAsString(),projectObj.get("researcher_id").getAsString(),projectObj.get("project_name").getAsString(),projectObj.get("project_description").getAsString(),projectObj.get("category_id").getAsString(),projectObj.get("project_start_date").getAsString(),projectObj.get("project_end_date").getAsString(), projectObj.get("expected_total_budget").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
					updateCount++;
				} else {
					JsonObject errorElem = new JsonObject();
					errorElem.addProperty("exception", response.get("MESSAGE").getAsString());
					errorsArr.add(errorElem);
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", updateCount + " Projects were updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + updateCount +" Projects were Updated. Updating failed for "+ (elemCount-updateCount) + " Projects.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}



	@DELETE
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteProject(String projectJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		try {
			// Get Current User's ID
			String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

			// Authorize only ADMINs & RSCHRs
			if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR"))) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
				return result.toString();
			}

			JsonObject projectJSON_parsed = new JsonParser().parse(projectJSON).getAsJsonObject();

			//check if multiple inserts
			if(!projectJSON_parsed.has("projects")) {
				if(! (securityContext.isUserInRole("ADMIN"))) {					
					if(! projectJSON_parsed.get("researcher_id").getAsString().equals(current_user_id)) {
						result = new JsonObject();
						result.addProperty("STATUS", "PROHIBITED");
						result.addProperty("MESSAGE","You are NOT Allowed to delete research projects added by others");
						return result.toString();
					}

					return (project.deleteProject(current_user_id, projectJSON_parsed.get("project_id").getAsString())).toString();
				}				
				return (project.deleteProject(null, projectJSON_parsed.get("project_id").getAsString())).toString();

			} else if (!projectJSON_parsed.get("projects").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = projectJSON_parsed.get("projects").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement projectElem : projectJSON_parsed.get("projects").getAsJsonArray()) {
				JsonObject projectObj = projectElem.getAsJsonObject();
				JsonObject response = null;

				if(! (securityContext.isUserInRole("ADMIN"))) {
					if(! projectObj.get("researcher_id").getAsString().equals(current_user_id)) {
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("id_mismatch", " You are not allowed to delete research projects uploaded by others.");
						errorsArr.add(errorElem);
						continue;
					}

					response = (project.deleteProject(current_user_id, projectObj.get("project_id").getAsString()));				
				} else {
					response = (project.deleteProject(null, projectObj.get("project_id").getAsString()));
				}

				if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
					deleteCount++;
				} else {
					JsonObject errorElem = new JsonObject();
					errorElem.addProperty("exception", response.get("MESSAGE").getAsString());
					errorsArr.add(errorElem);
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", deleteCount + " Projects were deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + deleteCount +" Projects were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Projects.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	// Collaborators related End-points.
	@GET
	@Path("/{project_id}/collaborators")
	@Produces(MediaType.APPLICATION_JSON)
	public String readCollaborator(@PathParam("project_id") String project_id, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		// User Authorization
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR") || securityContext.isUserInRole("FUNDR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		return collaborator.readCollaborators(project_id).toString();
	}


	@POST
	@Path("/{project_id}/collaborators")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertCollaborator(@PathParam("project_id") String project_id, String collaboratorJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		// User Authorization
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if the given project id is valid for the currently logged in NON-ADMIN users
		if(securityContext.isUserInRole("RSCHR")) {
			if(!project.isOwner(current_user_id, project_id)) {
				result = new JsonObject();
				result.addProperty("STATUS", "PROHIBITED");
				result.addProperty("MESSAGE","You are not Allowed to add collaborators to non-owned projects.");
				return result.toString();
			}
		}
		
		try {

			JsonObject collaboratorJSON_parsed = new JsonParser().parse(collaboratorJSON).getAsJsonObject();

			//check if multiple inserts
			if(!collaboratorJSON_parsed.has("collaborators")) {

				return (collaborator.insertCollaborator(project_id, collaboratorJSON_parsed.get("full_name").getAsString(), collaboratorJSON_parsed.get("institution").getAsString())).toString();

			} else if (!collaboratorJSON_parsed.get("collaborators").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = collaboratorJSON_parsed.get("collaborators").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement collaboratorElem : collaboratorJSON_parsed.get("collaborators").getAsJsonArray()) {
				JsonObject collaboratorObj = collaboratorElem.getAsJsonObject();

				JsonObject response = (collaborator.insertCollaborator("project_id",collaboratorObj.get("full_name").getAsString(), collaboratorObj.get("institution").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
					insertCount++;
				} else {
					JsonObject errorElem = new JsonObject();
					errorElem.addProperty("exception", response.get("MESSAGE").getAsString());
					errorsArr.add(errorElem);
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", insertCount + " Collaborators were inserted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + insertCount +" Collaborators were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Collaborators.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	@PUT
	@Path("/{project_id}/collaborators")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateCollaborator(@PathParam("project_id") String project_id,String collaboratorJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		// User Authorization
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if the given project id is valid for the currently logged in NON-ADMIN users
		if(securityContext.isUserInRole("RSCHR")) {
			if(!project.isOwner(current_user_id, project_id)) {
				result = new JsonObject();
				result.addProperty("STATUS", "PROHIBITED");
				result.addProperty("MESSAGE","You are not Allowed to update collaborators to non-owned projects.");
				return result.toString();
			}
		}
		
		try {

			JsonObject collaboratorJSON_parsed = new JsonParser().parse(collaboratorJSON).getAsJsonObject();

			//check if multiple inserts
			if(!collaboratorJSON_parsed.has("collaborators")) {
				return (collaborator.updateCollaborator(project_id,collaboratorJSON_parsed.get("full_name").getAsString(),collaboratorJSON_parsed.get("institution").getAsString())).toString();
			} else if (!collaboratorJSON_parsed.get("collaborators").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = collaboratorJSON_parsed.get("collaborators").getAsJsonArray().size();

			for (JsonElement collaboratorElem : collaboratorJSON_parsed.get("collaborators").getAsJsonArray()) {
				JsonObject collaboratorObj = collaboratorElem.getAsJsonObject();
				
				JsonObject response = (collaborator.updateCollaborator("project_id",collaboratorObj.get("full_name").getAsString(), collaboratorObj.get("institution").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", insertCount + " Collaborators were updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + insertCount +" Collaborators were Updated. Updating failed for "+ (elemCount-insertCount) + " Collaborators.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}

	@DELETE
	@Path("/{project_id}/collaborators")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteCollaborator(@PathParam("project_id") String project_id,String collaboratorJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		// User Authorization
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if the given project id is valid for the currently logged in NON-ADMIN users
		if(securityContext.isUserInRole("RSCHR")) {
			if(!project.isOwner(current_user_id, project_id)) {
				result = new JsonObject();
				result.addProperty("STATUS", "PROHIBITED");
				result.addProperty("MESSAGE","You are not Allowed to delete collaborators to non-owned projects.");
				return result.toString();
			}
		}

		try {

			JsonObject collaboratorJSON_parsed = new JsonParser().parse(collaboratorJSON).getAsJsonObject();

			//check if multiple inserts
			if(!collaboratorJSON_parsed.has("collaborators")) {
				return (collaborator.deleteCollaborator(collaboratorJSON_parsed.get("project_id").getAsString(),collaboratorJSON_parsed.get("full_name").getAsString())).toString();
			} else if (!collaboratorJSON_parsed.get("collaborators").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = collaboratorJSON_parsed.get("collaborators").getAsJsonArray().size();

			for (JsonElement collaboratorElem : collaboratorJSON_parsed.get("collaborators").getAsJsonArray()) {
				JsonObject collaboratorObj = collaboratorElem.getAsJsonObject();
				JsonObject response = (collaborator.deleteCollaborator(collaboratorObj.get("project_id").getAsString(),collaboratorObj.get("full_name").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", insertCount + " Collaborators were deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + insertCount +" Collaborators were deleted. Deleting failed for "+ (elemCount-insertCount) + " Collaborators.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}


	// Categories related End-points.
	@GET
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public String readCategories(@Context SecurityContext securityContext)
	{
		//ADMINs, Researchers, funders
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("FUNDR") || securityContext.isUserInRole("RSCHR"))) {
			JsonObject result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		return category.readCategories().toString();
	}

	@POST
	@Path("/categories")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertCategory(String categoryJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		//Allow only UserType ADMIN
		if(!securityContext.isUserInRole("ADMIN")) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		try {

			JsonObject categoryJSON_parsed = new JsonParser().parse(categoryJSON).getAsJsonObject();

			//check if multiple inserts
			if(!categoryJSON_parsed.has("categories")) {
				return (category.insertCategory(categoryJSON_parsed.get("category_name").getAsString(), categoryJSON_parsed.get("category_description").getAsString(), current_user_id)).toString();
			} else if (!categoryJSON_parsed.get("categories").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = categoryJSON_parsed.get("categories").getAsJsonArray().size();

			for (JsonElement categoryElem : categoryJSON_parsed.get("categories").getAsJsonArray()) {
				JsonObject categoryObj = categoryElem.getAsJsonObject();
				JsonObject response = (category.insertCategory(categoryObj.get("category_name").getAsString(), categoryObj.get("category_description").getAsString(), current_user_id));

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
			result.addProperty("MESSAGE","Exception Details: " + ex);
			return result.toString();
		}
		return result.toString();
	}

	@PUT
	@Path("/categories")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updateCategory(String categoryJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		//Allow only UserType ADMIN
		if(!securityContext.isUserInRole("ADMIN")) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		try {

			JsonObject categoryJSON_parsed = new JsonParser().parse(categoryJSON).getAsJsonObject();

			//check if multiple inserts
			if(!categoryJSON_parsed.has("categories")) {
				return (category.updateCategory(categoryJSON_parsed.get("category_id").getAsString(), categoryJSON_parsed.get("category_name").getAsString(), categoryJSON_parsed.get("category_description").getAsString(), current_user_id)).toString();
			} else if (!categoryJSON_parsed.get("categories").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = categoryJSON_parsed.get("categories").getAsJsonArray().size();

			for (JsonElement categoryElem : categoryJSON_parsed.get("categories").getAsJsonArray()) {
				JsonObject categoryObj = categoryElem.getAsJsonObject();
				JsonObject response = (category.updateCategory(categoryObj.get("category_id").getAsString(), categoryObj.get("category_name").getAsString(), categoryObj.get("category_description").getAsString(), current_user_id));

				if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
					updateCount++;
				}
			}

			result = new JsonObject();
			if(updateCount == elemCount) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", updateCount + " Categories were updated successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + updateCount +" Categories were Updated. Updating failed for "+ (elemCount-updateCount) + " Categories.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE","Exception Details: " + ex);
			return result.toString();
		}

		return result.toString();
	}

	@DELETE
	@Path("/categories")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteCategory(String categoryJSON, @Context SecurityContext securityContext)
	{
		JsonObject result = null;

		//Allow only UserType ADMIN
		if(!securityContext.isUserInRole("ADMIN")) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		try {

			JsonObject categoryJSON_parsed = new JsonParser().parse(categoryJSON).getAsJsonObject();

			//check if multiple inserts
			if(!categoryJSON_parsed.has("categories")) {
				return (category.deleteCategory(categoryJSON_parsed.get("category_id").getAsString())).toString();
			} else if (!categoryJSON_parsed.get("categories").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "UNAUTHORIZED");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = categoryJSON_parsed.get("categories").getAsJsonArray().size();

			for (JsonElement categoryElem : categoryJSON_parsed.get("categories").getAsJsonArray()) {
				JsonObject categoryObj = categoryElem.getAsJsonObject();
				JsonObject response = (category.deleteCategory(categoryObj.get("category_id").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
					deleteCount++;
				}
			}

			result = new JsonObject();
			if(deleteCount == elemCount) {
				result.addProperty("STATUS", "SUCCESSFUL");
				result.addProperty("MESSAGE", deleteCount + " Categories were deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + deleteCount +" Categories were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Categories.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE","Exception Details: " + ex);
			return result.toString();
		}

		return result.toString();
	}

}
