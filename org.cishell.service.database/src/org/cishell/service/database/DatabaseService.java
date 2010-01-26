package org.cishell.service.database;

public interface DatabaseService {
	public Database createNewDatabase() throws DatabaseCreationException;
	public Database connectToExistingDatabase(String driver, String url)
		throws DatabaseCreationException;
	public Database connectToExistingDatabase(
			String driver, String url, String username, String password)
		throws DatabaseCreationException;
	public Database copyDatabase(Database originalDatabase) throws DatabaseCopyException;
}
