package org.cishell.utilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.service.database.Database;

public class DatabaseUtilities {
	public static void closeConnectionQuietly(Connection connection) {
		if(connection == null) {
			return;
		}
		try {
			connection.close();
		} catch (SQLException e) {
			//quietly! Use this only in finally blocks, pretty much, and right before throwing exceptions that will leave the scope of the Connection object.
		}
	}

	public static Connection connect(Database database, String messageIfError)
	throws AlgorithmExecutionException {
		Connection connection;
		try {
			connection = database.getConnection();
		} catch (SQLException e) {
			throw new AlgorithmExecutionException(messageIfError, e);
		}
		return connection;
	}

	public static Statement createStatement(Connection connection, String messageIfError)
	throws AlgorithmExecutionException {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			throw new AlgorithmExecutionException(messageIfError, e);
		}
	}

	public static Statement createStatement(
			Connection connection,
			int resultSetType,
			int resultSetConcurrency,
			String messageIfError)
	throws AlgorithmExecutionException {
		try {
			return connection.createStatement(resultSetType, resultSetConcurrency);
		} catch (SQLException e) {
			throw new AlgorithmExecutionException(messageIfError, e);
		}
	}

	public static Statement createStatement(
			Connection connection,
			int resultSetType,
			int resultSetConcurrency,
			int resultSetHoldability,
			String messageIfError)
	throws AlgorithmExecutionException {
		try {
			return connection.createStatement(
				resultSetType, resultSetConcurrency, resultSetHoldability);
		} catch (SQLException e) {
			throw new AlgorithmExecutionException(messageIfError, e);
		}
	}

	public static String createSQLInExpression(List<String> columns, List<Map<String, Object>> valueMaps) {
		String columnNames = implodeAndWrap(columns);
		
		List<String> values = new ArrayList<String>();
		for(Map<String, Object> oldValues : valueMaps) {
			List<String> rowValues = new ArrayList<String>();
			for(String column : columns) {
				rowValues.add(formatValue(oldValues.get(column)));
			}
			values.add(implodeAndWrap(rowValues));
		}
		String columnValues = implodeAndWrap(values);
		
		
		//first make part with column names
		//then make part with groups of column values
		return columnNames + " IN " + columnValues;
	}
	
	//TODO: expand this to other sorts of SQL datatypes (per their Prefuse table equivalents)
	public static String formatValue(Object value) {
		if(value == null) {
			return "NULL";
		} else if(value instanceof Number) {
			return value.toString();
		} else {
			return "'" + value.toString() + "'";
		}
	}
	
	//TODO: Consider abstracting what you're wrapping with and making this a StringUtility.
	public static String implodeAndWrap(List<String> values) {
		return "(" + StringUtilities.implodeItems(values, ", ") + ")";
	}
}
