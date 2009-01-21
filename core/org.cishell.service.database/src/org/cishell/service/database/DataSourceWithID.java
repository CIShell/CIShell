package org.cishell.service.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

public class DataSourceWithID implements DataSource {
	private static int idCounter = 0;
	
	private DataSource wrappedDataSource;
	private int id;
	
	private static int generateNewID() {
		int newID = idCounter;
		
		idCounter++;
		
		return newID;
	}
	
	public DataSourceWithID(DataSource wrappedDataSource) {
		this.wrappedDataSource = wrappedDataSource;
		this.id = generateNewID();
	}
	
	public int getID() {
		return id;
	}
	
	public Connection getConnection() throws SQLException {
		return this.wrappedDataSource.getConnection();
	}
	
	public Connection getConnection(String username, String password)
			throws SQLException {
		return this.wrappedDataSource.getConnection(username, password);
	}
	
	public PrintWriter getLogWriter() throws SQLException {
		return this.wrappedDataSource.getLogWriter();
	}
	
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.wrappedDataSource.setLogWriter(out);
	}
	
	public int getLoginTimeout() throws SQLException {
		return this.wrappedDataSource.getLoginTimeout();
	}
	
	public void setLoginTimeout(int seconds) throws SQLException {
		this.wrappedDataSource.setLoginTimeout(seconds);
	}
}