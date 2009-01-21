package org.cishell.reference.service.database;

import java.sql.SQLException;
import java.util.Hashtable;

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
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;

public class DatabaseServiceImpl implements DatabaseService, BundleActivator {
	/* TODO: These variables should be abstracted out in a Preferences page at some
	   point (I guess).
	*/
	private static final String DEFAULT_DRIVER_NAME =
		"org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DEFAULT_PROTOCOL = "jdbc:derby:";
	private static final String DEFAULT_CREATE_CONNECTION_STRING = ";create=true";
	private static final String DEFAULT_DB_NAME = "ultra_sasquatch";
	
	// Give each db a unique (but meaningless) name by counting up from 0 for each
	// new database.
	/* TODO: Using a rolling counter like this may have bad implications later on,
	   but it's decent for now.
	 */
	private static int dbNameCounter = 0;
	
	private ServiceRegistration databaseServiceRegistration;
	
	private PoolingDataSource poolingDataSource = null;
	// TODO: Needed?  I just want to make sure nothing goes wrong for now.
	private DataSourceWithID myDataSource = null;
	
	private String driver;
	
	public void start(BundleContext context) throws Exception {
		this.driver = DEFAULT_DRIVER_NAME;
		
		System.err.println("starting!");
		
		// Register me as a service! (This doesn't work?)
		databaseServiceRegistration = context.registerService
			(DatabaseService.class.getName(), this, new Hashtable());
		
		// Get MY data source!  It's mine, and you can't have it!
		try {
			myDataSource = createDatabase();
		}
		catch (DatabaseCreationException e) {
			System.err.println(":'( " + e.getMessage());
			throw e;
		}
		
		System.err.println("meep?");
	}

	public void stop(BundleContext context) throws Exception {
	}
	
	// If one hasn't been created yet, create a connection pool and return it.
	private PoolingDataSource getConnectionPool() throws DatabaseCreationException
	{
		if (poolingDataSource != null)
			return poolingDataSource;
		
    	try {
    		System.err.println("Loading driver");
    		// This loads the database driver.
    		Class.forName(DEFAULT_DRIVER_NAME);
    		
    		// We can use this later to check acceptsUrl for better error reporting.
			// Driver jdbcDriver = (Driver) Class.forName(driver).newInstance();
    		
    		String newDatabaseName =
    			DEFAULT_DB_NAME + Integer.toString(dbNameCounter);
    		String newDatabaseConnectionURL = DEFAULT_PROTOCOL +
    										  newDatabaseName +
    										  DEFAULT_CREATE_CONNECTION_STRING;
    		
    		System.err.println("connection url: " + newDatabaseConnectionURL);
    		
    		// This connection factory actually uses the loaded database driver to
    		// generate connections.
    		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory
    			(newDatabaseConnectionURL, null, null);
    		
    		// This is a generic object pool.  It must be linked to a poolable
    		// object factory (PoolableObjectFactory), which the new
    		// PoolableConnectionFactory below is.
			GenericObjectPool connectionPool = new GenericObjectPool();
			
			// Not sure what this does?
			KeyedObjectPoolFactory stmtPool = new GenericKeyedObjectPoolFactory(null);
			
			// This is a poolable object factory (PoolableObjectFactory) used to
			// create connections for an object pool.  It is the glue between the
			// connection factory and the object pool.
			// It links itself up to the connect pool inside its constructor, which
			// is why it's not assigned to a variable out here.
			new PoolableConnectionFactory(connectionFactory, connectionPool, stmtPool, null, false, true);
			
			// Finally, create the connection pool.
			poolingDataSource = new PoolingDataSource(connectionPool);
			
			// TODO: Remove this?
			// Make sure we can get a connection from the connection pool.
			try {
				poolingDataSource.getConnection().close();
			}
			catch (SQLException e) {
				throw new DatabaseCreationException
					("Could not properly initiate database.", e);
			}
		}
    	catch (ClassNotFoundException e) {
			throw new DatabaseCreationException
				("Database driver (" + driver + ") could not be found", e);
		}
    	
    	return poolingDataSource;
	}

	public DataSourceWithID createDatabase() throws DatabaseCreationException {
		return new DataSourceWithID(getConnectionPool());
	}
}
