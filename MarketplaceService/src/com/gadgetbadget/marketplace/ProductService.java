package com.gadgetbadget.marketplace;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gadgetbudget.marketplace.model.Product;
import com.gadgetbudget.marketplace.model.Product_Category;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/marketplace")
public class ProductService {
	Product_Category productCategory = new Product_Category();
	Product product = new Product();
	
		//Product-Category related end points
		//get method
		@GET
		@Path("/product-categories")
		@Produces(MediaType.APPLICATION_JSON)
		public String readAllProductCategory() {
			return productCategory.readAllProductCategory().toString();
		}
	
		//insert method
		@POST
		@Path("/product-categories")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String insertProductCategory(String productCategoryJSON)
		{
			JsonObject result = null;
	
			try {
	
				JsonObject productCategoryJSON_parsed = new JsonParser().parse(productCategoryJSON).getAsJsonObject();
	
				//check if multiple inserts
				if(!productCategoryJSON_parsed.has("product-categories")) {
					return (productCategory.insertProductCategory(productCategoryJSON_parsed.get("category_name").getAsString(),productCategoryJSON_parsed.get("category_description").getAsString(), productCategoryJSON_parsed.get("last_modified_by").getAsString())).toString();
				} else if (!productCategoryJSON_parsed.get("product-categories").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}
	
				int insertCount = 0;
				int elemCount = productCategoryJSON_parsed.get("product-categories").getAsJsonArray().size();
	
				for (JsonElement productCategoryElem : productCategoryJSON_parsed.get("product-categories").getAsJsonArray()) {
						JsonObject productCategoryObj = productCategoryElem.getAsJsonObject();
						JsonObject response = (productCategory.insertProductCategory(productCategoryObj.get("category_name").getAsString(),productCategoryObj.get("category_description").getAsString(), productCategoryObj.get("last_modified_by").getAsString()));
	
						if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
							insertCount++;
						}
				}
	
				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Product categories were inserted successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" Product categories were Inserted. Inserting failed for "+ (elemCount-insertCount) + "Product categories.");
				}
	
			} catch (Exception e){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + e.getMessage());
			}
	
			return result.toString();
		}
	
	
		//update method
		@PUT
		@Path("/product-categories")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String updateProductCategory(String productCategoryJSON)
		{
			JsonObject result = null;
	
			try {
	
				JsonObject productCategoryJSON_parsed = new JsonParser().parse(productCategoryJSON).getAsJsonObject();
	
				//check if multiple inserts
				if(!productCategoryJSON_parsed.has("product-categories")) {
					return (productCategory.updateProductCategory(productCategoryJSON_parsed.get("category_id").getAsString(),
							productCategoryJSON_parsed.get("category_name").getAsString(),
							productCategoryJSON_parsed.get("category_description").getAsString(),
							productCategoryJSON_parsed.get("last_modified_by").getAsString())).toString();
				
				} else if (!productCategoryJSON_parsed.get("product-categories").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}
	
				int insertCount = 0;
				int elemCount = productCategoryJSON_parsed.get("product-categories").getAsJsonArray().size();
	
				for (JsonElement productCategoryElem : productCategoryJSON_parsed.get("product-categories").getAsJsonArray()) {
					JsonObject productCategoryObj = productCategoryElem.getAsJsonObject();
					JsonObject response = (productCategory.updateProductCategory(productCategoryObj.get("category_id").getAsString(),
							productCategoryObj.get("category_name").getAsString(),
							productCategoryObj.get("category_description").getAsString(),
							productCategoryObj.get("last_modified_by").getAsString()));
	
					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					}
				}
	
				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Product Categories were updated successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" product Categories were Updated. Updating failed for "+ (elemCount-insertCount) + " product Categories.");
				}
	
			} catch (Exception e){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + e.getMessage());
			}
	
			return result.toString();
		}
	
		//delete method
		@DELETE
		@Path("/product-categories")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String deleteProductCategory(String productCategoryJSON)
		{
			JsonObject result = null;
	
			try {
	
				JsonObject productCategoryJSON_parsed = new JsonParser().parse(productCategoryJSON).getAsJsonObject();
	
				//check if multiple inserts
				if(!productCategoryJSON_parsed.has("product-categories")) {
					return (productCategory.deleteProductCategory(productCategoryJSON_parsed.get("category_id").getAsString())).toString();
				
				} else if (!productCategoryJSON_parsed.get("product-categories").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}
	
				int insertCount = 0;
				int elemCount = productCategoryJSON_parsed.get("product-categories").getAsJsonArray().size();
	
				for (JsonElement productCategoryElem : productCategoryJSON_parsed.get("product-categories").getAsJsonArray()) {
					JsonObject productCategoryObj = productCategoryElem.getAsJsonObject();
					JsonObject response = (productCategory.deleteProductCategory(productCategoryObj.get("category_id").getAsString()));
	
					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					}
				}
	
				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS","SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " product categories were deleted successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" product categories were deleted. Deleting failed for "+ (elemCount-insertCount) + "product categories.");
				}
	
			} catch (Exception e){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + e.getMessage());
			}
	
			return result.toString();
		}
	
	
		//Product related end points
		//get method
		@GET
		@Path("/products")
		@Produces(MediaType.APPLICATION_JSON)
		public String readAllProducts() {
			return product.readAllProducts().toString();
		}
		
		//insert method
		@POST
		@Path("/products")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String insertProduct(String productCategoryJSON)
		{
			JsonObject result = null;
	
			try {
	
				JsonObject productCategoryJSON_parsed = new JsonParser().parse(productCategoryJSON).getAsJsonObject();
	
				//check if multiple inserts
				if(!productCategoryJSON_parsed.has("products")) {
					return (product.insertProduct(productCategoryJSON_parsed.get("researcher_id").getAsString(),productCategoryJSON_parsed.get("product_name").getAsString(),
									productCategoryJSON_parsed.get("product_description").getAsString(),productCategoryJSON_parsed.get("category_id").getAsString(),
									productCategoryJSON_parsed.get("available_items").getAsInt(),productCategoryJSON_parsed.get("price").getAsDouble())).toString();
				} else if (!productCategoryJSON_parsed.get("products").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}
	
				int insertCount = 0;
				int elemCount = productCategoryJSON_parsed.get("products").getAsJsonArray().size();
	
				for (JsonElement productCategoryElem : productCategoryJSON_parsed.get("products").getAsJsonArray()) {
						JsonObject productCategoryObj = productCategoryElem.getAsJsonObject();
						JsonObject response = (product.insertProduct(productCategoryObj.get("researcher_id").getAsString(),productCategoryObj.get("product_name").getAsString(),
								productCategoryObj.get("product_description").getAsString(),productCategoryObj.get("category_id").getAsString(),
								productCategoryObj.get("available_items").getAsInt(),productCategoryObj.get("price").getAsDouble()));
	
						if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
							insertCount++;
						}
				}
	
				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Products were inserted successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" Products were Inserted. Inserting failed for "+ (elemCount-insertCount) + "Products.");
				}
	
			} catch (Exception e){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + e.getMessage());
			}
	
			return result.toString();
		}
		
		//update method
		@PUT
		@Path("/products")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String updateProduct(String productCategoryJSON)
		{
			JsonObject result = null;
	
			try {
	
				JsonObject productCategoryJSON_parsed = new JsonParser().parse(productCategoryJSON).getAsJsonObject();
	
				//check if multiple inserts
				if(!productCategoryJSON_parsed.has("products")) {
					return (product.updateProduct(productCategoryJSON_parsed.get("product_id").getAsString(),
							productCategoryJSON_parsed.get("researcher_id").getAsString(),
							productCategoryJSON_parsed.get("product_name").getAsString(),
							productCategoryJSON_parsed.get("product_description").getAsString(),
							productCategoryJSON_parsed.get("category_id").getAsString(),
							productCategoryJSON_parsed.get("available_items").getAsInt(),
							productCategoryJSON_parsed.get("price").getAsDouble())).toString();
				
				} else if (!productCategoryJSON_parsed.get("products").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}
	
				int insertCount = 0;
				int elemCount = productCategoryJSON_parsed.get("products").getAsJsonArray().size();
	
				for (JsonElement productCategoryElem : productCategoryJSON_parsed.get("products").getAsJsonArray()) {
					JsonObject productCategoryObj = productCategoryElem.getAsJsonObject();
					JsonObject response = (product.updateProduct(productCategoryObj.get("product_id").getAsString(),
							productCategoryObj.get("researcher_id").getAsString(),
							productCategoryObj.get("product_name").getAsString(),
							productCategoryObj.get("product_description").getAsString(),
							productCategoryObj.get("category_id").getAsString(),
							productCategoryObj.get("available_items").getAsInt(),
							productCategoryObj.get("price").getAsDouble()));
	
					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					}
				}
	
				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS", "SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " Products were updated successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" products were Updated. Updating failed for "+ (elemCount-insertCount) + " products.");
				}
	
			} catch (Exception e){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + e.getMessage());
			}
	
			return result.toString();
		}
		
		//delete method
		@DELETE
		@Path("/products")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public String deleteProduct(String productCategoryJSON)
		{
			JsonObject result = null;
	
			try {
	
				JsonObject productCategoryJSON_parsed = new JsonParser().parse(productCategoryJSON).getAsJsonObject();
	
				//check if multiple inserts
				if(!productCategoryJSON_parsed.has("products")) {
					return (product.deleteProduct(productCategoryJSON_parsed.get("product_id").getAsString())).toString();
				
				} else if (!productCategoryJSON_parsed.get("products").isJsonArray()) {
					result = new JsonObject();
					result.addProperty("STATUS", "ERROR");
					result.addProperty("MESSAGE","Invalid JSON Object.");
					return result.toString();
				}
	
				int insertCount = 0;
				int elemCount = productCategoryJSON_parsed.get("products").getAsJsonArray().size();
	
				for (JsonElement productCategoryElem : productCategoryJSON_parsed.get("products").getAsJsonArray()) {
					JsonObject productCategoryObj = productCategoryElem.getAsJsonObject();
					JsonObject response = (product.deleteProduct(productCategoryObj.get("product_id").getAsString()));
	
					if (response.get("STATUS").getAsString().equalsIgnoreCase("SUCCESSFUL")) {
						insertCount++;
					}
				}
	
				result = new JsonObject();
				if(insertCount == elemCount) {
					result.addProperty("STATUS","SUCCESSFUL");
					result.addProperty("MESSAGE", insertCount + " products were deleted successfully.");
				} else {
					result.addProperty("STATUS", "UNSUCCESSFUL");
					result.addProperty("MESSAGE", "Only " + insertCount +" products were deleted. Deleting failed for "+ (elemCount-insertCount) + "products.");
				}
	
			} catch (Exception e){
				result = new JsonObject();
				result.addProperty("STATUS", "EXCEPTION");
				result.addProperty("MESSAGE", "Exception Details: " + e.getMessage());
			}
	
			return result.toString();
		}
				
}
