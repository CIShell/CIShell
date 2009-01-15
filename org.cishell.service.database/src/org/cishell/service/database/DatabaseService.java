package org.cishell.service.database;

import java.sql.ResultSet;

import javax.sql.DataSource;

public interface DatabaseService {

	public DataSource createDatabase() throws DatabaseCreationException;
	public DataSource createDatabase(ResultSet resultSet) throws DatabaseCreationException;
	public DataSource copyDatabase(DataSource database) throws DatabaseCopyException;
}
