package org.cishell.service.database;

import java.sql.ResultSet;

import javax.sql.DataSource;

public interface DatabaseService {

	public DataSourceWithID createDatabase() throws DatabaseCreationException;
	public DataSourceWithID createDatabase(ResultSet resultSet) throws DatabaseCreationException;
	public DataSourceWithID copyDatabase(DataSourceWithID database) throws DatabaseCopyException;
}
