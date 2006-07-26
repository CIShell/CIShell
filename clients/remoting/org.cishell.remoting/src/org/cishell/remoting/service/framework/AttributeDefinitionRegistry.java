/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 3, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.remoting.service.framework;

import java.util.Vector;

import org.osgi.service.metatype.AttributeDefinition;

public interface AttributeDefinitionRegistry {
    public static String SERVICE_NAME = "AttributeDefinitionRegistry";
    
    public int getCardinality(String attrID);
    public Vector getDefaultValue(String attrID);
    public String getDescription(String attrID);
    public String getID(String attrID);
    public String getName(String attrID);
    public Vector getOptionLabels(String attrID);
    public Vector getOptionValues(String attrID);
    public int getType(String attrID);
    public String validate(String attrID, String value);
    
    
    public AttributeDefinition getAttributeDefinition(String attrID);
    public void unregisterAttributeDefinition(String attrID);
    public String registerAttributeDefinition(AttributeDefinition attr);
}
