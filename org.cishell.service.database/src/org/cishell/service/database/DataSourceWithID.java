package org.cishell.service.database;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

public interface DataSourceWithID {

	public abstract int getID();

	public abstract Connection getConnection() throws SQLException;

	public abstract Connection getConnection(String username, String password) throws SQLException;

	public abstract PrintWriter getLogWriter() throws SQLException;

	public abstract void setLogWriter(PrintWriter out) throws SQLException;

	public abstract int getLoginTimeout() throws SQLException;

	public abstract void setLoginTimeout(int seconds) throws SQLException;

}