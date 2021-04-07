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
import com.gadgetbadget.user.model.Role;
import com.gadgetbadget.user.model.User;
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
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String hello() {
		return "users is up and running.";
	}
	
	@POST
	@Path("/employee")
	@Produces(MediaType.APPLICATION_JSON)
	public String insertEmployee() {
		return employee.insertEmployee();
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

