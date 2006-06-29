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



public interface DataModelProperty {
    /** The Label of the DataModel */
    public static final String LABEL = "Label";
    
    /** 
     * The Parent DataModel of the DataModel.  This is used when a model
     * is derived from another DataModel, to show the hierarchical relationship
     * between them.  This Property can be null, signifying that the DataModel
     * was not derived from any DataModel, such as when loading a new DataModel
     * from a file. 
     * */
    public static final String PARENT = "Parent";    
    
    /**
     * The type of this DataModel. Various standard types are created as 
     * constants in the DataModelType class.  These can be used, or new
     * types can be introduced as needed by creating new DataModelTypes.
     */
    public static final String TYPE = "Type";
    
    /**
     * Flag to determined if this DataModel has been modified and not saved since
     * the modification.  This is used to do things like notify the user before they
     * exit that a modified DataModel exists and see if they want to save it.
     */
    public static final String MODIFIED = "Modified";
}
