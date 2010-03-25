package org.cishell.reference.service.database;

import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.cishell.reference.service.database.utility.DatabaseCleaner;
import org.cishell.service.database.Database;
import org.cishell.service.database.DatabaseCopyException;
import org.cishell.service.database.DatabaseCreationException;
import org.cishell.service.database.DatabaseService;
import org.cishell.utilities.DatabaseUtilities;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


//TODO: rework exception handling everywhere to be failsafe.
public class DerbyDatabaseService implements DatabaseService, BundleActivator {

	public static final String DERBY_DRIVER_NAME =
		"org.apache.derby.jdbc.EmbeddedDriver";
	public static final String DERBY_PROTOCOL = "jdbc:derby:";

	public static final String DEFAULT_CREATE_CONNECTION_STRING = ";create=true";
	public static final String DEFAULT_SHUTDOWN_CONNECTION_STRING =";shutdown=true";

	//where the database exists on the filesystem (relative to the application root directory)
	private static final String DATABASE_DIRECTORY = "database/";

	//hold on to our service registration so we can unregister when this plugin stops.
	private ServiceRegistration databaseServiceRegistration;

	/*
	 * internal databases are created when requested by outside services,
	 * and are cleaned and shut down when this service is stopped.
	 */
	private List<InternalDerbyDatabase> internalDatabases = new ArrayList<InternalDerbyDatabase>();

	public final void start(BundleContext context) throws Exception {

		/*
		 * Tell Derby to look for an existing database or create a new database 
		 * in the default directory (within our application's directory)		 
		 */
		System.setProperty("derby.system.home", DATABASE_DIRECTORY);

		//Allow the database service to be found by other services/plugins
		//TODO: figure out why this throws a concurrentmodificationerror on startup
		databaseServiceRegistration = context.registerService(
				DatabaseService.class.getName(), this, new Hashtable());
	}

	public final void stop(BundleContext context)  {
		//disallow the database service to be found by other services/plugins
		this.databaseServiceRegistration.unregister();

		//Clean out the internal databases and shut them down.
		try {
			for (InternalDerbyDatabase internalDatabase : internalDatabases) {
				Connection internalDatabaseConnection = internalDatabase.getConnection();
				//DatabaseCleaner.cleanDatabase(internalDatabaseConnection, false);
				internalDatabase.shutdown();
			}
		} catch (Exception e) {
			String message =
				"An unexpected exception occurred while shutting down the internal database." 
				+ "Aborting database shutdown process." 
				+ "Database may not be left in a valid state, "
				+ "but we will try to make its state valid on next startup.";
			throw new RuntimeException(message, e);
		}
	}


	private static final String INTERNAL_DB_NAME_PREFIX = "cishell_database";
	private static int id = 0;

	public Database createNewDatabase() throws DatabaseCreationException {
		//connect to and create a 'new' database
		String databaseName = nextDatabaseIdentifier();
		DataSource internalDataSource = createNewInternalDataSource(databaseName);
		//TODO: find a way to get the darn database name that isn't awful!
		InternalDerbyDatabase internalDatabase = createNewInternalDatabase(internalDataSource);
		internalDatabase.setName(databaseName);
		return internalDatabase;
	}



	public Database connectToExistingDatabase(String driver, String url) 
	throws DatabaseCreationException {
		return connectToExistingDatabase(driver, url, null, null);
	}

	public Database connectToExistingDatabase(
			String driver, String url, String username, String password)
	throws DatabaseCreationException {
		DataSource dataSource = 
			createNewDataSource(driver, url, username, password);
		//TODO: See if we can get the default schema as a property somehow. 
		Database db = new ExternalDatabase(dataSource, "APP"); 
		return db;
	}



	public Database copyDatabase(Database originalDatabase) throws DatabaseCopyException {
		/* Connection originalConnection = null;
		Connection newConnection = null;
		try {
			Database newDatabase = createNewDatabase();
			originalConnection = originalDatabase.getConnection();
			try {
				newConnection = newDatabase.getConnection();
				DatabaseTable[] tables = DatabaseTable.availableTables(originalConnection);
				for(DatabaseTable table : tables) {
					table.duplicateTable(originalConnection, newConnection);
				}
				for(DatabaseTable table : tables) {
					table.transferPrimaryKey(originalConnection, newConnection);
					table.pointForeignKeys(originalConnection, newConnection);
				}


			} catch(SQLException e) {
				throw new DatabaseCopyException("There was a problem creating the new database: " + e.getMessage(), e);
			}
		} catch (DatabaseCreationException e) {
			throw new DatabaseCopyException("Unable to create a new database to copy into: " + e.getMessage(), e);
		} catch (SQLException e) {
			throw new DatabaseCopyException("Unable to connect to the database being copied.", e);
		} finally {
			DatabaseUtilities.closeConnectionQuietly(originalConnection);
			DatabaseUtilities.closeConnectionQuietly(newConnection);
		}

		//TODO: copy views. Wait until we have the darn things.
		/*
		 * TODO: make copy table work
		 * On the subject of copying tables . . . looks like that needs to be provided. Rough plan of attack:
		 * Make new database. For every table in the original (make sure system tables are excluded), make an identical table in the new one.
		 * For every table, copy all values into the new database.
		 * Put all constraints in place on the new database (especially: primary keys, foreign keys)
		 */

		//Make a backup, then make a new database derived from the backup.
		if(originalDatabase instanceof InternalDerbyDatabase) {
			InternalDerbyDatabase database = (InternalDerbyDatabase) originalDatabase;
			String originalName = database.getName();
			Connection connection = null;
			try {
				connection = database.getConnection();
				String backupLocation = createDatabaseBackup(connection,
						originalName);
				
				InternalDerbyDatabase internalDatabase = createInternalDatabaseFromBackup(backupLocation);
				return internalDatabase;
				
				
			} catch (SQLException e) {
				throw new DatabaseCopyException("A problem occurred while attempting to copy the database.", e);
			} catch (DatabaseCreationException e) {
				throw new DatabaseCopyException("A problem occurred while attempting to copy the database.", e);
			} finally {
				DatabaseUtilities.closeConnectionQuietly(connection);
			}
			
			
			
		} else {
			throw new DatabaseCopyException("Unable to copy external databases!");
		}
	}

	private InternalDerbyDatabase createInternalDatabaseFromBackup(
			String backupLocation) throws DatabaseCreationException {
		String newName = nextDatabaseIdentifier();
		String newDatabaseConnectionURL = DERBY_PROTOCOL + newName + ";restoreFrom=" + backupLocation;
		DataSource derivedDataSource = createNewDataSource(DERBY_DRIVER_NAME,
											newDatabaseConnectionURL, null, null);
		InternalDerbyDatabase internalDatabase = createNewInternalDatabase(derivedDataSource, false);
		internalDatabase.setName(newName);
		return internalDatabase;
	}

	private String createDatabaseBackup(Connection connection,
			String originalName) throws SQLException {
		CallableStatement backupStatement = connection.prepareCall("CALL SYSCS_UTIL.SYSCS_BACKUP_DATABASE(?)");
		String tempDir = System.getProperty("java.io.tmpdir");
		backupStatement.setString(1, tempDir);
		backupStatement.execute();
		String backupLocation = new File(new File(tempDir), originalName).getAbsolutePath();
		return backupLocation;
	}
	
	
	

	//***---UTILITIES---***

	private DataSource createNewDataSource(
			String driver, String url, String username, String password) 
	throws DatabaseCreationException {
		try {
			//Load the database driver
			Class.forName(driver);

			//create a new data source based on the database connection info provided.
			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
					url, username, password);	
			GenericObjectPool connectionPool = new GenericObjectPool();
			KeyedObjectPoolFactory stmtPool = new GenericKeyedObjectPoolFactory(null);
			new PoolableConnectionFactory(
					connectionFactory, connectionPool, stmtPool, null, false, true);
			DataSource dataSource = new PoolingDataSource(connectionPool);

			//test the connection (this will throw an exception if the connection is faulty)
			dataSource.getConnection();

			//return that data source.
			return dataSource;
		} catch (ClassNotFoundException e) {
			throw new DatabaseCreationException(
					"Database driver '" + driver + "' could not be found", e);
		} catch (SQLException e) {
			throw new DatabaseCreationException(e.getMessage(), e);
		}
	}

	private DataSource createNewInternalDataSource(String dbName)
	throws DatabaseCreationException {
		String newDatabaseConnectionURL = DERBY_PROTOCOL 
		+ dbName 
		+ DEFAULT_CREATE_CONNECTION_STRING;
		return createNewDataSource(DERBY_DRIVER_NAME, newDatabaseConnectionURL, null, null);
	}
	
	
	private InternalDerbyDatabase createNewInternalDatabase(DataSource internalDataSource) throws DatabaseCreationException {
		return createNewInternalDatabase(internalDataSource, true);
	}

	private InternalDerbyDatabase createNewInternalDatabase(DataSource internalDataSource, boolean clean)
	throws DatabaseCreationException {
		InternalDerbyDatabase db =  
			new InternalDerbyDatabase(internalDataSource);
		Connection cleaningConnection = null;
		try {
			//if this database existed on disk from a previous session, clean it to be like new
			if(clean) {
			cleaningConnection = db.getConnection();
			DatabaseCleaner.cleanDatabase(cleaningConnection, false);
			}

			//keep track of our new database for this CIShell session
			internalDatabases.add(db);


			return db;
		} catch (Exception e) {
			DatabaseUtilities.closeConnectionQuietly(cleaningConnection);
			throw new DatabaseCreationException(e);
		}
	}
	//only thing that needs to be synchronized; all other ops are non-conflicting once they have differing names
	private synchronized String nextDatabaseIdentifier() {
		//Random to deal with the multiple running CIShell instance problem. Note: this means databases will build up. This is not a big deal, as even a "large" database will only be a few megabytes.
		String randomPart = UUID.randomUUID().toString();
		String identifier = INTERNAL_DB_NAME_PREFIX + randomPart + "_" + id;
		id++;
		return identifier;
	}

}
