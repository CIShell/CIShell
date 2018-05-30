package org.cishell.templates.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Dictionary;

import javax.sql.DataSource;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.service.database.DatabaseCreationException;
import org.cishell.service.database.DatabaseService;

public abstract class SQLExecutionAlgorithm implements Algorithm {
	protected Data[] data;
	protected Dictionary parameters;
	protected CIShellContext context;
	
	protected DatabaseService databaseService;
    
    public SQLExecutionAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        this.data = data;
        this.parameters = parameters;
        this.context = context;
        
        this.databaseService = (DatabaseService)context.getService(DatabaseService.class.getName());
    }

    // TODO: Update the output to have proper contents.  (Since we're only using one
    // database now, the ACTUAL (wrapped DataSource) should be the same, but new
    // DataSourceWithIDs should be created as the output.)
    public Data[] execute() throws AlgorithmExecutionException {
    	System.err.println("this.databaseService: " + this.databaseService);
    	// Unpack the in-data as a data source.
    	DataSource dataSource = (DataSource)this.data[0].getData();
    	// Open a connection with the data source so we can perform SQL on the
    	// data source.
    	Connection dataSourceConnection = null;
    	// The statement is what will actually execute our SQL.
    	Statement sqlStatement = null;
    	
    	try {
    		dataSourceConnection = dataSource.getConnection();
    		sqlStatement = dataSourceConnection.createStatement();
    	}
    	catch (SQLException e) {
    		throw new AlgorithmExecutionException(e);
    	}
    	
    	String sqlStatementString = null;
    	
    	// Attempt to form our SQL query and update.
    	try {
    		sqlStatementString = formSQL();
    	}
    	catch (SQLFormationException sqlFormationException) {
    		throw new AlgorithmExecutionException(sqlFormationException);
    	}
    	
    	// Success at this point (with forming the SQL), so execute it if it is not
    	// empty.
    	if (!sqlStatement.equals("")) {
    		try {
    			// If execute returns true, there is at least one result set.  So,
    			// let's get those and turn them into data sources for our out-data
    			// YAY!
    			if (sqlStatement.execute(sqlStatementString)) {
    				// Used to store the list of
    				// result-sets-converted-to-new-databases that we're working on.
    				ArrayList resultSetsConvertedToNewDatabases = new ArrayList();
    				
    				do {
    					// Get the current result set.
    					ResultSet resultSet = sqlStatement.getResultSet();
    					
    					// Construct the new database out of the result set.
    					
    					DataSource newDatabase = null;
    					
    					/* try {
    						newDatabase = null; 
    							// this.databaseService.createDatabase(resultSet);
    					}
    					catch (DatabaseCreationException e) {
    						throw new AlgorithmExecutionException(e);
    					} */
    					
    					// Wrap the new database.
    					Data newDatabaseData = createOutDataFromDataSource(newDatabase);
    					
    					// Add the new out-data entry to our working list.
    					resultSetsConvertedToNewDatabases.add(newDatabaseData);
    				}
    				while (sqlStatement.getMoreResults());
    				
    				// Convert the ArrayList to a Data[].
    				Data[] finalResultSetsConvertedToNewDatabasesData =
    					new Data[resultSetsConvertedToNewDatabases.size()];
    					
    				finalResultSetsConvertedToNewDatabasesData =
    					(Data[])resultSetsConvertedToNewDatabases.toArray
    						(finalResultSetsConvertedToNewDatabasesData);
    					
    				return manipulateFinalOutData
    					(finalResultSetsConvertedToNewDatabasesData);
    			}
    		}
    		catch (SQLException sqlException) {
    			throw new AlgorithmExecutionException(sqlException);
    		}
    	}
    	
    	//
    	return null;
    }
    
    public String formSQL() throws SQLFormationException {
    	return "";
    }
    
    public Data createOutDataFromDataSource(DataSource dataSource) {
    	return new BasicData(dataSource, dataSource.getClass().getName());
    }
    
    public Data[] manipulateFinalOutData(Data[] outData) {
    	return outData;
    }
}