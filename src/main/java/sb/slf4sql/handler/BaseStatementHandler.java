package sb.slf4sql.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import sb.slf4sql.SQLLogger;

public abstract class BaseStatementHandler implements InvocationHandler {
	protected static final Set<String> SET_METHODS = new HashSet<>();
	protected static final Set<String> EXECUTE_METHODS = new HashSet<>();

	protected Statement statement;

	static {
		SET_METHODS.add("setString");
		SET_METHODS.add("setNString");
		SET_METHODS.add("setInt");
		SET_METHODS.add("setByte");
		SET_METHODS.add("setShort");
		SET_METHODS.add("setLong");
		SET_METHODS.add("setDouble");
		SET_METHODS.add("setFloat");
		SET_METHODS.add("setTimestamp");
		SET_METHODS.add("setDate");
		SET_METHODS.add("setTime");
		SET_METHODS.add("setArray");
		SET_METHODS.add("setBigDecimal");
		SET_METHODS.add("setAsciiStream");
		SET_METHODS.add("setBinaryStream");
		SET_METHODS.add("setBlob");
		SET_METHODS.add("setBoolean");
		SET_METHODS.add("setBytes");
		SET_METHODS.add("setCharacterStream");
		SET_METHODS.add("setNCharacterStream");
		SET_METHODS.add("setClob");
		SET_METHODS.add("setNClob");
		SET_METHODS.add("setObject");
		SET_METHODS.add("setNull");

		EXECUTE_METHODS.add("execute");
		EXECUTE_METHODS.add("executeUpdate");
		EXECUTE_METHODS.add("executeQuery");
		EXECUTE_METHODS.add("addBatch");
	}

	public BaseStatementHandler(Statement statement) {
		this.statement = statement;
	}

	abstract protected void onExecuting(Object[] args);

	protected Object handleExecution(Method method, Object[] args) throws Exception {
		long start = System.currentTimeMillis();
		try {
			onExecuting(args);

	    	if ("executeQuery".equals(method.getName())) {
	    		return handleResultSet(method, args);
			}

	    	return method.invoke(statement, args);
		}
		finally {
			long end = System.currentTimeMillis() - start;
	    	SQLLogger.log(statement.hashCode(), MessageFormat.format("<== SQL executed in {0} msec", end));
		}
	}

	protected Object handleResultSet(Method method, Object[] args) throws Exception {
		Object result = method.invoke(statement, args);
		if (result != null && result instanceof ResultSet) {
			ResultSet rs = (ResultSet) result;
	        return ResultSetHandler.newInstance(rs, statement.hashCode());
		}

		return result;
	}
}
