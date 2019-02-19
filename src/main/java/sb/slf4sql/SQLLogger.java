package sb.slf4sql;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SQLLogger {
	private static final Logger logger = LoggerFactory.getLogger(SQLLogger.class);

	public static void log(String message) {
		logger.debug(message);
	}

	public static void log(int statementId, String message) {
		logger.debug(MessageFormat.format("Statement-{0} {1}", StringUtil.fixedLengthString(String.valueOf(statementId), 12), message));
	}

	public static boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}
}
