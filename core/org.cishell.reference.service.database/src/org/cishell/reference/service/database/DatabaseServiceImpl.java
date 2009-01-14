package org.cishell.reference.service.database;

import java.sql.SQLException;
import java.util.Dictionary;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.cishell.service.database.DatabaseCopyException;
import org.cishell.service.database.DatabaseCreationException;
import org.cishell.service.database.DatabaseService;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
public class DatabaseServiceImpl implements DatabaseService {

	//TODO: These variables should be abstracted out in a Preferences page at some point (I guess)
	private static final String DEFAULT_DRIVER_NAME = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String DEFAULT_CONNECTION_STRING_PREFIX = "jdbc:derby";
	private static final String DEFAULT_CONNECTION_STRING_SUFFIX = ";create=true";
	
	private static final String DEFAULT_DB_NAME = "ultra_sasquatch";
	//Give each db a unique (but meaningless) name by counting up from 0 for each new database
	//TODO: Using a rolling counter like this may have bad implications later on, but it's decent for now
	private static int dbNameCounter = 0;
	
	private LogService log; 
	
	protected void activate(ComponentContext ctxt) {

		this.log = (LogService) ctxt.locateService("LOG");
	}

	protected void deactivate(ComponentContext ctxt) {
	}
	
	private void ensureConnectionPoolIsEstablished() {
		try {
		Class.forName(DEFAULT_DRIVER_NAME);
		
		String newDatabaseName = DEFAULT_DB_NAME + Integer.toString(dbNameCounter);
		String newDatabaseConnectionURL = DEFAULT_CONNECTION_STRING_PREFIX + 
										  newDatabaseName +
										  DEFAULT_CONNECTION_STRING_SUFFIX;
		
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(newDatabaseConnectionURL, null, null);	
		GenericObjectPool connectionPool = new GenericObjectPool();	
		} catch (ClassNotFoundException e) {
			throw new AlgorithmExecutionException("Database driver not found: " + driver, e);
		}
	}

	public DataSource createDatabase() throws DatabaseCreationException {

    	javax.sql.DataSource dataSource;
    	
    	try {
    		Class.forName(DEFAULT_DRIVER_NAME);
			//Driver jdbcDriver = (Driver) Class.forName(driver).newInstance(); //we can use this later to check acceptsUrl for better error reporting
    		
    		String newDatabaseName = DEFAULT_DB_NAME + Integer.toString(dbNameCounter);
    		String newDatabaseConnectionURL = DEFAULT_CONNECTION_STRING_PREFIX + 
    										  newDatabaseName +
    										  DEFAULT_CONNECTION_STRING_SUFFIX;
    		
    		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(newDatabaseConnectionURL, null, null);	
			GenericObjectPool connectionPool = new GenericObjectPool();			
			dataSource = new PoolingDataSource(connectionPool);
			
			try {
				dataSource.getConnection().close();
			} catch (SQLException e) {
				logger.log(LogService.LOG_WARNING, "Problem opening test connection.", e);
			}
			
			//prefuseConnection = prefuse.data.io.sql.ConnectionFactory.getDatabaseConnection(connection);
			
			
			
		} catch (ClassNotFoundException e) {
			throw new AlgorithmExecutionException("Database driver not found: " + driver, e);
		}
	}
	
	public DataSource copyDatabase(DataSource database) throws DatabaseCopyException {
		// TODO Auto-generated method stub
		return null;
	}

}
