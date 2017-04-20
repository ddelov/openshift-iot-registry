package com.estafet.openshift.rest.services;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/")
public class HelloResource {

		@GET
		@Path("/hello")
		@Produces(APPLICATION_JSON)
		public String ping(){

				return "Hello World!";
		}

}
