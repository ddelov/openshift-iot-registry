package com.estafet.openshift.registry.rest;

import com.estafet.openshift.registry.model.Rule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Delcho Delov on 19.04.17.
 */
@Path("/")
public class RegistryServices{
		private final Logger log = Logger.getLogger(RegistryServices.class);

		private ConcurrentMap<String, List<Rule>> deviceTopics = new ConcurrentHashMap<>();

		@GET
		@Path("/")
		@Produces(APPLICATION_JSON)
		public String hello() {
				return "Welcome to IoT registry!";
		}

		@GET
		@Path("/getListeners/{device_id}")
		@Produces(APPLICATION_JSON)
		public Response getListeners(@PathParam("device_id")String deviceId) {
				log.debug(">> RegistryServices.getListeners("+deviceId+")");
				final List<Rule> rules = deviceTopics.get(deviceId);
				return Response.status(HttpServletResponse.SC_OK).entity(rules).build();
		}

		@POST
		@Path("/registerDevice/{device_id}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response registerDevice(@PathParam("device_id")String deviceId) {
				log.debug(">> RegistryServices.registerDevice("+deviceId+")");
				deviceTopics.putIfAbsent(deviceId, new LinkedList<Rule>());
				return Response.status(HttpServletResponse.SC_OK).build();
		}

		//================ for simulator interaction ====================
		@POST
		@Path("/sendState/{device_id}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response sendState(@PathParam("device_id")String deviceId, String jsonState) {
				log.debug(">> RegistryServices.sendState("+deviceId+ ", "+ jsonState +")");
				sendStatePrv(deviceId, jsonState);
				return Response.status(HttpServletResponse.SC_OK).build();
		}

		private void sendStatePrv(String deviceId, String jsonState){
				registerDevice(deviceId);//harmless method
				final List<Rule> topicListeners = deviceTopics.get(deviceId);
				for (Rule rule : topicListeners) {
						log.debug("updating Rule "+rule);
						rule.update(jsonState);
				}
		}

		// for rules/listeners interaction

		@POST
		@Path("/registerRule/{device_id}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response registerRule(@PathParam("device_id")String deviceId, String jsonRule) {
				log.debug(">> RegistryServices.registerRule("+deviceId+ ", "+ jsonRule +")");
				Gson gson = new GsonBuilder().create();
				final Rule rule = gson.fromJson(jsonRule, Rule.class);
				registerRule(deviceId, rule);
				return Response.status(HttpServletResponse.SC_OK).build();
		}
		public void registerRule(String deviceId, Rule rule) {
				registerDevice(deviceId);
				deviceTopics.get(deviceId).add(rule);
		}

		@DELETE
		@Path("/deleteRule/{thing_name}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteRule(@PathParam("device_id")String deviceId, String jsonRule) {
				log.debug(">> RegistryServices.deleteRule("+deviceId+ ", "+ jsonRule +")");
				Gson gson = new GsonBuilder().create();
				final Rule rule = gson.fromJson(jsonRule, Rule.class);
				deleteRule(deviceId, rule);
				return Response.status(HttpServletResponse.SC_OK).build();
		}
		private void deleteRule(String deviceId, Rule rule) {
				final List<Rule> rules = deviceTopics.get(deviceId);
				final Iterator<Rule> iterator = rules.iterator();
				while(iterator.hasNext()){
						final Rule next = iterator.next();
						if(next.equals(rule)){
								iterator.remove();
								return;
						}
				}
		}
}