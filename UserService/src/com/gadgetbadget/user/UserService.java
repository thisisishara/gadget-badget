package com.gadgetbadget.user;	

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gadgetbadget.user.model.Consumer;
import com.gadgetbadget.user.model.Employee;
import com.gadgetbadget.user.model.Funder;
import com.gadgetbadget.user.model.PaymentInfo;
import com.gadgetbadget.user.model.Researcher;
import com.gadgetbadget.user.model.User;
import com.gadgetbadget.user.util.DBOpStatus;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Path("/users")
public class UserService {	
	User user = new User();
	Employee employee = new Employee();
	Researcher researcher = new Researcher();
	Funder funder = new Funder();
	Consumer consumer = new Consumer();
	PaymentInfo paymentInfo = new PaymentInfo();


	@GET
	@Path("/employees")
	@Produces(MediaType.APPLICATION_JSON)
	public String readEmployees() {
		return employee.readEmployees().toString();
	}

	@POST
	@Path("/employees")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String insertEmployee(String roleJSON)
	{
		JsonObject result = null;

		try {

			JsonObject roleJSON_parsed = new JsonParser().parse(roleJSON).getAsJsonObject();

			//check if multiple inserts
			if(!roleJSON_parsed.has("employees")) {
				return (employee.insertEmployee(roleJSON_parsed.get("username").getAsString(), roleJSON_parsed.get("password").getAsString(), roleJSON_parsed.get("role_id").getAsString(), roleJSON_parsed.get("first_name").getAsString(), roleJSON_parsed.get("last_name").getAsString(), roleJSON_parsed.get("gender").getAsString(), roleJSON_parsed.get("primary_email").getAsString(), roleJSON_parsed.get("primary_phone").getAsString(), roleJSON_parsed.get("gb_employee_id").getAsString(), roleJSON_parsed.get("department").getAsString(), roleJSON_parsed.get("date_hired").getAsString())).toString();
			} else if (!roleJSON_parsed.get("employees").isJsonArray()) {
				result = new JsonObject();
				result.addProperty("STATUS", DBOpStatus.ERROR.toString());
				result.addProperty("MESSAGE","Invalid JSON Object.");
				return result.toString();
			}

			int insertCount = 0;
			int elemCount = roleJSON_parsed.get("employees").getAsJsonArray().size();

			for (JsonElement roleElem : roleJSON_parsed.get("employees").getAsJsonArray()) {
				JsonObject roleObj = roleElem.getAsJsonObject();
				JsonObject response = (employee.insertEmployee(roleObj.get("username").getAsString(), roleObj.get("password").getAsString(), roleObj.get("role_id").getAsString(), roleObj.get("first_name").getAsString(), roleObj.get("last_name").getAsString(), roleObj.get("gender").getAsString(), roleObj.get("primary_email").getAsString(), roleObj.get("primary_phone").getAsString(), roleObj.get("gb_employee_id").getAsString(), roleObj.get("department").getAsString(), roleObj.get("date_hired").getAsString()));

				if (response.get("STATUS").getAsString().equalsIgnoreCase(DBOpStatus.SUCCESSFULL.toString())) {
					insertCount++;
				}
			}

			result = new JsonObject();
			if(insertCount == elemCount) {
				result.addProperty("STATUS", DBOpStatus.SUCCESSFULL.toString());
				result.addProperty("MESSAGE", insertCount + " Employees were inserted successfully.");
			} else {
				result.addProperty("STATUS", DBOpStatus.UNSUCCESSFUL.toString());
				result.addProperty("MESSAGE", "Only " + insertCount +" Employees were Inserted. Inserting failed for "+ (elemCount-insertCount) + " Employees.");
			}

		} catch (Exception ex){
			result = new JsonObject();
			result.addProperty("STATUS", DBOpStatus.EXCEPTION.toString());
			result.addProperty("MESSAGE", "Exception Details: " + ex.getMessage());
		}

		return result.toString();
	}





	/*
	//Testing Inter-service communications with Payment Service
	@GET
	@Path("/Intercomms/")
	@Produces(MediaType.TEXT_PLAIN)
	public String intercomms()
	{
		Client c = Client.create();
		WebResource resource = c.resource("http://127.0.0.1:8080/PaymentService/PaymentService/Test/");
		String output = resource.get(String.class);
		return "Response of Payment Server: " + output;
	}*/
}

