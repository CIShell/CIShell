package org.cishell.reference.service.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.cishell.service.database.DataSourceWithID;
import org.cishell.service.database.DatabaseCreationException;
import org.cishell.service.database.DatabaseService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;



public class DerbyDatabaseService implements DatabaseService, BundleActivator {
	
	/*
	 * Variables used to connect to the internal derby database
	 * 
	 * TODO: Eventually support connecting databases other than derby,
	 * and connecting to databases other than our internal one
	 * (maybe not by modifying this class directly though).
	 */
	private static final String DERBY_DRIVER_NAME =
		"org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DERBY_PROTOCOL = "jdbc:derby:";
	private static final String DEFAULT_CREATE_CONNECTION_STRING = ";create=true";
	private static final String DEFAULT_SHUTDOWN_CONNECTION_STRING =";shutdown=true";
	private static final String DEFAULT_DB_NAME = "cishell_database";
	
	//where the database exists on the filesystem (relative to the application root directory)
	private static final String DATABASE_DIRECTORY = "database/";
	
	//(new connections to the database will come from a pool of connections, improving performance)
	private PoolingDataSource poolingDataSource = null;
	
	//hold on to our service registration so we can unregister when this plugin stops.
	private ServiceRegistration databaseServiceRegistration;
	
	public void start(BundleContext context) throws Exception {
		
		/*
		 * Tell Derby to look for an existing database or create a new database 
		 * in the default directory (within our application's directory)		 
		 */
		System.setProperty("derby.system.home", DATABASE_DIRECTORY);
		
		/*
		 * Make sure we don't have any leftover cruft in the db from previous sessions.
		 * This is our second chance, if the database wasn't properly cleaned out 
		 * in the stop() method on application exit.
		 * TODO: If we ever implement some kind of persistence we may not want to do this so bluntly
		 */
		removeAllNonSystemDatabaseTables();
		
		//allow the database service to be found by other services/plugins
		databaseServiceRegistration = context.registerService
			(DatabaseService.class.getName(), this, new Hashtable());
		
	}
	
	public void stop(BundleContext context)  {
			//disallow the database service to be found by other services/plugins
			this.databaseServiceRegistration.unregister();
			
			//try to clean out the database and shut it down.
			try {
				removeAllNonSystemDatabaseTables();
				String shutdownDatabaseCommand = 
					DERBY_PROTOCOL + DEFAULT_SHUTDOWN_CONNECTION_STRING;
				DriverManager.getConnection(shutdownDatabaseCommand);
			} catch (Exception e) {
				String message =
					"An unexpected exception occurred while shutting down the internal database." +
					"Aborting database shutdown process." +
					"Database may not be left in a valid state (but it will probably be okay).";
				throw new RuntimeException(message, e);
			}
	}
	
	//Return a link to a "database" that cannot interfere with other "databases" in the system.
	//TODO: May need to refactor to improve the terminology used surrounding this functionality.
	public DataSourceWithID createDatabase() throws DatabaseCreationException {
		return new DataSourceWithIDImpl(getDataSource());
	}
	
	
	private static final int SCHEMA_NAME_INDEX = 2;
	private static final int TABLE_NAME_INDEX = 3;
	private static final String NONSYSTEM_SCHEMA_NAME = "APP";
	
	private void removeAllNonSystemDatabaseTables() throws Exception {
			
		   DataSource db = getDataSource();
		   Connection dbConnection = db.getConnection();
		   DatabaseMetaData dbMetadata = dbConnection.getMetaData();
		   ResultSet allTableNames = dbMetadata.getTables(null, null, null, null);
		   
		   Statement removeTables = dbConnection.createStatement();
		   
		   while (allTableNames.next()) {
			   if (allTableNames.getString(SCHEMA_NAME_INDEX).indexOf(NONSYSTEM_SCHEMA_NAME) != -1) {
					 String removeTableSQL = 
						  "DROP TABLE " + 
						  NONSYSTEM_SCHEMA_NAME + "." + allTableNames.getString(TABLE_NAME_INDEX);
					 removeTables.addBatch(removeTableSQL);
			   }
		   }
		   
		   removeTables.executeBatch();	   
	}

	
	//TODO: It could be that we should give everyone different datasources instead of the same one
	private PoolingDataSource getDataSource() throws DatabaseCreationException
	{
		if (this.poolingDataSource == null) { initializePoolingDataSource(); };
		return poolingDataSource;    	
	}
	
	//lazy-load the pooling data source (implicitly creates the initial database connection)
	//TODO: Make it more clear where database connection is initially established
	private void initializePoolingDataSource() throws DatabaseCreationException {
		if (this.poolingDataSource != null) {
			return;
		}
		
		try {
    		//Load the database driver
    		Class.forName(DERBY_DRIVER_NAME);
    		
    		/*
    		 * TODO:We can use this later to check acceptsUrl for better error reporting.
    		 * Driver jdbcDriver = (Driver) Class.forName(driver).newInstance();
    		 */	
    		String newDatabaseConnectionURL = DERBY_PROTOCOL +
    										  DEFAULT_DB_NAME +
    										  DEFAULT_CREATE_CONNECTION_STRING;
    		
    		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory
    			(newDatabaseConnectionURL, null, null);
			GenericObjectPool connectionPool = new GenericObjectPool();
			KeyedObjectPoolFactory stmtPool = new GenericKeyedObjectPoolFactory(null);
			//(side-effects the connection-pool so it gets its connections from the connection factory (I think))
			new PoolableConnectionFactory(connectionFactory, connectionPool, stmtPool, null, false, true);
			this.poolingDataSource = new PoolingDataSource(connectionPool);			
		}
    	catch (ClassNotFoundException e) {
			throw new DatabaseCreationException
				("Database driver '" + DERBY_DRIVER_NAME + "' could not be found", e);
		}
	}
}
