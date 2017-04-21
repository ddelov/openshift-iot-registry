package com.estafet.openshift.util;

import com.estafet.openshift.config.Constants;
import com.estafet.openshift.config.Queries;
import com.estafet.openshift.model.exception.EmptyArgumentException;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Delcho Delov on 04.04.17.
 *
 */
public class PersistenceProvider {
		private Connection con = null;
		private static final Logger log = Logger.getLogger(PersistenceProvider.class);

		static {
				try {
						log.debug("PersistenceProvider static initializer");
						Class.forName(Constants.DRIVER);
						log.debug("============ DB Connection URL: " + Constants.CONNECTION_URL);
				} catch (Exception e) {
						log.error(e.getMessage(), e);
				}
		}

		public Connection getCon() throws SQLException {
				if (con == null || con.isClosed()) {
						// try to open a new one
						con = DriverManager.getConnection(Constants.CONNECTION_URL, Constants.USERNAME, Constants.PASSWORD);
						log.debug("Connection established");
						con.setAutoCommit(false);
				}
				if (con == null || con.isClosed()) {
						throw new SQLException("Could not open DB connection");
				}
				log.debug("returns connection: " + con);
				return con;
		}

		/**
		 * Persists leaks data
		 * Should be wrapped in transaction
		 *
		 * @param thingName device name
		 * @param pressure	pressure
		 * @param tstamp		timestamp
		 * @param conn			connection
		 * @throws SQLException
		 * @throws EmptyArgumentException
		 */
		public void writeLeaksData(String thingName, Double pressure, long tstamp, Connection conn) throws SQLException, EmptyArgumentException {
				log.debug(">> writeLeaksData()");
				if (conn == null || conn.isClosed()) {
						throw new EmptyArgumentException("connection is mandatory parameter");
				}
				if (Utils.isEmpty(thingName) || pressure==null) {
						throw new EmptyArgumentException("All parameters are mandatory");
				}
				PreparedStatement ps = conn.prepareStatement(Queries.SQL_INSERT_LEAKS_DATA);
				ps.setString(1, thingName);
				ps.setDouble(2, pressure);
				ps.setLong(3, tstamp);

				final int i = ps.executeUpdate();
				log.debug("Affected rows: " + i);
				log.debug("<< writeLeaksData()");
		}

}