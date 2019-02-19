package sb.slf4sql.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
import java.util.Map;
import java.util.TreeMap;

import sb.slf4sql.SQLArgument;
import sb.slf4sql.SQLLogger;
import sb.slf4sql.StringUtil;

public class PreparedStatementHandler extends BaseStatementHandler {
	private String sql;
	private final TreeMap<Integer, SQLArgument> arguments;

	public PreparedStatementHandler(PreparedStatement preparedStatement, String sql) {
		super(preparedStatement);
		this.sql = sql;
		this.arguments = new TreeMap<>();
	}

	public static PreparedStatement newInstance(PreparedStatement stmt, String sql) {
		InvocationHandler handler = new PreparedStatementHandler(stmt, sql);
		return (PreparedStatement) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { PreparedStatement.class }, handler);
	}

	public static CallableStatement newInstance(CallableStatement stmt, String sql) {
		InvocationHandler handler = new PreparedStatementHandler(stmt, sql);
		return (CallableStatement) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { CallableStatement.class }, handler);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//SQLLogger.get().debug("PreparedStatement - proxy: " + method);

		if (EXECUTE_METHODS.contains(method.getName())) {
			return handleExecution(method, args);
		}
		else if (SET_METHODS.contains(method.getName())) {
			return handleArgumemnts(method, args);
		}
		else if ("clearParameters".equals(method.getName())) {
        	synchronized (arguments) {
    			arguments.clear();
    		}

        	return handleExecution(method, args);
        }
        else if ("getResultSet".equals(method.getName())) {
//        	ps.executeQuery()
        	return handleResultSet(method, args);
        }
        else if ("getUpdateCount".equals(method.getName())) {
        	return handleUpdateCount(method, args);
        }

		return method.invoke(statement, args);
	}

	@Override
	protected void onExecuting(Object[] args) {
		String parameters = getArgumentValues();
		SQLLogger.log(statement.hashCode(), MessageFormat.format("==> Parameters: {0}", parameters));

    	String parsedSQL = parseSQL();
    	SQLLogger.log(statement.hashCode(), MessageFormat.format("==> SQL:\n{0}", parsedSQL));
	}

	private void putArgument(int position, Object argValue) {
		SQLArgument sqlArgument = new SQLArgument(position);

		try {
			sqlArgument.setArgument(StringUtil.getArgumentValue(argValue));
		} catch (Throwable t) {
			sqlArgument.setArgument(argValue.toString());
		}

		try {
			sqlArgument.setSQLParameter(StringUtil.getSQLParameter(argValue));
		} catch (Throwable t) {
			sqlArgument.setSQLParameter(argValue.toString());
		}

		synchronized (arguments) {
			arguments.put(new Integer(position), sqlArgument);
		}
	}

	protected String parseSQL() {
		StringBuffer parsedSql = new StringBuffer();
		if (sql.contains("?")) {
			String[] segments = sql.concat(" ").split("\\?");
			for (int i = 0; i < segments.length; i++) {
				if (i > 0) {
					SQLArgument arg = getSqlArgument(i);
					if (arg != null)
						parsedSql.append(arg.getSQLParameter());
				}

				String segment = segments[i];
				parsedSql.append(segment);
			}
		}
		else {
			parsedSql.append(sql);
		}

		return parsedSql.toString();
	}

	private SQLArgument getSqlArgument(int position) {
		synchronized (arguments) {
			return arguments.get(position);
		}
	}

	private Object handleUpdateCount(Method method, Object[] args) throws Exception {
		Object result = method.invoke(statement, args);
		if (result != null) {
			SQLLogger.log(statement.hashCode(), MessageFormat.format("<== Updates: {0}", result));
		}

		return result;
	}

	private String getArgumentValues() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Integer, SQLArgument> entry : arguments.entrySet()) {
			SQLArgument argument = entry.getValue();
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(argument.getArgument());
		}

		return sb.toString();
	}

	private Object handleArgumemnts(Method method, Object[] args) throws Exception {
		if ("setNull".equals(method.getName())) {
			putArgument((int) args[0], null);
		}
		else {
			putArgument((int) args[0], args[1]);
		}

		return method.invoke(statement, args);
	}
}
