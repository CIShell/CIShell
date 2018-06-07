package org.cishell.reference.service.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.cishell.service.database.Database;

public class InternalDerbyDatabase implements Database {
	
	private DataSource dataSource;
	private String databaseName;
	// TODO: Should this be public?
	public InternalDerbyDatabase(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}
	
	public void shutdown() throws SQLException {
			String shutdownDatabaseCommand = 
				DerbyDatabaseService.DERBY_PROTOCOL 
				+ DerbyDatabaseService.DEFAULT_SHUTDOWN_CONNECTION_STRING;
				DriverManager.getConnection(shutdownDatabaseCommand);
	}


	//TODO: We might want to expose our different 'databases' as different schemas at some point instead.
	public String getApplicationSchemaName() {
		return "APP";
	}


	protected void setName(String databaseName) {
		this.databaseName = databaseName;
	}
	
	protected String getName() {
		return databaseName;
	}
}
