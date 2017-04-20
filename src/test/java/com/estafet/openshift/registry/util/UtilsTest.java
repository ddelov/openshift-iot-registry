package com.estafet.openshift.registry.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Delcho Delov on 13.04.17.
 *
 */
public class UtilsTest {
		@Test
		public void isEmpty() throws Exception {
				assertTrue(Utils.isEmpty(null));
				assertTrue(Utils.isEmpty(""));
				assertTrue(Utils.isEmpty(" "));
				assertFalse(Utils.isEmpty("888"));
		}

		@Test
		public void areEquals() {
				double Pi = 3.141592;
				double[] DBL = {3.141592, 22.0 / 7.0, -3.141592, 3.14, 3.1515};
				assertTrue(Utils.areEquals(Pi, DBL[0], 0.009));
				assertTrue(Utils.areEquals(Pi, DBL[1], 0.009));
				assertFalse(Utils.areEquals(Pi, DBL[2], 0.009));
				assertTrue(Utils.areEquals(Pi, DBL[3], 0.009));
				assertFalse(Utils.areEquals(Pi, DBL[4], 0.009));
		}
}