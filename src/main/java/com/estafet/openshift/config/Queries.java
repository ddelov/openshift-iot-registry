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
}
