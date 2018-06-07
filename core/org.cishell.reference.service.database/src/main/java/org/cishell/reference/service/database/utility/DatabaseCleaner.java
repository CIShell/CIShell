package org.cishell.reference.service.database.utility;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//taken from http://svn.apache.org/viewvc/db/derby/code/trunk/java/testing/org/apache/derbyTesting/junit/CleanDatabaseTestSetup.java?view=markup
/*
 *
 * Derby - Class org.apache.derbyTesting.functionTests.util.CleanDatabase
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

public class DatabaseCleaner {


    /**
     * Clean a complete database
     * @param conn Connection to be used, must not be in auto-commit mode.
     * @param compress True if selected system tables are to be compressed
     * to avoid potential ordering differences in test output.
     * @throws SQLException database error
     */
     public static void cleanDatabase(Connection conn, boolean compress) throws SQLException {
         clearProperties(conn);
         removeObjects(conn);
         if (compress)
             compressObjects(conn);
         //removeRoles(conn);
     }
     
     /**
      * Set of database properties that will be set to NULL (unset)
      * as part of cleaning a database.
      */
     private static final String[] CLEAR_DB_PROPERTIES =
     {
         "derby.database.classpath",
     };
     
     /**
      * Clear all database properties.
      */
     private static void clearProperties(Connection conn) throws SQLException {

         PreparedStatement ps = conn.prepareCall(
           "CALL SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY(?, NULL)");
         
         for (int i = 0; i < CLEAR_DB_PROPERTIES.length; i++)
         {
             ps.setString(1, CLEAR_DB_PROPERTIES[i]);
             ps.executeUpdate();
         }
         ps.close();
         conn.commit();
     }
     
     
     /**
      * Remove all objects in all schemas from the database.
      */
     private static void removeObjects(Connection conn) throws SQLException {
   
        DatabaseMetaData dmd = conn.getMetaData();

        SQLException sqle = null;
        // Loop a number of arbitary times to catch cases
        // where objects are dependent on objects in
        // different schemas.
        for (int count = 0; count < 5; count++) {
            // Fetch all the user schemas into a list
            List schemas = new ArrayList();
            ResultSet rs = dmd.getSchemas();
            while (rs.next()) {
    
                String schema = rs.getString("TABLE_SCHEM");
                if (schema.startsWith("SYS"))
                    continue;
                if (schema.equals("SQLJ"))
                    continue;
                if (schema.equals("NULLID"))
                    continue;
    
                schemas.add(schema);
            }
            rs.close();
    
            // DROP all the user schemas.
            sqle = null;
            for (Iterator i = schemas.iterator(); i.hasNext();) {
                String schema = (String) i.next();
                try {
                    JDBC.dropSchema(dmd, schema);
                } catch (SQLException e) {
                    sqle = e;
                }
            }
            // No errors means all the schemas we wanted to
            // drop were dropped, so nothing more to do.
            if (sqle == null)
                return;
        }
        throw sqle;
    }

    private static void removeRoles(Connection conn) throws SQLException {
        // No metadata for roles, so do a query against SYSROLES
        Statement stm = conn.createStatement();
        Statement dropStm = conn.createStatement();

        // cast to overcome territory differences in some cases:
        ResultSet rs = stm.executeQuery(
            "select roleid from sys.sysroles where " +
            "cast(isdef as char(1)) = 'Y'");

        while (rs.next()) {
            dropStm.executeUpdate("DROP ROLE " + JDBC.escape(rs.getString(1)));
        }

        stm.close();
        dropStm.close();
        conn.commit();
    }

     /**
      * Set of objects that will be compressed as part of cleaning a database.
      */
     private static final String[] COMPRESS_DB_OBJECTS =
     {
         "SYS.SYSDEPENDS",
     };
     
     /**
      * Compress the objects in the database.
      * 
      * @param conn the db connection
      * @throws SQLException database error
      */
     private static void compressObjects(Connection conn) throws SQLException {
    	 
    	 CallableStatement cs = conn.prepareCall
    	     ("CALL SYSCS_UTIL.SYSCS_INPLACE_COMPRESS_TABLE(?, ?, 1, 1, 1)");
    	 
    	 for (int i = 0; i < COMPRESS_DB_OBJECTS.length; i++)
    	 {
    		 int delim = COMPRESS_DB_OBJECTS[i].indexOf(".");
             cs.setString(1, COMPRESS_DB_OBJECTS[i].substring(0, delim) );
             cs.setString(2, COMPRESS_DB_OBJECTS[i].substring(delim+1) );
             cs.execute();
    	 }
    	 
    	 cs.close();
    	 conn.commit();
     }
     
     
    	   
}