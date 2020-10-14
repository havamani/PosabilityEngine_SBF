package com.fss.pos.base.api.db;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import com.fss.pos.base.api.db.datasource.JdbcDatasource;
import com.fss.pos.base.api.db.datasource.JndiDataSource;
import com.fss.pos.base.commons.Config;
import com.fss.pos.base.commons.Log;
import com.fss.pos.base.commons.PosException;
import com.fss.pos.base.commons.StaticStore;
import com.fss.pos.base.commons.constants.Constants;
import com.fss.pos.base.commons.utils.Util;
import com.zaxxer.hikari.HikariDataSource;

public abstract class AbstractDatabaseApi implements DatabaseApi {

	@Autowired
	private JdbcDatasource jdbcDatasource;

	@Autowired
	private JndiDataSource jndiDataSource;

	@Autowired
	private Config config;

	private static Map<String, HikariDataSource> jdbcMap = new ConcurrentHashMap<String, HikariDataSource>();
	private static Map<String, DataSource> jndiMap = new ConcurrentHashMap<String, DataSource>();

	@Override
	public Connection getConnection(String instId) throws PosException {
		if (Constants.MASTER_INSTID.equals(instId))
			return getMasterConnection(instId);
		else
			return getChildConnection(instId);
	}

	private Connection getMasterConnection(String instId) throws PosException {
		try {
			if (Constants.DB_CONNECTION_TYPE_JNDI.equals(config
					.getMasterDbConnectionType())) {
				DataSource ds = jndiDataSource.getDataSource(config
						.getMasterJndi());
				return ds.getConnection();
			} else if (Constants.DB_CONNECTION_TYPE_JDBC.equals(config
					.getMasterDbConnectionType())) {
				Class.forName(getDriver());
				return DriverManager.getConnection(config.getDbUrl(),
						config.getDbName(), config.getDbCode());
			} else
				throw new Exception("Invalid Connection type");
		} catch (Exception e) {
			Log.info("Master database connection failed due to ",
					e.getMessage());
			Log.error("Master database connection failed due to ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	private Connection getChildConnection(String instId) throws PosException {
		try {
			if (!StaticStore.schemaMap.containsKey(instId))
				throw new PosException(Constants.ERR_INVALID_MSP);
			DatabaseSchema schema = StaticStore.schemaMap.get(instId);
			if (Constants.DB_CONNECTION_TYPE_JNDI.equals(schema
					.getConectionType())) {
				if (!jndiMap.containsKey(instId))
					registerJndiPool(schema);
				return jndiMap.get(instId).getConnection();
			} else if (Constants.DB_CONNECTION_TYPE_JDBC.equals(schema
					.getConectionType())) {
				if (!jdbcMap.containsKey(instId))
					registerJdbcPool(schema);
				return jdbcMap.get(instId).getConnection();
			} else
				return null;
		} catch (PosException e) {
			Log.error("In getConnection ", e);
			throw e;
		} catch (Exception e) {
			Log.error("In getConnection ", e);
			throw new PosException(Constants.ERR_SYSTEM_ERROR);
		}
	}

	private void registerJdbcPool(DatabaseSchema schema) throws NamingException {
		Log.trace("Registering connections for " + schema.getMspAcr()
				+ " using Jdbc");
		HikariDataSource hds = jdbcDatasource.getDataSource(schema,
				getDriver(), getTestQuery());
		jdbcMap.put(schema.getMspAcr(), hds);

	}

	private void registerJndiPool(DatabaseSchema schema) throws NamingException {
		Log.trace("Registering connections for " + schema.getMspAcr()
				+ " using Jndi " + schema.getJndi());
		DataSource ds = jndiDataSource.getDataSource(schema.getJndi());
		jndiMap.put(schema.getMspAcr(), ds);
	}

	protected void iterateResultSet(ResultSet rs, ResultSetMetaData rsmt,
			Class<?> clazz, Object o) throws Exception {
		try {
			for (int i = 1; i < rsmt.getColumnCount() + 1; i++) {
				Object obj = rs.getObject(i);
				String fieldName = clazz.getCanonicalName()
						+ rsmt.getColumnName(i);
				String value = obj == null ? null : String.valueOf(obj);
				MethodHandle mh = StaticStore.setterMethodHandles
						.get(fieldName);
				if (mh == null) {
					Method m = clazz.getDeclaredMethod(
							"set" + Util.capitalize(rsmt.getColumnName(i)),
							new Class[] { String.class });
					m.invoke(o, value);
				} else {
					mh.invoke(o, value);
				}
			}
		} catch (Throwable e) {
			Log.error("iterateResultSet ", e);
			throw new Exception(e);
		}
	}

	protected void closeConnection(ResultSet r, PreparedStatement p,
			Connection c) {
		try {
			if (r != null)
				r.close();
			if (p != null)
				p.close();
			if (c != null)
				c.close();
		} catch (Exception e) {
			Log.error("closeConnection ", e);
		}
	}

	protected abstract String getDriver();

	protected abstract String getTestQuery();

}
