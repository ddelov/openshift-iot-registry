package com.estafet.openshift.rest.services;

import com.estafet.openshift.model.exception.EmptyArgumentException;
import com.estafet.openshift.util.PersistenceProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static com.estafet.openshift.config.Constants.COL_PRESSURE;
import static com.estafet.openshift.config.Constants.EMPTY_JSON;
import static com.estafet.openshift.config.Constants.THING_NAME;
import static com.estafet.openshift.util.TestConstants.*;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Delcho Delov on 24.04.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegistryServiceTest {

		@Mock
		private PersistenceProvider persistenceProviderMock = mock(PersistenceProvider.class);

		private class RegistryServiceMock extends RegistryService{
				@Override
				protected PersistenceProvider getPersistenceProvider() {
						return persistenceProviderMock;
				}

				ConcurrentMap<String, String> getDevicesMap(){
						return devices;
				}
		}
		private RegistryServiceMock service = new RegistryServiceMock();

		@Before
		public void setUp() throws Exception {
				MockitoAnnotations.initMocks(this);
				service.getDevicesMap().clear();
		}

		@Test
		public void countZero() throws Exception {
				final Response countRes = service.count();
				assertThat(countRes.getStatus(), is(SC_OK));
				final String message = (String) countRes.getEntity();
				assertThat(message, is("At the moment there is no registered devices"));
		}
		@Test
		public void countOne() throws Exception {
				service.getDevicesMap().put(TEST_DEVICE_ID, getPayload());
				final Response countRes = service.count();
				assertThat(countRes.getStatus(), is(SC_OK));
				final String message = (String) countRes.getEntity();
				assertThat(message, is("At the moment there is 1 registered device"));
		}
		@Test
		public void countMany() throws Exception {
				service.getDevicesMap().put(TEST_DEVICE_ID, getPayload());
				service.getDevicesMap().put(TEST_DEVICE_ID+1, getPayload());
				service.getDevicesMap().put(TEST_DEVICE_ID+2, getPayload());
				final Response countRes = service.count();
				assertThat(countRes.getStatus(), is(SC_OK));
				final String message = (String) countRes.getEntity();
				assertThat(message, is("At the moment there are "+3+" registered devices"));
		}

		@Test
		public void getStateNotFound() throws Exception {
				final Response stateRes = service.getState(TEST_DEVICE_ID);
				assertThat(stateRes.getStatus(), is(SC_BAD_REQUEST));
				final String message = (String) stateRes.getEntity();
				assertThat(message, is("Device not found"));
		}
		@Test
		public void getStateOK() throws Exception {
				service.getDevicesMap().put(TEST_DEVICE_ID, getPayload());
				final Response stateRes = service.getState(TEST_DEVICE_ID);
				assertThat(stateRes.getStatus(), is(SC_OK));
				final String message = (String) stateRes.getEntity();
				assertThat(message, is(getPayload()));
		}

		@Test
		public void registerDuplicate() throws Exception {
				service.getDevicesMap().put(TEST_DEVICE_ID, getPayload());
				final Response response = service.register(TEST_DEVICE_ID);
				assertThat(response.getStatus(), is(SC_OK));
				final String message = (String) response.getEntity();
				assertThat(message, is("Device is registered already"));
		}
		@Test
		public void registerOK() throws Exception {
				final Response response = service.register(TEST_DEVICE_ID);
				assertThat(response.getStatus(), is(SC_OK));
				final String message = (String) response.getEntity();
				assertThat(message, is("Device registered"));
		}

		@Test
		public void deleteNotFound() throws Exception {
				final Response response = service.delete(TEST_DEVICE_ID);
				assertThat(response.getStatus(), is(SC_OK));
				final String message = (String) response.getEntity();
				assertThat(message, is("Device not found"));
		}
		@Test
		public void deleteOK() throws Exception {
				service.getDevicesMap().put(TEST_DEVICE_ID, getPayload());
				final Response response = service.delete(TEST_DEVICE_ID);
				assertThat(response.getStatus(), is(SC_OK));
				final String message = (String) response.getEntity();
				assertThat(message, is("Device unregistered"));
		}

		@Test
		public void sendDevNotFound() throws Exception {
				final Response sendResponse = service.send(getPayload());
				assertThat(sendResponse.getStatus(), is(SC_BAD_REQUEST));
				final String message = (String) sendResponse.getEntity();
				assertThat(message, is("Device not found"));
		}
		@Test
		public void sendDevPressureLow() throws Exception {
				service.getDevicesMap().put(TEST_DEVICE_ID, EMPTY_JSON);
				final Response sendResponse = service.send(getPayload());
				assertThat(sendResponse.getStatus(), is(SC_OK));
		}
		@Test
		public void sendCouldNotConnect() throws Exception {
				//mocks
				service.getDevicesMap().put(TEST_DEVICE_ID, EMPTY_JSON);
				when(persistenceProviderMock.getCon()).thenThrow(SQLException.class);
				//call method
				final Response sendResponse = service.send(getPayload4Db());
				assertThat(sendResponse.getStatus(), is(SC_BAD_REQUEST));
				final String message = (String) sendResponse.getEntity();
				assertThat(message, is("Could not open DB connection"));
		}
		@Test
		public void sendEmptyArgEx() throws Exception {
				//mocks
				service.getDevicesMap().put(TEST_DEVICE_ID, EMPTY_JSON);
				Connection connectionMock=mock(Connection.class);
				EmptyArgumentException exceptionMock = mock(EmptyArgumentException.class);
				when(persistenceProviderMock.getCon()).thenReturn(connectionMock);
				when(exceptionMock.getMessage()).thenReturn(TEST_TYPE);
				doThrow(exceptionMock).when(persistenceProviderMock).writeLeaksData(
								anyString(), anyDouble(), anyLong(), any(Connection.class));
				//call method
				final Response sendResponse = service.send(getPayload4Db());
				assertThat(sendResponse.getStatus(), is(SC_BAD_REQUEST));
				final String message = (String) sendResponse.getEntity();
				assertThat(message, is(TEST_TYPE));
		}

		private String getPayload(){
				final Map<String, Object> payloadMap = new HashMap<>(2);
				payloadMap.put(THING_NAME, TEST_DEVICE_ID);
				payloadMap.put(COL_PRESSURE, TEST_PRESSURE_LOW);
				Gson gson = new GsonBuilder().create();
				return gson.toJson(payloadMap);
		}
		private String getPayload4Db(){
				final Map<String, Object> payloadMap = new HashMap<>(2);
				payloadMap.put(THING_NAME, TEST_DEVICE_ID);
				payloadMap.put(COL_PRESSURE, TEST_PRESSURE_HI);
				Gson gson = new GsonBuilder().create();
				return gson.toJson(payloadMap);
		}

}