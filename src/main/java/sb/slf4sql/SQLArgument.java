package sb.slf4sql;

import java.text.MessageFormat;

public class SQLArgument {
	private int position;
	private String argument; // form Application
	private String sqlParameter; // for SQL

	public SQLArgument(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getArgument() {
		return argument;
	}
	public void setArgument(String argument) {
		this.argument = argument;
	}
	public String getSQLParameter() {
		return sqlParameter;
	}
	public void setSQLParameter(String parameter) {
		this.sqlParameter = parameter;
	}

	@Override
	public String toString() {
		return MessageFormat.format("position: {0}, argument: {1}, sqlParameter: {2}", position, argument, sqlParameter);
	}
}
