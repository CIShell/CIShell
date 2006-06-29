/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 15, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.framework.datamodel;



/**
 * Defines some standard DataModelTypes to be used in DataModel PropertyMaps
 * to declare the type of a DataModel, as well as allows for creation of
 * new types when needed.
 *
 * @author Team IVC
 */
public class DataModelType {        
    
    /** Matrix data model */
    public static DataModelType MATRIX = new DataModelType("Matrix");
    
    /** Network data model */
    public static DataModelType NETWORK = new DataModelType("Network");
    
    /** Tree data model */
    public static DataModelType TREE = new DataModelType("Tree");
    
    /** Unknown data model */
    public static DataModelType OTHER = new DataModelType("Unknown");       
    
    private String name;
    
    /**
     * Creates a new DataModelType object.
     *
     * @param name the name of this DataModelType
     */
    public DataModelType(String name) {
        this.name = name;
    }

    /**
     * Returns the name of this DataModelType.
     *
     * @return the name of this DataModelType
     */
    public String getName() {
        return name;
    }
    
}
