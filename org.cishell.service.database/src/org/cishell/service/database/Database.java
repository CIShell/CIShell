package org.cishell.service.database;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;

public interface Database {
	public Connection getConnection() throws SQLException;
}