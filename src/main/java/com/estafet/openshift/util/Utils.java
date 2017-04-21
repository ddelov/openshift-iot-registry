package com.estafet.openshift.util;

import org.apache.log4j.Logger;

/**
 * Created by Delcho Delov on 06.04.17.
 *
 */
class Utils {
		private static final Logger log = Logger.getLogger(Utils.class);

		private Utils() {
		}

		public static boolean isEmpty(String val) {
				return val == null || val.trim().isEmpty();
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
