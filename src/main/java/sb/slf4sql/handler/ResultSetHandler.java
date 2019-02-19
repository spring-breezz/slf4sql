package sb.slf4sql.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.text.MessageFormat;

import sb.slf4sql.SQLLogger;

public final class ResultSetHandler implements InvocationHandler {
	private int rows;
	private int statementId;
	private final ResultSet resultSet;

	private ResultSetHandler(ResultSet resultSet, int statementId) {
		this.resultSet = resultSet;
		this.statementId = statementId;
	}

	public static ResultSet newInstance(ResultSet rs, int statementId) {
		InvocationHandler handler = new ResultSetHandler(rs, statementId);
		ClassLoader cl = ResultSet.class.getClassLoader();
		return (ResultSet) Proxy.newProxyInstance(cl, new Class[] { ResultSet.class }, handler);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] params) throws Throwable {
		try {
			if (Object.class.equals(method.getDeclaringClass())) {
				return method.invoke(this, params);
			}
			Object o = method.invoke(resultSet, params);
			if ("next".equals(method.getName())) {
				if ((Boolean) o) {
					rows++;
				} else {
					SQLLogger.log(statementId, MessageFormat.format("<== Total: {0}", rows));
				}
			}
			return o;
		} catch (Throwable t) {
			throw t;
		}
	}

	public ResultSet getResultSet() {
		return resultSet;
	}
}
