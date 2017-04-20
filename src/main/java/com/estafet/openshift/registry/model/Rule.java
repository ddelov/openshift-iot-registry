package com.estafet.openshift.registry.model;

/**
 * Created by Delcho Delov on 19.04.17.
 */
public interface Rule{
		void update(String payload);
		String getEndpoint();
}
