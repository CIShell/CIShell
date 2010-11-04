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
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework.data;


/**
 * Standard property keys and values to use when creating metadata for a 
 * {@link Data} object
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface DataProperty {
    /** 
     * The label to give the Data object if displayed. The type associated with
     * this property is of type {@link String}.
     */
    public static final String LABEL = "Label";
    
    /** 
     * A short label to give the Data object for shorter displays. It is 
     * recommended to keep the string length below 20 characters. This will often
     * be used for recommended file names when saving the data to disk. The type 
     * associated with this property is of type {@link String}.
     */
    public static final String SHORT_LABEL = "Short_Label";
    
    /** 
     * The parent Data object of the Data object. This is used when a Data object
     * is derived from another Data object to show the hierarchical relationship
     * between them. This property can be null, signifying that the Data object
     * was not derived from any other Data object, such as when loading a new Data
     * object from a file. The type associated with this property is of type 
     * {@link Data}.
     */
    public static final String PARENT = "Parent";    
    
    /**
     * The general type of the Data object. Various standard types are created as 
     * constants with name *_TYPE from this class. These can be used, or new
     * types can be introduced as needed. The type associated with this 
     * property is of type {@link String}.
     */
    public static final String TYPE = "Type";
    
    /**
     * Flag to determine if the Data object has been modified and not saved since
     * the modification. This is used to do things like notify the user before 
     * they exit that a modified Data object exists and ask if they want to save 
     * it. The type associated with this property is of type {@link Boolean}.
     */
    public static final String MODIFIED = "Modified";
    
    /** Says this data model is abstractly a matrix */
    public static String MATRIX_TYPE = "Matrix";
    
    /** Says this data model is abstractly a network */
    public static String NETWORK_TYPE = "Network";

    /** Says this data model is abstractly a table */
    public static String TABLE_TYPE = "Table";
    
    /** Says this data model is abstractly a tree */
    public static String TREE_TYPE = "Tree";
    
    /** Says this data model is abstractly an unknown type */
    public static String OTHER_TYPE = "Unknown";  
    
    /** Says this data model is abstractly a plain text file */
    public static String TEXT_TYPE = "Text";
    
    /** Says this data model is abstractly a data plot */
    public static String PLOT_TYPE = "Plot";
    
    /** Says this data model is abstractly a database */
    public static String DATABASE_TYPE = "Database";
    
    /** Says this data model is a PostScript file */
    public static String VECTOR_IMAGE_TYPE = "Vector Image";
    
    /** Says this data model is a JPEG object */
    public static String RASTER_IMAGE_TYPE = "Raster Image";
    
    /** Says this data model is a 'model' object */
    public static String MODEL_TYPE = "Model";

    /** Says this data model is an 'R instance' object */
    public static String R_INSTANCE_TYPE = "R Instance";
}
