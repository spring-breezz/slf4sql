package sb.slf4sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class ProxyDriver implements Driver {
	public static final String URL_PREFIX = "jdbc:slf4sql:";
	public static Driver instance = new ProxyDriver();

	private Driver realDBDriver;
	private String realDBUrl;

	static {
		try {
			DriverManager.registerDriver(ProxyDriver.instance);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot register ProxyDriver in DriverManager", e);
		}
	}

	public ProxyDriver() {
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		String realUrl = extractRealUrl(url);
		Driver driver = findUnderlyingDriver(realUrl);
		if (driver == null) {
			throw new SQLException("Cannot find a driver from DriverManager");
		}

		realDBUrl = realUrl;
		realDBDriver = driver;

		final Connection connection = driver.connect(realDBUrl, info);
		if (connection == null) {
			throw new SQLException("Invalid or unknown driver url: " + realDBUrl);
		}

		if (SQLLogger.isDebugEnabled()) {
			ProxyConnection proxyConnection = new ProxyConnection(connection);
			SQLLogger.log(String.format("Connection-%1$s checked out from %2$s", proxyConnection.hashCode(), realUrl));
			return proxyConnection;
		}

		return connection;
	}

	@Override
	public boolean acceptsURL(String url) {
		return url != null && url.startsWith(URL_PREFIX);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		String realUrl = extractRealUrl(url);
		Driver driver = findUnderlyingDriver(realUrl);
		if (driver == null) {
			return new DriverPropertyInfo[0];
		}

		return driver.getPropertyInfo(url, info);
	}

	@Override
	public int getMajorVersion() {
		return realDBDriver == null ? 1 : realDBDriver.getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		return realDBDriver == null ? 0 : realDBDriver.getMinorVersion();
	}

	@Override
	public boolean jdbcCompliant() {
		return realDBDriver != null && realDBDriver.jdbcCompliant();
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	private String extractRealUrl(String url) {
		return acceptsURL(url) ? url.replaceFirst(URL_PREFIX, "jdbc:") : url;
	}

	private static List<Driver> getRegisteredDrivers() {
		List<Driver> drivers = new ArrayList<>();
		Enumeration<Driver> e = DriverManager.getDrivers();
		while (e.hasMoreElements()) {
			Driver driver = e.nextElement();
			drivers.add(driver);
		}

		return drivers;
	}

	private Driver findUnderlyingDriver(String realUrl) throws SQLException {
		for (Driver driver : getRegisteredDrivers()) {
			try {
				if (driver.acceptsURL(realUrl)) {
					return driver;
				}
			}
			catch (Exception e) {}
		}

		return null;
	}
}
