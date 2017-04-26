package com.estafet.openshift.config;

/**
 * Created by Delcho Delov on 21.04.17.
 *
 */
public interface Constants {
		double MAX_PRESSURE = 35.0;//simulator generates between 1 and 51
		String DATE_PATTERN = "yyyyMMdd";

		String SCHEMA_NAME = "openshift";
		//DB tables
		String TABLE_NAME_DEVICE_OWNERSHIP = "dev_ownership";//
		String TABLE_NAME_LEAKS_DATA = "leaks_data";

		String DRIVER = "org.postgresql.Driver";
		String CONNECTION_URL = "jdbc:postgresql://" + System.getenv("DB_HOST") + ":5432/" + SCHEMA_NAME;
		String USERNAME = "debil4o";
		String PASSWORD = "debil4o";

		//DB columns
		String COL_ID = "id";
		String COL_THING_NAME = "thing_name";
		String COL_CUST_ID = "customer_id";
		String COL_VALID_FROM = "valid_from";
		String COL_VALID_TO = "valid_to";
		String COL_OWN = "own";
		String COL_THING_TYPE = "thing_type";
		String COL_SN = "sn";
		String COL_PRESSURE = "pressure";//double
		String COL_TSTAMP = "tstamp";//long

		String EMPTY_JSON = "{}";

		String THING_NAME = "thingName";
}
