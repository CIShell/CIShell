package org.cishell.service.database;

public interface DatabaseService {
	public DataSourceWithID createDatabase() throws DatabaseCreationException;
}
