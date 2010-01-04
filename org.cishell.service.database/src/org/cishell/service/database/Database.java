package org.cishell.service.database;

import java.sql.Connection;
import java.sql.SQLException;



public interface Database {
	public static final String DB_MIME_TYPE_PREFIX = "db:";
	public static final String GENERIC_DB_MIME_TYPE = "db:any";
	
	public Connection getConnection() throws SQLException;
	
	/**
	 * 
	 * @return the name of the schema where the non-system tables we are interested in reside.
	 */
	public String getApplicationSchemaName();
	
}