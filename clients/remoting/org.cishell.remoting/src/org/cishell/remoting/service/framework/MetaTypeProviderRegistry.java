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

import org.osgi.service.metatype.MetaTypeProvider;

public interface MetaTypeProviderRegistry {
    public static String SERVICE_NAME = "MetaTypeProviderRegistry";
    
    public Vector getLocales(String providerID);
    public String getObjectClassDefinition(String providerID, String id, String locale);
    
    public void unregisterMetaTypeProvider(String providerID);
    public String registerMetaTypeProvider(MetaTypeProvider provider);
    
    public MetaTypeProvider getMetaTypeProvider(String providerID);
}
