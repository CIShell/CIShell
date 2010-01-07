package org.cishell.utilities;

import java.sql.Connection;
import java.sql.SQLException;

import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.service.database.Database;

public class DatabaseUtilities {
	public static void closeConnectionQuietly(Connection connection) {
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
}
