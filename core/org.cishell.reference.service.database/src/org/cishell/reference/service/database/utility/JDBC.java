package org.cishell.reference.service.database.utility;

//taken from http://svn.apache.org/viewvc/db/derby/code/trunk/java/testing/org/apache/derbyTesting/junit/CleanDatabaseTestSetup.java?view=markup

/*
 *
 * Derby - Class org.apache.derbyTesting.junit.JDBC
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, 
 * software distributed under the License is distributed on an 
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific 
 * language governing permissions and limitations under the License.
 */

import java.io.IOException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Locale;


/**
 * JDBC utility methods for the JUnit tests.
 * Note that JSR 169 is a subset of JDBC 3 and
 * JDBC 3 is a subset of JDBC 4.
 * The base level for the Derby tests is JSR 169.
 */
public class JDBC {
    
    /**
     * Helper class whose <code>equals()</code> method returns
     * <code>true</code> for all strings on this format: SQL061021105830900
     */
    public static class GeneratedId {
        public boolean equals(Object o) {
            // unless JSR169, use String.matches...
            if (JDBC.vmSupportsJDBC3()) 
            {
                return o instanceof String &&
                ((String) o).matches("SQL[0-9]{15}");
            }
            else
            {
                String tmpstr = (String)o;
                boolean b = true;
                if (!(o instanceof String))
                    b = false;
                if (!(tmpstr.startsWith("SQL")))
                    b = false;
                if (tmpstr.length() != 18)
                    b = false;
                for (int i=3 ; i<18 ; i++)
                {
                    if (Character.isDigit(tmpstr.charAt(i)))
                        continue;
                    else
                    {
                        b = false;
                        break;
                    }
                }
            return b;
            }
        }
        public String toString() {
            return "xxxxGENERATED-IDxxxx";
        }
    }

    /**
     * Constant to pass to DatabaseMetaData.getTables() to fetch
     * just tables.
     */
    public static final String[] GET_TABLES_TABLE = new String[] {"TABLE"};
    /**
     * Constant to pass to DatabaseMetaData.getTables() to fetch
     * just views.
     */
    public static final String[] GET_TABLES_VIEW = new String[] {"VIEW"};
    /**
     * Constant to pass to DatabaseMetaData.getTables() to fetch
     * just synonyms.
     */
    public static final String[] GET_TABLES_SYNONYM =
        new String[] {"SYNONYM"};
    
    /**
     * Types.SQLXML value without having to compile with JDBC4.
     */
    public static final int SQLXML = 2009;
	
    /**
     * Tell if we are allowed to use DriverManager to create database
     * connections.
     */
    private static final boolean HAVE_DRIVER
                           = haveClass("java.sql.Driver");
    
    /**
     * Does the Savepoint class exist, indicates
     * JDBC 3 (or JSR 169). 
     */
    private static final boolean HAVE_SAVEPOINT
                           = haveClass("java.sql.Savepoint");

    /**
     * Does the java.sql.SQLXML class exist, indicates JDBC 4. 
     */
    private static final boolean HAVE_SQLXML
                           = haveClass("java.sql.SQLXML");

    /**
     * Can we load a specific class, use this to determine JDBC level.
     * @param className Class to attempt load on.
     * @return true if class can be loaded, false otherwise.
     */
    static boolean haveClass(String className)
    {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable e) {
        	return false;
        }    	
    }
 	/**
	 * Return true if the virtual machine environment
	 * supports JDBC4 or later. JDBC 4 is a superset
     * of JDBC 3 and of JSR169.
     * <BR>
     * This method returns true in a JDBC 4 environment
     * and false in a JDBC 3 or JSR 169 environment.
	 */
	public static boolean vmSupportsJDBC4()
	{
		return HAVE_DRIVER
	       && HAVE_SQLXML;
	}
 	/**
	 * Return true if the virtual machine environment
	 * supports JDBC3 or later. JDBC 3 is a super-set of JSR169
     * and a subset of JDBC 4.
     * <BR>
     * This method will return true in a JDBC 3 or JDBC 4
     * environment, but false in a JSR169 environment.
	 */
	public static boolean vmSupportsJDBC3()
	{
		return HAVE_DRIVER
		       && HAVE_SAVEPOINT;
	}

	/**
	 * Return true if the virtual machine environment
	 * supports JSR169. JSR169 is a subset of JDBC 3
     * and hence a subset of JDBC 4 as well.
     * <BR>
     * This method returns true only in a JSR 169
     * environment.
	 */
	public static boolean vmSupportsJSR169()
	{
		return !HAVE_DRIVER
		       && HAVE_SAVEPOINT;
	}	
	
	/**
	 * Rollback and close a connection for cleanup.
	 * Test code that is expecting Connection.close to succeed
	 * normally should just call conn.close().
	 * 
	 * <P>
	 * If conn is not-null and isClosed() returns false
	 * then both rollback and close will be called.
	 * If both methods throw exceptions
	 * then they will be chained together and thrown.
	 * @throws SQLException Error closing connection.
	 */
	public static void cleanup(Connection conn) throws SQLException
	{
		if (conn == null)
			return;
		if (conn.isClosed())
			return;
		
		SQLException sqle = null;
		try {
			conn.rollback();
		} catch (SQLException e) {
			sqle = e;
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			if (sqle == null)
			    sqle = e;
			else
				sqle.setNextException(e);
			throw sqle;
		}
	}
	
	/**
	 * Drop a database schema by dropping all objects in it
	 * and then executing DROP SCHEMA. If the schema is
	 * APP it is cleaned but DROP SCHEMA is not executed.
	 * 
	 * TODO: Handle dependencies by looping in some intelligent
	 * way until everything can be dropped.
	 * 

	 * 
	 * @param dmd DatabaseMetaData object for database
	 * @param schema Name of the schema
	 * @throws SQLException database error
	 */
	public static void dropSchema(DatabaseMetaData dmd, String schema) throws SQLException
	{		
		Connection conn = dmd.getConnection();
		Statement s = dmd.getConnection().createStatement();
        
        // Functions - not supported by JDBC meta data until JDBC 4
        // Need to use the CHAR() function on A.ALIASTYPE
        // so that the compare will work in any schema.
        PreparedStatement psf = conn.prepareStatement(
                "SELECT ALIAS FROM SYS.SYSALIASES A, SYS.SYSSCHEMAS S" +
                " WHERE A.SCHEMAID = S.SCHEMAID " +
                " AND CHAR(A.ALIASTYPE) = ? " +
                " AND S.SCHEMANAME = ?");
        psf.setString(1, "F" );
        psf.setString(2, schema);
        ResultSet rs = psf.executeQuery();
        dropUsingDMD(s, rs, schema, "ALIAS", "FUNCTION");        

		// Procedures
		rs = dmd.getProcedures((String) null,
				schema, (String) null);
		
		dropUsingDMD(s, rs, schema, "PROCEDURE_NAME", "PROCEDURE");
		
		// Views
		rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_VIEW);
		
		dropUsingDMD(s, rs, schema, "TABLE_NAME", "VIEW");
		
		// Tables
		rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_TABLE);
		
		dropUsingDMD(s, rs, schema, "TABLE_NAME", "TABLE");
        
        // At this point there may be tables left due to
        // foreign key constraints leading to a dependency loop.
        // Drop any constraints that remain and then drop the tables.
        // If there are no tables then this should be a quick no-op.
        ResultSet table_rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_TABLE);

        while (table_rs.next()) {
            String tablename = table_rs.getString("TABLE_NAME");
            rs = dmd.getExportedKeys((String) null, schema, tablename);
            while (rs.next()) {
                short keyPosition = rs.getShort("KEY_SEQ");
                if (keyPosition != 1)
                    continue;
                String fkName = rs.getString("FK_NAME");
                // No name, probably can't happen but couldn't drop it anyway.
                if (fkName == null)
                    continue;
                String fkSchema = rs.getString("FKTABLE_SCHEM");
                String fkTable = rs.getString("FKTABLE_NAME");

                String ddl = "ALTER TABLE " +
                    JDBC.escape(fkSchema, fkTable) +
                    " DROP FOREIGN KEY " +
                    JDBC.escape(fkName);
                s.executeUpdate(ddl);
            }
            rs.close();
        }
        table_rs.close();
        conn.commit();
                
        // Tables (again)
        rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_TABLE);        
        dropUsingDMD(s, rs, schema, "TABLE_NAME", "TABLE");

        // drop UDTs
        psf.setString(1, "A" );
        psf.setString(2, schema);
        rs = psf.executeQuery();
        dropUsingDMD(s, rs, schema, "ALIAS", "TYPE");        
        psf.close();
  
        // Synonyms - need work around for DERBY-1790 where
        // passing a table type of SYNONYM fails.
        rs = dmd.getTables((String) null, schema, (String) null,
                GET_TABLES_SYNONYM);
        
        dropUsingDMD(s, rs, schema, "TABLE_NAME", "SYNONYM");
                
        // sequences
        if ( sysSequencesExists( conn ) )
        {
            psf = conn.prepareStatement
                (
                 "SELECT SEQUENCENAME FROM SYS.SYSSEQUENCES A, SYS.SYSSCHEMAS S" +
                 " WHERE A.SCHEMAID = S.SCHEMAID " +
                 " AND S.SCHEMANAME = ?");
            psf.setString(1, schema);
            rs = psf.executeQuery();
            dropUsingDMD(s, rs, schema, "SEQUENCENAME", "SEQUENCE");
            psf.close();
        }

		// Finally drop the schema if it is not APP
		if (!schema.equals("APP")) {
			s.executeUpdate("DROP SCHEMA " + JDBC.escape(schema) + " RESTRICT");
		}
		conn.commit();
		s.close();
	}

    /**
     * Return true if the SYSSEQUENCES table exists.
     */
    private static boolean sysSequencesExists( Connection conn ) throws SQLException
    {
        PreparedStatement ps = null;
        ResultSet rs =  null;
        try {
            ps = conn.prepareStatement
                (
                 "select count(*) from sys.systables t, sys.sysschemas s\n" +
                 "where t.schemaid = s.schemaid\n" +
                 "and ( cast(s.schemaname as varchar(128)))= 'SYS'\n" +
                 "and ( cast(t.tablename as varchar(128))) = 'SYSSEQUENCES'" );
            rs = ps.executeQuery();
            rs.next();
            return ( rs.getInt( 1 ) > 0 );
        }
        finally
        {
            if ( rs != null ) { rs.close(); }
            if ( ps != null ) { ps.close(); }
        }
    }
	
	/**
	 * DROP a set of objects based upon a ResultSet from a
	 * DatabaseMetaData call.
	 * 
	 * TODO: Handle errors to ensure all objects are dropped,
	 * probably requires interaction with its caller.
	 * 
	 * @param s Statement object used to execute the DROP commands.
	 * @param rs DatabaseMetaData ResultSet
	 * @param schema Schema the objects are contained in
	 * @param mdColumn The column name used to extract the object's
	 * name from rs
	 * @param dropType The keyword to use after DROP in the SQL statement
	 * @throws SQLException database errors.
	 */
	private static void dropUsingDMD(
			Statement s, ResultSet rs, String schema,
			String mdColumn,
			String dropType) throws SQLException
	{
		String dropLeadIn = "DROP " + dropType + " ";
		
        // First collect the set of DROP SQL statements.
        ArrayList ddl = new ArrayList();
		while (rs.next())
		{
            String objectName = rs.getString(mdColumn);
            String raw = dropLeadIn + JDBC.escape(schema, objectName);
            if ( "TYPE".equals( dropType ) ) { raw = raw + " restrict "; }
            ddl.add( raw );
		}
		rs.close();
        if (ddl.isEmpty())
            return;
                
        // Execute them as a complete batch, hoping they will all succeed.
        s.clearBatch();
        int batchCount = 0;
        for (Iterator i = ddl.iterator(); i.hasNext(); )
        {
            Object sql = i.next();
            if (sql != null) {
                s.addBatch(sql.toString());
                batchCount++;
            }
        }

		int[] results;
        boolean hadError;
		try {
		    results = s.executeBatch();
            hadError = false;
		} catch (BatchUpdateException batchException) {
			results = batchException.getUpdateCounts();
            hadError = true;
		}
		
        // Remove any statements from the list that succeeded.
		boolean didDrop = false;
		for (int i = 0; i < results.length; i++)
		{
			int result = results[i];
			if (result == Statement.EXECUTE_FAILED)
				hadError = true;
			else if (result == Statement.SUCCESS_NO_INFO || result >= 0) {
				didDrop = true;
				ddl.set(i, null);
			}
		}
        s.clearBatch();
        if (didDrop) {
            // Commit any work we did do.
            s.getConnection().commit();
        }

        // If we had failures drop them as individual statements
        // until there are none left or none succeed. We need to
        // do this because the batch processing stops at the first
        // error. This copes with the simple case where there
        // are objects of the same type that depend on each other
        // and a different drop order will allow all or most
        // to be dropped.
        if (hadError) {
            do {
                hadError = false;
                didDrop = false;
                for (ListIterator i = ddl.listIterator(); i.hasNext();) {
                    Object sql = i.next();
                    if (sql != null) {
                        try {
                            s.executeUpdate(sql.toString());
                            i.set(null);
                            didDrop = true;
                        } catch (SQLException e) {
                            hadError = true;
                        }
                    }
                }
                if (didDrop)
                    s.getConnection().commit();
            } while (hadError && didDrop);
        }
	}
    /**
     * Convert byte array to String.
     * Each byte is converted to a hexadecimal string representation.
     *
     * @param ba Byte array to be converted.
     * @return Hexadecimal string representation. Returns null on null input.
     */
    private static String bytesToString(byte[] ba)
    {
        if (ba == null) return null;
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < ba.length; ++i) {
            s.append(Integer.toHexString(ba[i] & 0x00ff));
        }
        return s.toString();
    }

	/**
	 * Escape a non-qualified name so that it is suitable
	 * for use in a SQL query executed by JDBC.
	 */
	public static String escape(String name)
	{
        StringBuffer buffer = new StringBuffer(name.length() + 2);
        buffer.append('"');
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            // escape double quote characters with an extra double quote
            if (c == '"') buffer.append('"');
            buffer.append(c);
        }
        buffer.append('"');
        return buffer.toString();
	}	


	/**
     * Compress 2 adjacent (single or double) quotes into a single (s or d)
     * quote when found in the middle of a String.
     *
     * NOTE:  """" or '''' will be compressed into "" or ''.
     * This function assumes that the leading and trailing quote from a
     * string or delimited identifier have already been removed.
     * @param source string to be compressed
     * @param quotes string containing two single or double quotes.
     * @return String where quotes have been compressed
     */
    private static String compressQuotes(String source, String quotes)
    {
        String  result = source;
        int         index;

        /* Find the first occurrence of adjacent quotes. */
        index = result.indexOf(quotes);

        /* Replace each occurrence with a single quote and begin the
         * search for the next occurrence from where we left off.
         */
        while (index != -1) {
            result = result.substring(0, index + 1) +
                     result.substring(index + 2);
            index = result.indexOf(quotes, index + 1);
        }

        return result;
    }


    /**
     * Convert a SQL identifier to case normal form.
     *
     * Normalize a SQL identifer, up-casing if <regular identifer>,
     * and handling of <delimited identifer> (SQL 2003, section 5.2).
     * The normal form is used internally in Derby.
     */
    public static String identifierToCNF(String id)
    {
        if (id == null || id.length() == 0) {
            return id;
        }

        if (id.charAt(0) == '"' &&
                id.length() >= 3   &&
                id.charAt(id.length() - 1) == '"') {
            // assume syntax is OK, thats is, any quotes inside are doubled:

            return compressQuotes(
                id.substring(1, id.length() - 1), "\"\"");

        } else {
            return id.toUpperCase(Locale.ENGLISH);
        }
    }


    /**
	 * Escape a schama-qualified name so that it is suitable
	 * for use in a SQL query executed by JDBC.
	 */
	public static String escape(String schema, String name)
	{
        return escape(schema) + "." + escape(name);
	}
         
        /**
         * Return Type name from jdbc type
         * 
         * @param jdbcType  jdbc type to translate
         */
        public static String sqlNameFromJdbc(int jdbcType) {
            switch (jdbcType) {
            case Types.BIT          :  return "Types.BIT";
            case Types.BOOLEAN  : return "Types.BOOLEAN";
            case Types.TINYINT      :  return "Types.TINYINT";
            case Types.SMALLINT     :  return "SMALLINT";
            case Types.INTEGER      :  return "INTEGER";
            case Types.BIGINT       :  return "BIGINT";
            
            case Types.FLOAT        :  return "Types.FLOAT";
            case Types.REAL         :  return "REAL";
            case Types.DOUBLE       :  return "DOUBLE";
            
            case Types.NUMERIC      :  return "Types.NUMERIC";
            case Types.DECIMAL      :  return "DECIMAL";
            
            case Types.CHAR         :  return "CHAR";
            case Types.VARCHAR      :  return "VARCHAR";
            case Types.LONGVARCHAR  :  return "LONG VARCHAR";
            case Types.CLOB         :  return "CLOB";
            
            case Types.DATE         :  return "DATE";
            case Types.TIME         :  return "TIME";
            case Types.TIMESTAMP    :  return "TIMESTAMP";
            
            case Types.BINARY       :  return "CHAR () FOR BIT DATA";
            case Types.VARBINARY    :  return "VARCHAR () FOR BIT DATA";
            case Types.LONGVARBINARY:  return "LONG VARCHAR FOR BIT DATA";
            case Types.BLOB         :  return "BLOB";

            case Types.OTHER        :  return "Types.OTHER";
            case Types.NULL         :  return "Types.NULL";
            default : return String.valueOf(jdbcType);
                }
        }

       
}

