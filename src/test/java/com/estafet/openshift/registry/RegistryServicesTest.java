package com.estafet.openshift.registry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.Response;

import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Delcho Delov on 20.04.17.
 */
@RunWith(MockitoJUnitRunner.class)
public class RegistryServicesTest {
		final String TEST_PAYLOAD = "{\n" +
						"\"customer_id\":\"Asan@ibm.bg\",\n" +
						"\"thing_name\":\"MD1\",\n" +
						"\"thing_type\":\"митническа декларация\",\n" +
						"\"sn\":\"BG23-445-ДДС\",\n" +
						"\"own\":true,\n" +
						"\"valid_from\":\"20170311\"\n" +
						"}";
		final String TEST_DEVICE = "Podvodnica";
		final String TEST_ENDPOINT = "http://device-manager-device-manager.192.168.42.182.nip.io/registerDevice";

		@InjectMocks
		private RegistryServices handler;

		@Before
		public void setUp() throws Exception {
				MockitoAnnotations.initMocks(this);
		}

		@Test
		public void getListeners() throws Exception {
		}

		@Test
		public void send() throws Exception {
				final Response response1 = handler.send(TEST_DEVICE, TEST_PAYLOAD);
				assertThat(response1.getStatus(), is(SC_OK));
				System.out.println("handler = " + handler);
				final Response response = handler.registerRule(TEST_DEVICE, TEST_ENDPOINT);
				final Response response2 = handler.send(TEST_DEVICE, TEST_PAYLOAD);
				assertThat(response2.getStatus(), is(SC_OK));
		}

		@Test
		public void registerRule() throws Exception {
		}

		@Test
		public void deleteRule() throws Exception {
		}

}