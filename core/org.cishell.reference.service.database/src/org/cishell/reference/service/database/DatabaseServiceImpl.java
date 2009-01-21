package org.cishell.reference.service.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.service.database.DataSourceWithID;
import org.cishell.service.database.DatabaseCopyException;
import org.cishell.service.database.DatabaseCreationException;
import org.cishell.service.database.DatabaseService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

public class DatabaseServiceImpl implements DatabaseService {
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
	
	private static PoolingDataSource poolingDataSource = null;
	
	private String driver;
	private LogService logger;
	
	protected void activate(ComponentContext ctxt) {
		this.driver = DEFAULT_DRIVER_NAME;
		this.logger = (LogService)ctxt.locateService("LOG");
	}

	protected void deactivate(ComponentContext ctxt) {
	}
	
	// If one hasn't been created yet, create a connection pool and return it.
	private PoolingDataSource getConnectionPool() throws AlgorithmExecutionException
	{
		if (poolingDataSource != null)
			return poolingDataSource;
		
    	try {
    		// This loads the database driver.
    		Class.forName(DEFAULT_DRIVER_NAME);
    		
    		// We can use this later to check acceptsUrl for better error reporting.
			// Driver jdbcDriver = (Driver) Class.forName(driver).newInstance();
    		
    		String newDatabaseName =
    			DEFAULT_DB_NAME + Integer.toString(dbNameCounter);
    		String newDatabaseConnectionURL = DEFAULT_PROTOCOL +
    										  newDatabaseName +
    										  DEFAULT_CREATE_CONNECTION_STRING;
    		
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
				this.logger.log(LogService.LOG_WARNING,
								"Problem opening test connection.",
								e);
				
				throw new AlgorithmExecutionException
					("Could not properly initiate database.", e);
			}
		}
    	catch (ClassNotFoundException e) {
			throw new AlgorithmExecutionException
				("Database driver (" + driver + ") could not be found", e);
		}
    	
    	return poolingDataSource;
	}

	public DataSourceWithID createDatabase() throws DatabaseCreationException {
		DataSourceWithID dataSource = null;
    	
    	return dataSource;
	}
	
	public DataSourceWithID copyDatabase(DataSourceWithID database)
			throws DatabaseCopyException {
		// TODO Auto-generated method stub.
		return null;
	}

	public DataSourceWithID createDatabase(ResultSet resultSet)
			throws DatabaseCreationException {
		// TODO Auto-generated method stub
		return null;
	}
}
