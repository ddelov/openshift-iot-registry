package com.estafet.openshift.dm.rest;

import com.estafet.openshift.dm.model.exception.DMException;
import com.estafet.openshift.dm.model.entity.DeviceOwnership;
import com.estafet.openshift.dm.model.exception.EmptyArgumentException;
import com.estafet.openshift.dm.model.exception.ResourceNotFoundException;
import com.estafet.openshift.dm.util.PersistenceProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.estafet.openshift.dm.config.Constants.*;
import static com.estafet.openshift.dm.config.Queries.SQL_GET_ALL_DEV_OWNERSHIP;
import static com.estafet.openshift.dm.config.Queries.SQL_GET_DEV_OWNERSHIP_BY_CUSTOMER;
import static com.estafet.openshift.dm.util.Utils.isEmpty;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

/**
 * Created by Delcho Delov on 06.04.17.
 *
 */
@Path("/")
public class DeviceManagerServices {
		private final Logger log = Logger.getLogger(DeviceManagerServices.class);

		@GET
		@Path("/")
		@Produces(APPLICATION_JSON)
		public String hello() {
				return "Welcome to AWS IoT bench project migration on OpenShift!";
		}


		@GET
		@Path("/getAllDevices")
		@Produces(APPLICATION_JSON)
		public Response getAllDevices(@HeaderParam(HDR_CUSTOMER_ID) String customerId,
																	@HeaderParam(ROLE) String role) {
				log.debug(">> DeviceManagerServices.getAllDevices()");
				//check parameters
				try {
						if (isEmpty(role)) {
								log.error(ROLE + " parameter is mandatory");
								throw new EmptyArgumentException(ROLE + " parameter is mandatory");
						}
						if (isEmpty(customerId) && !ROLE_MANAGER.equalsIgnoreCase(role)) {
								log.error("As you have not MANAGER permissions granted, so you should specify your customer ID");
								throw new EmptyArgumentException("As you have not MANAGER permissions granted, so you should specify your customer ID");
						}
				} catch (EmptyArgumentException e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
				}
				//processing
				List<Map<String, Object>> allDevices;
				try {
						if (ROLE_MANAGER.equalsIgnoreCase(role)) {
								allDevices = listMyDevices(null);
						} else {
								allDevices = listMyDevices(customerId);
						}
				} catch (DMException e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
				}
				log.debug("<< DeviceManagerServices.getAllDevices()");
				// return HTTP response 200 in case of success
				return Response.status(HttpServletResponse.SC_OK).entity(allDevices).build();
		}


		@DELETE
		@Path("/deleteDevice/{thing_name}")
		@Produces(MediaType.APPLICATION_JSON)
		public Response deleteDevice(@PathParam(HDR_THING_NAME) String thingName) {
				log.debug(">> DeviceManagerServices.deleteDevice()");
				//check parameters
				try {
						if (isEmpty(thingName)) {
								log.error(HDR_THING_NAME + " parameter is mandatory");
								throw new EmptyArgumentException(HDR_THING_NAME + " parameter is mandatory");
						}
				} catch (EmptyArgumentException e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
				}
				//invalidate device ownership record
				final PersistenceProvider dao = getPersistenceProvider();
				try (Connection conn = dao.getCon()) {
						try {
								//1. search device current record in DeviceOwnership and mark as invalid
								final DeviceOwnership loadDeviceOwnership = dao.loadDeviceOwnership(thingName, conn);
								loadDeviceOwnership.setValidTo(Calendar.getInstance()); // effective immediately - today device is no longer active
								dao.markDeviceOwnershipInvalid(loadDeviceOwnership, conn);
								conn.commit();
						} catch (ResourceNotFoundException e) {
								log.info(e.getMessage());
								return Response.status(HttpServletResponse.SC_OK).entity(e.getMessage()).build();
						} catch (DMException e) {
								log.error(e.getMessage(), e);
								return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
						}
				} catch (SQLException e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("Could not open DB connection").build();
				}
				log.debug("<< DeviceManagerServices.deleteDevice()");
				// return HTTP response 200 in case of success
				return Response.status(HttpServletResponse.SC_OK).entity("Device deleted").build();
		}

		PersistenceProvider getPersistenceProvider() {
				return new PersistenceProvider();
		}


//		@POST
//		@Path("/registerDevice")
//		@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//		@Produces(MediaType.APPLICATION_JSON)
//		public Response registerDevice(MultivaluedMap<String, String> body) {
//				log.debug(">> DeviceManagerServices.registerDevice()");
//				//1. extract input parameters
//				try {
//						//1. extract input parameters
//						if (body == null || body.isEmpty()) {
//								throw new EmptyArgumentException("Missing request body");
//						}
//						log.info("body: "+body);
//						if (!body.containsKey(COL_CUST_ID)) {
//								log.error(COL_CUST_ID + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_CUST_ID + " parameter is mandatory");
//						}
//						final String customerId = body.getFirst(COL_CUST_ID);
//						if (isEmpty(customerId)) {
//								log.error(COL_THING_NAME + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_THING_NAME + " parameter is mandatory");
//						}
//
//						final String thingName = body.getFirst(COL_THING_NAME);
//						if (isEmpty(thingName)) {
//								log.error(COL_THING_NAME + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_THING_NAME + " parameter is mandatory");
//						}
//						final String thingType = body.getFirst(COL_THING_TYPE);
//						if (isEmpty(thingType)) {
//								log.error(COL_THING_TYPE + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_THING_TYPE + " parameter is mandatory");
//						}
//						final String sn = body.getFirst(COL_SN);
//						if (isEmpty(sn)) {
//								log.error(COL_SN + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_SN + " parameter is mandatory");
//						}
//						if (!body.containsKey(COL_OWN)) {
//								log.error(COL_OWN + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_OWN + " parameter is mandatory");
//						}
//						final boolean own = Boolean.getBoolean(body.getFirst(COL_OWN));
//						final String validFrom = body.getFirst(COL_VALID_FROM);
//						if (isEmpty(validFrom)) {
//								log.error(COL_VALID_FROM + " parameter is mandatory");
//								throw new EmptyArgumentException(COL_VALID_FROM + " parameter is mandatory");
//						}
//						//2.processing
//						final DeviceOwnership deviceOwnership = new DeviceOwnership(customerId, thingName, thingType, sn, own);
//						deviceOwnership.setValidFrom(validFrom);
//						registerDevice(deviceOwnership);
//				} catch (DMException e) {
//						log.error(e.getMessage(), e);
//						return Response.status(HttpServletResponse.SC_BAD_REQUEST).build();
//				}
//
//				log.debug("<< DeviceManagerServices.registerDevice()");
//				// return HTTP response 200 in case of success
//				return Response.status(HttpServletResponse.SC_OK).entity("Device registered").build();
//		}

		@POST
		@Path("/registerDevice")
		@Consumes(MediaType.APPLICATION_JSON)
		@Produces(MediaType.APPLICATION_JSON)
		public Response registerDevice(String jsonPayload) {
				log.debug(">> DeviceManagerServices.registerDevice()");
				Gson gson = new GsonBuilder().create();
				try {
						//1. extract input parameters
						final Map<String, Object> body = gson.fromJson(jsonPayload, Map.class);
						if (body == null || body.isEmpty()) {
								throw new EmptyArgumentException("Missing request body");
						}
						log.info("body: " + body);
						final String customerId = (String) body.get(COL_CUST_ID);
						if (isEmpty(customerId)) {
								log.error(COL_CUST_ID + " parameter is mandatory");
								throw new EmptyArgumentException(COL_CUST_ID + " parameter is mandatory");
						}
						final String thingName = (String) body.get(COL_THING_NAME);
						if (isEmpty(thingName)) {
								log.error(COL_THING_NAME + " parameter is mandatory");
								throw new EmptyArgumentException(COL_THING_NAME + " parameter is mandatory");
						}
						final String thingType = (String) body.get(COL_THING_TYPE);
						// NOTE: thing type is not checked - any non-empty value is accepted
						if (isEmpty(thingType)) {
								log.error(COL_THING_TYPE + " parameter is mandatory");
								throw new EmptyArgumentException(COL_THING_TYPE + " parameter is mandatory");
						}
						final String sn = (String) body.get(COL_SN);
						if (isEmpty(sn)) {
								log.error(COL_SN + " parameter is mandatory");
								throw new EmptyArgumentException(COL_SN + " parameter is mandatory");
						}
						if (!body.containsKey(COL_OWN)) {
								log.error(COL_OWN + " parameter is mandatory");
								throw new EmptyArgumentException(COL_OWN + " parameter is mandatory");
						}
						final boolean own = (boolean) body.get(COL_OWN);
						final String validFrom = (String) body.get(COL_VALID_FROM);
						if (isEmpty(validFrom)) {
								log.error(COL_VALID_FROM + " parameter is mandatory");
								throw new EmptyArgumentException(COL_VALID_FROM + " parameter is mandatory");
						}
						//2.processing
						final PersistenceProvider dao = getPersistenceProvider();
						try (Connection conn = dao.getCon()) {
								// 1. search device current record (if any) in DeviceOwnership and mark as invalid
								try {
										final DeviceOwnership loadDeviceOwnership = dao.loadDeviceOwnership(thingName, conn);
										loadDeviceOwnership.setValidTo(Calendar.getInstance()); // effective immediately - today device is no longer active
										dao.markDeviceOwnershipInvalid(loadDeviceOwnership, conn);
								} catch (ResourceNotFoundException e) {
										// not a problem - continue
								}
								// 2. insert a new record - valid from today
								DeviceOwnership actualRecord = new DeviceOwnership(customerId, thingName, thingType, sn, own, validFrom);
								try {
										dao.writeDeviceOwnership(actualRecord, conn);
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
				} catch (DMException e) {
						log.error(e.getMessage(), e);
						return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity(e.getMessage()).build();
				}

				log.info("Exit DeviceManager.addNewDevice() method");
				// return HTTP response 200 in case of success
				return Response.status(HttpServletResponse.SC_OK).entity("Device registered").build();
		}

		private List<Map<String, Object>> listMyDevices(String customerId) throws DMException {
				final List<Map<String, Object>> devices = new LinkedList<>();
				final PersistenceProvider dao = getPersistenceProvider();
				String sql = SQL_GET_ALL_DEV_OWNERSHIP;
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 0);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND, 0);
				today.set(Calendar.MILLISECOND, 0);
				final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
				sdf.setTimeZone(today.getTimeZone());
				final String now = sdf.format(today.getTime());

				try (Connection conn = dao.getCon()) {
						PreparedStatement ps = conn.prepareStatement(SQL_GET_ALL_DEV_OWNERSHIP);
						ps.setString(1, now);
						ps.setString(2, now);
						if (!isEmpty(customerId)) {
								ps = conn.prepareStatement(SQL_GET_DEV_OWNERSHIP_BY_CUSTOMER);
								ps.setString(3, customerId);
								sql = SQL_GET_DEV_OWNERSHIP_BY_CUSTOMER;
						}

						log.debug(sql);
						final ResultSet resultSet = ps.executeQuery();
						while (resultSet.next()) {
								final int id = Double.valueOf(resultSet.getDouble(1)).intValue();
								final String thingName = resultSet.getString(2);
								final String thingTypeName = resultSet.getString(3);
								final String sn = resultSet.getString(4);
								boolean own = resultSet.getBoolean(5);
								final String validFrom = resultSet.getString(6);
								final String validTo = resultSet.getString(7);
								final String customerIdRead = resultSet.getString(8);
								final DeviceOwnership deviceOwnership = new DeviceOwnership(id, customerIdRead, thingName, thingTypeName, sn, own, validFrom, validTo);
								log.debug("Found info for " + deviceOwnership);
								final Map<String, Object> propertiesMap = deviceOwnership.asMap();
								//TODO get real device status from registry
								propertiesMap.put("deviceStatus", "OF");
								devices.add(propertiesMap);
						}
				} catch (SQLException e) {
						throw new DMException("Could not open DB connection", e);
				}
				return devices;
		}

}
