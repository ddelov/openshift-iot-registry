package com.estafet.openshift.registry.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@ApplicationPath("/")
public class RestConfig extends Application {

		@GET
		@Path("/")
		@Produces(APPLICATION_JSON)
		public String hello() {
				System.out.println("RestConfig hello method called");
				return "Welcome to IoT registry!";
		}

}
