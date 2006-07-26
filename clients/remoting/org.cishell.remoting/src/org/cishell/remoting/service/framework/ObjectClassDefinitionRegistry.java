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

import org.osgi.service.metatype.ObjectClassDefinition;

public interface ObjectClassDefinitionRegistry {
    public static String SERVICE_NAME = "ObjectClassDefinitionRegistry";
    
    public String getID(String ocdID);
    public String getName(String ocdID);
    public String getDescription(String ocdID);
    
    //public byte[] getIcon(long ocdID, int size);
    public Vector getAttributeDefinitions(String ocdID, int filter);
    
    //TODO: dictionary parsing
    
    public ObjectClassDefinition getObjectClassDefinition(String ocdID);
    public void unregisterObjectClassDefinition(String ocdID);
    public String registerObjectClassDefinition(ObjectClassDefinition ocd);
}
