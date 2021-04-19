package com.gadgetbadget.marketplace;

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

import com.gadgetbadget.marketplace.model.Product;
import com.gadgetbadget.marketplace.model.Product_Category;
import com.gadgetbadget.marketplace.util.InterServiceCommHandler;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/products")
public class ProductService {
	Product_Category productCategory = new Product_Category();
	Product product = new Product();
	
	//Product related end points
	//get method
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String readProducts(@Context SecurityContext securityContext, @QueryParam("researcherid") String researcher_id, @QueryParam("summarized") boolean isSummarized, @QueryParam("filtered") boolean isFiltered) {
		JsonObject result = null;

		// Authorize only ADMINs,Consumers, Researchers and User service
		if(! (securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("USR") || securityContext.isUserInRole("RSCHR") || securityContext.isUserInRole("CNSMR") || securityContext.isUserInRole("FNMGR"))) {
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
					return product.readProductSummeryByResearcherId(researcher_id).toString();
				}

				result = new JsonObject();
				result.addProperty("STATUS", "PROHIBITED");
				result.addProperty("MESSAGE","You are NOT Allowed to retrieve summarized product details.");
				return result.toString();

			}

			if(securityContext.isUserInRole("RSCHR") && isFiltered) {
				return product.readProducts(current_user_id).toString();
			}

			return product.readProducts(null).toString();

		} catch(Exception ex) {
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex);
			return result.toString();
		}
	}
	
	
	@GET
	@Path("/{product_id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String readProduct(@Context SecurityContext securityContext, @PathParam("product_id") String product_id, @QueryParam("filtered") boolean isFiltered) {

		JsonObject result = null;

		// Authorize only ADMINs, FUNDRs
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("RSCHR") || securityContext.isUserInRole("FNMGR") || securityContext.isUserInRole("CNSMR"))) {
			result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		// Get Current User's ID
		String current_user_id = securityContext.getUserPrincipal().getName().split(";")[1];

		// Check if its a Single ID or Multiple IDs
		if(!product_id.contains(",")) {
			// Allow retrieving only if the IDs are matched for NON ADMINs
			if(securityContext.isUserInRole("RSCHR") && isFiltered) {
				return product.readProduct(current_user_id, product_id).toString();
			}

			return product.readProduct(null, product_id).toString();
		}

		//if multiple id s
		String[] ids = product_id.split(",");

		int readCount = 0;
		int elemCount = ids.length;
		JsonArray resultArray = new JsonArray();

		for (String id : ids) {
			JsonObject response = null;
			if(securityContext.isUserInRole("RSCHR") && isFiltered) {
				response = product.readProduct(current_user_id, id);
			} else {
				response = product.readProduct(null, id);
			}

			if (!response.has("MESSAGE")) {
				readCount++;
				resultArray.add(response);
			} 
		}

		result = new JsonObject();
		result.add("products", resultArray);

		if(readCount == elemCount) {
			result.addProperty("STATUS", "SUCCESSFUL");
			result.addProperty("MESSAGE", readCount + " Products were retrieved successfully.");

		} else {
			result.addProperty("STATUS", "UNSUCCESSFUL");
			result.addProperty("MESSAGE", "Only " + readCount +" Products were retrieved. Retrieving failed for "+ (elemCount-readCount) + " Products.");
		}

		return result.toString();
	}
	
	//Inter-service communication is used here with payment service
	@GET
	@Path("/{product_id}/payments")
	@Produces(MediaType.APPLICATION_JSON)
	public String readProductFunds(@Context SecurityContext securityContext, @PathParam("product_id") String product_id) {

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
			if(!product.isOwner(current_user_id, product_id)) {
				result = new JsonObject();
				result.addProperty("STATUS", "PROHIBITED");
				result.addProperty("MESSAGE","You are not Allowed to view list of funds received by other researchers' projects.");
				return result.toString();
			}
		}

		return (new InterServiceCommHandler().paymentIntercomms("payments?productid=" + product_id)).toString();
	}
	
	//insert method
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertProduct(String productJSON, @Context SecurityContext securityContext)
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

			JsonObject productJSON_parsed = new JsonParser().parse(productJSON).getAsJsonObject();

			if(!productJSON_parsed.has("products")) {
				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! productJSON_parsed.get("researcher_id").getAsString().equals(current_user_id)){
						result = new JsonObject();
						result.addProperty("STATUS", "PROHIBITED");
						result.addProperty("MESSAGE","You are NOT Allowed to place products on behalf of other researchers.");
						return result.toString();
					}
				}

				return (product.insertProduct( productJSON_parsed.get("researcher_id").getAsString(), productJSON_parsed.get("product_name").getAsString(), productJSON_parsed.get("product_description").getAsString(), productJSON_parsed.get("category_id").getAsString(), productJSON_parsed.get("available_items").getAsInt(), productJSON_parsed.get("price").getAsFloat()).toString());

			} else if (!productJSON_parsed.get("products").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = productJSON_parsed.get("products").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement productElem : productJSON_parsed.get("products").getAsJsonArray()) {
				JsonObject productObj = productElem.getAsJsonObject();

				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! productObj.get("researcher_id").getAsString().equals(current_user_id)){
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("id_mismatch", "Your user id does not match with the researcher id given in the payload. You are not allowed to add products on behalf of other researchers.");
						errorsArr.add(errorElem);
						continue;
					}
				}

				JsonObject response = product.insertProduct( productObj.get("researcher_id").getAsString(), productObj.get("product_name").getAsString(), productObj.get("product_description").getAsString(), productObj.get("category_id").getAsString(), productObj.get("available_items").getAsInt(), productObj.get("price").getAsFloat());

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
				result.addProperty("MESSAGE", insertCount + " Products were added successfully.");

			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + insertCount +" Products were added. failed to add "+ (elemCount-insertCount) + " Products.");
				result.add("insertion_errors", errorsArr);
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex);
		}

		return result.toString();
	}
	
	
	//update method
	@PUT
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String updatePayment(String productJSON, @Context SecurityContext securityContext)
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

			JsonObject productJSON_parsed = new JsonParser().parse(productJSON).getAsJsonObject();

			if(!productJSON_parsed.has("products")) {
				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! productJSON_parsed.get("researcher_id").getAsString().equals(current_user_id)){
						result = new JsonObject();
						result.addProperty("STATUS", "PROHIBITED");
						result.addProperty("MESSAGE","You are NOT Allowed to update products on behalf of other researchers.");
						return result.toString();
					}
				}

				return (product.updateProduct(productJSON_parsed.get("product_id").getAsString(), productJSON_parsed.get("researcher_id").getAsString(), productJSON_parsed.get("product_name").getAsString(), productJSON_parsed.get("product_description").getAsString(), productJSON_parsed.get("category_id").getAsString(), productJSON_parsed.get("available_items").getAsInt(), productJSON_parsed.get("price").getAsFloat()).toString()); 

			} else if (!productJSON_parsed.get("products").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int updateCount = 0;
			int elemCount = productJSON_parsed.get("products").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement productElem : productJSON_parsed.get("products").getAsJsonArray()) {
				JsonObject productObj = productElem.getAsJsonObject();

				if(! (securityContext.isUserInRole("ADMIN"))) {
					if (! productObj.get("researcher_id").getAsString().equals(current_user_id)){
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("id_mismatch", " You are not allowed to update products on behalf of other researchers.");
						errorsArr.add(errorElem);
						continue;
					}
				}

				JsonObject response = (product.updateProduct(productObj.get("product_id").getAsString(), productObj.get("researcher_id").getAsString(), productObj.get("product_name").getAsString(), productObj.get("product_description").getAsString(), productObj.get("category_id").getAsString(), productObj.get("available_items").getAsInt(), productObj.get("price").getAsFloat()));

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
				result.addProperty("MESSAGE", updateCount + " Products were updated successfully.");

			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + updateCount +" Products were Updated. Updating failed for "+ (elemCount-updateCount) + " Products.");
				result.add("updating_errors", errorsArr);
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}
	
	//delete method
	@DELETE
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deletePayment(String productJSON, @Context SecurityContext securityContext)
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

			JsonObject productJSON_parsed = new JsonParser().parse(productJSON).getAsJsonObject();

			if(!productJSON_parsed.has("products")) {
				if(! (securityContext.isUserInRole("ADMIN"))) {					
					if(! productJSON_parsed.get("researcher_id").getAsString().equals(current_user_id)) {
						result = new JsonObject();
						result.addProperty("STATUS", "PROHIBITED");
						result.addProperty("MESSAGE","You are NOT Allowed to delete products uploaded by others");
						return result.toString();
					}

					return (product.deleteProduct(current_user_id, productJSON_parsed.get("product_id").getAsString())).toString();
				}				
				return (product.deleteProduct(null, productJSON_parsed.get("product_id").getAsString())).toString();

			} else if (!productJSON_parsed.get("products").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", "ERROR");
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int deleteCount = 0;
			int elemCount = productJSON_parsed.get("products").getAsJsonArray().size();

			JsonArray errorsArr = new JsonArray();

			for (JsonElement productElem : productJSON_parsed.get("products").getAsJsonArray()) {
				JsonObject productObj = productElem.getAsJsonObject();
				JsonObject response = null;

				if(! (securityContext.isUserInRole("ADMIN"))) {
					if(! productObj.get("funder_id").getAsString().equals(current_user_id)) {
						JsonObject errorElem = new JsonObject();
						errorElem.addProperty("id_mismatch", " You are not allowed to delete products uploaded by others.");
						errorsArr.add(errorElem);
						continue;
					}

					response = (product.deleteProduct(current_user_id, productObj.get("product_id").getAsString()));				
				} else {
					response = (product.deleteProduct(null, productObj.get("product_id").getAsString()));
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
				result.addProperty("MESSAGE", deleteCount + " Products were deleted successfully.");
			} else {
				result.addProperty("STATUS", "UNSUCCESSFUL");
				result.addProperty("MESSAGE", "Only " + deleteCount +" Products were deleted. Deleting failed for "+ (elemCount-deleteCount) + " Products.");
				result.add("updating_errors", errorsArr);
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", "EXCEPTION");
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}
	
	
	//Product Category related HTTP methods
	//get method
	@GET
	@Path("/categories")
	@Produces(MediaType.APPLICATION_JSON)
	public String readCategories(@Context SecurityContext securityContext)
	{
		//ADMINs, Researchers, consumers
		if(!(securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("CNSMR") || securityContext.isUserInRole("RSCHR"))) {
			JsonObject result = new JsonObject();
			result.addProperty("STATUS", "UNAUTHORIZED");
			result.addProperty("MESSAGE","You are not Authorized to access this End-point!");
			return result.toString();
		}

		return productCategory.readCategories().toString();
	}
		
	//insert method
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
				return (productCategory.insertCategory(categoryJSON_parsed.get("category_name").getAsString(), categoryJSON_parsed.get("category_description").getAsString(), current_user_id)).toString();
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
				JsonObject response = (productCategory.insertCategory(categoryObj.get("category_name").getAsString(), categoryObj.get("category_description").getAsString(), current_user_id));

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
		
	//update method
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
				return (productCategory.updateCategory(categoryJSON_parsed.get("category_id").getAsString(), categoryJSON_parsed.get("category_name").getAsString(), categoryJSON_parsed.get("category_description").getAsString(), current_user_id)).toString();
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
				JsonObject response = (productCategory.updateCategory(categoryObj.get("category_id").getAsString(), categoryObj.get("category_name").getAsString(), categoryObj.get("category_description").getAsString(), current_user_id));

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
		
	//delete method
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
				return (productCategory.deleteCategory(categoryJSON_parsed.get("category_id").getAsString())).toString();
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
				JsonObject response = (productCategory.deleteCategory(categoryObj.get("category_id").getAsString()));

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
