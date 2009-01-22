package org.cishell.reference.service.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.cishell.service.database.DataSourceWithID;

public class DataSourceWithIDImpl implements DataSource, DataSourceWithID {
	private static int idCounter = 0;
	
	private DataSource wrappedDataSource;
	private int id;
	
	private static int generateNewID() {
		int newID = idCounter;
		
		idCounter++;
		
		return newID;
	}
	
	public DataSourceWithIDImpl(DataSource wrappedDataSource) {
		this.wrappedDataSource = wrappedDataSource;
		this.id = generateNewID();
	}
	
	/* (non-Javadoc)
	 * @see org.cishell.service.database.DataSourceWithIDI#getID()
	 */
	public int getID() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see org.cishell.service.database.DataSourceWithIDI#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		return this.wrappedDataSource.getConnection();
	}
	
	/* (non-Javadoc)
	 * @see org.cishell.service.database.DataSourceWithIDI#getConnection(java.lang.String, java.lang.String)
	 */
	public Connection getConnection(String username, String password)
			throws SQLException {
		return this.wrappedDataSource.getConnection(username, password);
	}
	
	/* (non-Javadoc)
	 * @see org.cishell.service.database.DataSourceWithIDI#getLogWriter()
	 */
	public PrintWriter getLogWriter() throws SQLException {
		return this.wrappedDataSource.getLogWriter();
	}
	
	/* (non-Javadoc)
	 * @see org.cishell.service.database.DataSourceWithIDI#setLogWriter(java.io.PrintWriter)
	 */
	public void setLogWriter(PrintWriter out) throws SQLException {
		this.wrappedDataSource.setLogWriter(out);
	}
	
	/* (non-Javadoc)
	 * @see org.cishell.service.database.DataSourceWithIDI#getLoginTimeout()
	 */
	public int getLoginTimeout() throws SQLException {
		return this.wrappedDataSource.getLoginTimeout();
	}
	
	/* (non-Javadoc)
	 * @see org.cishell.service.database.DataSourceWithIDI#setLoginTimeout(int)
	 */
	public void setLoginTimeout(int seconds) throws SQLException {
		this.wrappedDataSource.setLoginTimeout(seconds);
	}
}