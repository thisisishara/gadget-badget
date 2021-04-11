package com.gadgetbadget.researchhub;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.gadgetbadget.researchhub.model.Category;

@Path("/research-projects")
public class ResearchprojectService {
	Category category = new Category();
	
	 // Roles related End-points.
		@GET
		@Path("/categories")
		@Produces(MediaType.APPLICATION_JSON)
		public String readCategory()
		{
			return category.readCategory().toString();
		}


		
}
