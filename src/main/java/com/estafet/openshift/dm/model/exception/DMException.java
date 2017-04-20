package com.estafet.openshift.dm.model.exception;

/**
 * Created by Delcho Delov on 12.1.2017 г..
 *
 */
public class DMException extends Exception {
		public DMException(String message, Exception e) {
				super(message, e);
		}

		public DMException(String s) {
				super(s);
		}

}
