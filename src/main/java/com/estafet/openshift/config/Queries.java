package com.estafet.openshift.config;

/**
 * Created by Delcho Delov on 06.04.17.
 *
 */
public interface Queries {
		//SQL statements
		String SQL_INSERT_LEAKS_DATA = "insert into " + Constants.SCHEMA_NAME + '.' + Constants.TABLE_NAME_LEAKS_DATA +
						" (" + Constants.COL_THING_NAME + "," + Constants.COL_PRESSURE + "," + Constants.COL_TSTAMP +
						" ) values (?,?,?)";
		String SQL_LOAD_ACTIVE_DEVICES = "select " + Constants.COL_THING_NAME +
						" from " + Constants.SCHEMA_NAME + '.' + Constants.TABLE_NAME_DEVICE_OWNERSHIP +
						" where (" +
						//all next parameters are today(formatted)
						" (" + Constants.COL_VALID_FROM + " <= ? and " + Constants.COL_VALID_TO + " is NULL )" +
						" or (" + Constants.COL_VALID_TO + " > ? )" +
						" )";

}
