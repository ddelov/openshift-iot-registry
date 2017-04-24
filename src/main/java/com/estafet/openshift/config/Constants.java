package com.estafet.openshift.config;

/**
 * Created by Delcho Delov on 21.04.17.
 *
 */
public interface Constants {
		double MAX_PRESSURE = 55.0;

		String SCHEMA_NAME = "openshift";
		String TABLE_NAME_LEAKS_DATA = "leaks_data";

		String DRIVER = "org.postgresql.Driver";
		String CONNECTION_URL = "jdbc:postgresql://" + System.getenv("DB_HOST") + ":5432/" + SCHEMA_NAME;
		String USERNAME = "debil4o";
		String PASSWORD = "debil4o";

		String COL_ID = "id";//int
		String COL_THING_NAME = "thing_name";//string
		String COL_PRESSURE = "pressure";//double
		String COL_TSTAMP = "tstamp";//long
		String EMPTY_JSON = "{}";
}
