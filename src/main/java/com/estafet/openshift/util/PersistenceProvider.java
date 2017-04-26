package com.estafet.openshift.util;

import com.estafet.openshift.config.Constants;
import com.estafet.openshift.config.Queries;
import com.estafet.openshift.model.exception.EmptyArgumentException;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import static com.estafet.openshift.config.Constants.DATE_PATTERN;

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
		public static List<String> loadInitialStateFromTheDB(){
				log.debug(">> loadInitialStateFromTheDB()");
				Calendar today = Calendar.getInstance();
				today.set(Calendar.HOUR_OF_DAY, 0);
				today.set(Calendar.MINUTE, 0);
				today.set(Calendar.SECOND, 0);
				today.set(Calendar.MILLISECOND, 0);
				final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
				sdf.setTimeZone(today.getTimeZone());
				final String now = sdf.format(today.getTime());
				final List<String> activeDevices = new LinkedList<>();
				try(final Connection connection = DriverManager.getConnection(Constants.CONNECTION_URL, Constants.USERNAME, Constants.PASSWORD)){
						PreparedStatement ps = connection.prepareStatement(Queries.SQL_LOAD_ACTIVE_DEVICES);
						ps.setString(1, now);
						ps.setString(2, now);
						final ResultSet resultSet = ps.executeQuery();
						while (resultSet.next()) {
								final String thingName = resultSet.getString(1);
								activeDevices.add(thingName);
								log.debug("Added "+thingName + " to the devices map");
						}
				} catch (SQLException e) {
						log.error(e.getMessage());
				}finally {
						return activeDevices;
				}
		}
}