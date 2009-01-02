package org.cishell.service.database.databasefactory;

import javax.sql.DataSource;

public interface DatabaseFactory {

	public DataSource createDatabase() throws DatabaseCreationException;
}
