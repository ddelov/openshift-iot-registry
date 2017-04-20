package com.estafet.openshift.dm.config;

/**
 * Created by Delcho Delov on 06.04.17.
 *
 */
public interface Queries {
		//SQL statements
		String SQL_LOAD_LAST_ACTIVE_OWNERSHIP = "select " + Constants.COL_ID + "," + Constants.COL_CUST_ID + "," +
						Constants.COL_THING_TYPE + "," + Constants.COL_SN + "," + Constants.COL_OWN + "," + Constants.COL_VALID_FROM + "," + Constants.COL_VALID_TO +
						" from " + Constants.SCHEMA_NAME + '.' + Constants.TABLE_NAME_DEVICE_OWNERSHIP +
						" where " + Constants.COL_THING_NAME + " like ? and (" +
						//all next parameters are today(formatted)
						" (" + Constants.COL_VALID_FROM + " <= ? and " + Constants.COL_VALID_TO + " is NULL )" +
						" or (" + Constants.COL_VALID_TO + " > ? )" +
						")";

		String SQL_INSERT_DEV_OWNERSHIP = "insert into " + Constants.SCHEMA_NAME + '.' + Constants.TABLE_NAME_DEVICE_OWNERSHIP +
						" (" + Constants.COL_CUST_ID + "," + Constants.COL_THING_TYPE + "," + Constants.COL_SN + "," + Constants.COL_OWN + "," + Constants.COL_VALID_FROM + "," + Constants.COL_VALID_TO + "," + Constants.COL_THING_NAME +
						" ) values (?,?,?,?,?,?,?)";
		String SQL_MARK_DEV_OWNERSHIP_INVALID = "update " + Constants.SCHEMA_NAME + '.' + Constants.TABLE_NAME_DEVICE_OWNERSHIP +
						" set " + Constants.COL_VALID_TO + " = ? where " + Constants.COL_ID + " = ?";
		String SQL_GET_ALL_DEV_OWNERSHIP = "select " + Constants.COL_ID + ", " + Constants.COL_THING_NAME + "," + Constants.COL_THING_TYPE + "," + Constants.COL_SN + "," + Constants.COL_OWN + "," + Constants.COL_VALID_FROM + "," + Constants.COL_VALID_TO + "," + Constants.COL_CUST_ID +
						" from " + Constants.SCHEMA_NAME + '.' + Constants.TABLE_NAME_DEVICE_OWNERSHIP +
						" where (" +
						//all next parameters are today(formatted)
						" (" + Constants.COL_VALID_FROM + " <= ? and " + Constants.COL_VALID_TO + " is NULL )" +
						" or (" + Constants.COL_VALID_TO + " > ? )" +
						" )";
		String SQL_GET_DEV_OWNERSHIP_BY_CUSTOMER = "select " + Constants.COL_ID + ", " + Constants.COL_THING_NAME + "," + Constants.COL_THING_TYPE + "," + Constants.COL_SN + "," + Constants.COL_OWN + "," + Constants.COL_VALID_FROM + "," + Constants.COL_VALID_TO + "," + Constants.COL_CUST_ID +
						" from " + Constants.SCHEMA_NAME + '.' + Constants.TABLE_NAME_DEVICE_OWNERSHIP +
						" where (" +
						//all next parameters are today(formatted)
						" (" + Constants.COL_VALID_FROM + " <= ? and " + Constants.COL_VALID_TO + " is NULL )" +
						" or (" + Constants.COL_VALID_TO + " > ? )" +
						" ) and " + Constants.COL_CUST_ID + " like ?";

}
