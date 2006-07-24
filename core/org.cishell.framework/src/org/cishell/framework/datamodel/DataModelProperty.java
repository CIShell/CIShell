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
package org.cishell.framework.datamodel;



/**
 * Standard property keys to use when creating meta-data for a 
 * {@link DataModel}.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface DataModelProperty {
    /** 
     * The label to give the DataModel if displayed. The type associated with
     * this property is of type {@link String}.
     */
    public static final String LABEL = "Label";
    
    /** 
     * The parent DataModel of the DataModel. This is used when a DataModel
     * is derived from another DataModel to show the hierarchical relationship
     * between them.  This property can be null, signifying that the DataModel
     * was not derived from any DataModel, such as when loading a new DataModel
     * from a file. The type associated with this property is of type 
     * {@link DataModel} 
     */
    public static final String PARENT = "Parent";    
    
    //TODO: should we consider removing this/changing it?
    /**
     * The type of the DataModel. Various standard types are created as 
     * constants in the {@link DataModelType} class. These can be used, or new
     * types can be introduced as needed. The type associated with this 
     * property is of type {@link String}.
     */
    public static final String TYPE = "Type";
    
    /**
     * Flag to determine if this DataModel has been modified and not saved since
     * the modification. This is used to do things like notify the user before 
     * they exit that a modified DataModel exists and see if they want to save 
     * it. The type associated with this property is of type {@link Boolean}.
     */
    public static final String MODIFIED = "Modified";
}
