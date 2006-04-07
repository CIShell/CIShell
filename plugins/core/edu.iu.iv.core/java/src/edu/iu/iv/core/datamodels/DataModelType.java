/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 17, 2005 at Indiana University.
 */
package edu.iu.iv.core.datamodels;



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
