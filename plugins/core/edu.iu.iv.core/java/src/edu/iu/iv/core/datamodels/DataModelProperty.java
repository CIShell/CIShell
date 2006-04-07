/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package edu.iu.iv.core.datamodels;

import edu.iu.iv.common.property.Property;


/**
 * Defines various standard Properties that DataModels should publish.
 *
 * @author Team IVC
 */
public interface DataModelProperty {
    
    /** The Label of the DataModel */
    public static final Property LABEL = new Property("Label", String.class, 1);
    
    /** 
     * The Parent DataModel of the DataModel.  This is used when a model
     * is derived from another DataModel, to show the hierarchical relationship
     * between them.  This Property can be null, signifying that the DataModel
     * was not derived from any DataModel, such as when loading a new DataModel
     * from a file. 
     * */
    public static final Property PARENT = new Property("Parent", DataModel.class, 2);    
    
    /**
     * The type of this DataModel. Various standard types are created as 
     * constants in the DataModelType class.  These can be used, or new
     * types can be introduced as needed by creating new DataModelTypes.
     */
    public static final Property TYPE = new Property("Type", DataModelType.class, 3);
    
    /**
     * Flag to determined if this DataModel has been modified and not saved since
     * the modification.  This is used to do things like notify the user before they
     * exit that a modified DataModel exists and see if they want to save it.
     */
    public static final Property MODIFIED = new Property("Modified", Boolean.class, 4);
}
