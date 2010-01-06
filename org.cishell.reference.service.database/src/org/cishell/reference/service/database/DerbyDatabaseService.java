package org.cishell.reference.service.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.cishell.service.database.Database;
import org.cishell.service.database.DatabaseCreationException;
import org.cishell.service.database.DatabaseService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


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
					removeAllNonSystemDatabaseTables(internalDatabaseConnection);
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
	
	public synchronized Database createNewDatabase() throws DatabaseCreationException {
		try {
		//connect to and create a 'new' database
		String databaseName = INTERNAL_DB_NAME_PREFIX + id;
		InternalDerbyDatabase db =  
			new InternalDerbyDatabase(createNewInternalDataSource(databaseName));
		
		
		//if this database existed on disk from a previous session, clean it to be like new
		removeAllNonSystemDatabaseTables(db.getConnection());
		
		//keep track of our new database for this CIShell session
		internalDatabases.add(db);
		
		id++;
		
		return db;
		} catch (Exception e) {
			throw new DatabaseCreationException(e);
		}
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
	
	private static final int SCHEMA_NAME_INDEX = 2;
	private static final int TABLE_NAME_INDEX = 3;
	private static final String NONSYSTEM_SCHEMA_NAME = "APP";
	
	//(removes all non-system tables from the provided databases)
	private void removeAllNonSystemDatabaseTables(Connection dbConnection) throws SQLException  {
		   DatabaseMetaData dbMetadata = dbConnection.getMetaData();
		   //using the TABLE type means we get no system tables.
		   ResultSet allTableNames = dbMetadata.getTables(null, null, null, new String[]{"TABLE"});
		   
		   Statement removeTables = dbConnection.createStatement();
		   
		   while (allTableNames.next()) {
				String dropTableQuery = formDropTableQuery(allTableNames.getString(SCHEMA_NAME_INDEX),
											allTableNames.getString(TABLE_NAME_INDEX));
				removeTables.addBatch(dropTableQuery);
		   }
		   
		   removeTables.executeBatch();	   
	}
	
	private String formDropTableQuery(String schema, String tableName) {
		String schemaPrefix = "";
		if(schema != null && !"".equals(schema)) {
			schemaPrefix = schema + ".";
		}
		String removeTableSQL = 
			  "DROP TABLE " 
			  + schemaPrefix + "." + tableName;
		return removeTableSQL;
	}
}
