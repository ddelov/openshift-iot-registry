package com.estafet.openshift.rest.services;

import com.estafet.openshift.config.Constants;
import com.estafet.openshift.model.exception.EmptyArgumentException;
import com.estafet.openshift.util.PersistenceProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.estafet.openshift.config.Constants.EMPTY_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.http.util.TextUtils.isEmpty;

/**
 * Created by Delcho Delov on 19.04.17.
 *
 */
@Path("/")
public class RegistryService {
		private final Logger log = Logger.getLogger(RegistryService.class);

		//contains device_id and it latest reported state(if any)
		protected static final ConcurrentMap<String, String> devices = new ConcurrentHashMap<>();

		//================ for monitoring purposes ======================
		@GET
		@Produces(APPLICATION_JSON)
		public String hello() {
				return "Welcome to OpenShift, Mr. Delov!";
		}

		@GET
		@Path("/count")
		@Produces(APPLICATION_JSON)
		public Response count() {
				log.debug(">> RegistryService.count()");
				final int count = devices.size();
				String message = "At the moment there is no registered devices";
				if(count==1){
						message = "At the moment there is 1 registered device";
				}else if (count>1){
						message = "At the moment there are "+count+" registered devices";
				}
				return Response.status(HttpServletResponse.SC_OK).entity(message).build();
		}

		@GET
		@Path("/state/{device_id}")
		@Produces(APPLICATION_JSON)
		public Response getState(@PathParam("device_id")String deviceId) {
				log.debug(">> RegistryService.getState()");
				final String reported = devices.get(deviceId);
				if(isEmpty(reported)){
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Device not found").build();
				}
				return Response.status(HttpServletResponse.SC_OK).entity(reported).build();
		}

		//================ for device manager ===========================
		@PUT
		@Path("/register/{device_id}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response register(@PathParam("device_id")String deviceId) {
				log.debug(">> RegistryService.register("+deviceId+ ")");
				final String prevState = devices.putIfAbsent(deviceId, EMPTY_JSON);
				if(prevState!=null){
						return Response.status(HttpServletResponse.SC_OK).entity("Device is registered already").build();
				}
				return Response.status(HttpServletResponse.SC_OK).entity("Device registered").build();
		}

		@DELETE
		@Path("/delete/{device_id}")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response delete(@PathParam("device_id")String deviceId) {
				log.debug(">> RegistryService.delete("+deviceId+ ")");
				final String remove = devices.remove(deviceId);
				if(isEmpty(remove)){
						return Response.status(HttpServletResponse.SC_OK).entity("Device not found").build();
				}
				return Response.status(HttpServletResponse.SC_OK).entity("Device unregistered").build();
		}

		//================ for simulator interaction ====================
		@POST
		@Path("/send")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response send(String jsonState) {
				log.debug(">> RegistryService.send("+ jsonState +")");
				Gson gson = new GsonBuilder().create();
				Map<String, Object> reportedState = gson.fromJson(jsonState, Map.class);
				String thingName = (String) reportedState.get(Constants.COL_THING_NAME);
				final String prevState = devices.replace(thingName, jsonState);
				if(prevState==null){
						log.warn("Device not found");
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Device not found").build();
				}
				Double pressure = (Double) reportedState.get(Constants.COL_PRESSURE);
				log.debug("pressure "+pressure);
				if (pressure > Constants.MAX_PRESSURE) {
						long tstamp = Calendar.getInstance().getTimeInMillis();
						final PersistenceProvider dao = getPersistenceProvider();
						try (Connection conn = dao.getCon()) {
								try {
										dao.writeLeaksData(thingName, pressure, tstamp, conn);
										conn.commit();
								} catch (EmptyArgumentException e) {
										conn.rollback();
										log.error(e.getMessage(), e);
										return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
								}
						} catch (SQLException e) {
								log.error(e.getMessage(), e);
								return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Could not open DB connection").build();
						}
				}
				return Response.status(HttpServletResponse.SC_OK).build();
		}

		//for test/mock purposes only
		protected PersistenceProvider getPersistenceProvider() {
				return new PersistenceProvider();
		}
}