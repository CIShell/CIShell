package org.cishell.service.database.databasefactory;

import javax.sql.DataSource;

public interface DatabaseService {

	public DataSource createDatabase() throws DatabaseCreationException;
	public DataSource copyDatabase(DataSource database) throws DatabaseCopyException;
}
