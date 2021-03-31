package com.gadgetbadget.user;

//For REST Service
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

//For JSON
import com.google.gson.*;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

//For XML
import org.jsoup.*;
import org.jsoup.parser.*;
import org.jsoup.nodes.Document;

@Path("/Employee")
public class EmployeeService {
	
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_PLAIN)
	public String readItems()
	{
		return "Employee Service is up and running.\nNothing to see here yet.";
	}
	
	
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
	}
}
