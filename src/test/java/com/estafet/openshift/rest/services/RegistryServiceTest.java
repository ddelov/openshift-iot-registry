package com.estafet.openshift.rest.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static com.estafet.openshift.config.Constants.COL_PRESSURE;
import static com.estafet.openshift.config.Constants.THING_NAME;
import static com.estafet.openshift.util.TestConstants.TEST_DEVICE_ID;
import static com.estafet.openshift.util.TestConstants.TEST_PRESSURE_LOW;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Delcho Delov on 24.04.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegistryServiceTest {

		@InjectMocks
		private RegistryService service;
		@Before
		public void setUp() throws Exception {
				MockitoAnnotations.initMocks(this);
		}

		@Test
		public void count() throws Exception {
		}

		@Test
		public void getState() throws Exception {
		}

		@Test
		public void register() throws Exception {
		}

		@Test
		public void delete() throws Exception {
		}

		@Ignore
		@Test
		public void send() throws Exception {
				final Response registerResponse = service.register(TEST_DEVICE_ID);
				assertThat(registerResponse.getStatus(), is(SC_OK));

				final Map<String, Object> payloadMap = new HashMap<>(2);
				payloadMap.put(THING_NAME, TEST_DEVICE_ID);
				payloadMap.put(COL_PRESSURE, TEST_PRESSURE_LOW);
				Gson gson = new GsonBuilder().create();

				final Response sendResponse = service.send(gson.toJson(payloadMap));
				assertThat(sendResponse.getStatus(), is(SC_OK));
		}

}