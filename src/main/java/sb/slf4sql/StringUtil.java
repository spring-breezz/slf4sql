package sb.slf4sql;

import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

public class StringUtil {
	protected static final String dateFormat = "MM/dd/yyyy HH:mm:ss.SSS";

	public static String getSQLParameter(Object object) {
		if (object == null) {
			return "NULL";
		}

		if (object instanceof String) {
			return "'" + escapeString((String) object) + "'";
		}
		else if (object instanceof Date) {
			return "'" + new SimpleDateFormat(dateFormat).format(object) + "'";
		}
		else if (object instanceof Boolean) {
			return ((Boolean) object).booleanValue() ? "1" : "0";
		}
		else {
			return object.toString();
		}
	}

	public static String getArgumentValue(Object value) {
		if (value == null) {
			return "null";
		}

		return objectValueString(value) + "(" + value.getClass().getSimpleName() + ")";
	}

	public static String objectValueString(Object value) {
		if (value instanceof Array) {
			return arraytoString((Array) value);
		}

		return value.toString();
	}

	public static String arraytoString(Array array) {
		try {
			return Arrays.asList((String[]) array.getArray())
					.stream()
	                .collect(Collectors.joining(", ","[","]"));
		}
		catch (Exception e) {
			return "";
		}
	}

	public static String escapeString(String in) {
		StringBuilder out = new StringBuilder();
		for (int i = 0, j = in.length(); i < j; i++) {
			char c = in.charAt(i);
			if (c == '\'') {
				out.append(c);
			}
			out.append(c);
		}
		return out.toString();
	}

	public static String fixedLengthString(String string, int length) {
	    return String.format("%1$-"+length+ "s", string);
	}

	public static void main(String[] args) {

//		System.out.println("'" + fixedLengthString("12456", 10) + "'");
	}
}
