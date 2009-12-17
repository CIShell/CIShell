package org.cishell.reference.service.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.cishell.service.database.Database;

public class InternalDerbyDatabase implements Database {
	
	private String name;
	private DataSource dataSource;
	
	public InternalDerbyDatabase(String name, DataSource dataSource) {
		this.name = name;
		this.dataSource = dataSource;
	}

	
	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
	
	public void shutdown() throws SQLException {
			String shutdownDatabaseCommand = 
				DerbyDatabaseService.DERBY_PROTOCOL + 
				DerbyDatabaseService.DEFAULT_SHUTDOWN_CONNECTION_STRING;
				DriverManager.getConnection(shutdownDatabaseCommand);


	}
}
