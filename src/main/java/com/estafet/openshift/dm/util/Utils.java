package com.estafet.openshift.dm.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Delcho Delov on 06.04.17.
 *
 */
public class Utils {
		private static final Logger log = Logger.getLogger(Utils.class);

		private Utils() {
		}

		public static boolean isEmpty(String val) {
				return val == null || val.trim().isEmpty();
		}

		public static boolean areEquals(double a, double b, double delta) {
				if (a == b) {
						return true;
				}
				if (a > b) {
						return a - b < delta;
				}
				return b - a < delta;
		}
		public static int makePostJsonRequest(String url, String jsonString) throws IOException {
				log.debug("makePostJsonRequest to ("+url);
				CloseableHttpClient httpClient = HttpClients.createDefault();
				HttpPost httpPost = new HttpPost(url);
				StringEntity inputMappings = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
				httpPost.setEntity(inputMappings);
				httpPost.setHeader("Content-type", "application/json");
				log.debug("payload(JSON): "+ jsonString);
				CloseableHttpResponse response = httpClient.execute(httpPost);
				BufferedReader rd = new BufferedReader(new InputStreamReader(
								response.getEntity().getContent()));
				String line = rd.readLine();
				log.debug("response line" + line);
				return response.getStatusLine().getStatusCode();
		}

//		public static void main(String[] args) throws IOException {
//				final String url = "http://device-manager-device-manager.192.168.42.182.nip.io/registerDevice";
//				final String payload = "{" +
//								"\"customer_id\":\"Asan@ibm.bg\"," +
//								"\"thing_name\":\"Pod1\"," +
//								"\"thing_type\":\"подводница\"," +
//								"\"sn\":\"BG23-445\"," +
//								"\"own\":true," +
//								"\"valid_from\":\"20170311\"" +
//								"}";
//				final int responseCode = makePostJsonRequest(url, payload);
//				System.out.println("responseCode = " + responseCode);
//		}

}
