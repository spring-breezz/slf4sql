package sb.slf4sql.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Statement;
import java.text.MessageFormat;

import sb.slf4sql.SQLLogger;

public class StatementHandler extends BaseStatementHandler {

	public StatementHandler(Statement statement) {
		super(statement);
	}

	public static Statement newInstance(Statement stmt) {
		InvocationHandler handler = new StatementHandler(stmt);
		return (Statement) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[] { Statement.class }, handler);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//SQLLogger.get().debug("StatementHandler - proxy: " + method);

		if (EXECUTE_METHODS.contains(method.getName())) {
			return handleExecution(method, args);
		}
		else if ("getResultSet".equals(method.getName())) {
			return handleResultSet(method, args);
		}

		return method.invoke(statement, args);
	}

	@Override
	protected void onExecuting(Object[] args) {
		SQLLogger.log(statement.hashCode(), MessageFormat.format("==> SQL:\n{0}", (String) args[0]));
	}
}
