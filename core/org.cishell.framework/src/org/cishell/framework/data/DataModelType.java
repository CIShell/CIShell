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
 * Defines some standard data model types to be used in a Data's meta-data
 * to declare the type of a {@link Data}. These are the values associated 
 * with the {@link DataModelProperty}, DataModelProperty.GENERAL_TYPE. These 'types' are 
 * more general and are used to help a user to figure out in a broad sense what 
 * type of data he or she is looking at.
 *
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface DataModelType {        
    
    /** Says this data model is abstractly a matrix */
    public static String MATRIX = "Matrix";
    
    /** Says this data model is abstractly a network */
    public static String NETWORK = "Network";
    
    /** Says this data model is abstractly a tree */
    public static String TREE = "Tree";
    
    /** Says this data model is abstractly an unknown type */
    public static String OTHER = "Unknown";           
}
