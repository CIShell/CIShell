/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 10, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.remoting.service.framework;

import java.util.Vector;


public interface CIShellFramework {
    public static String SERVICE_NAME = "CIShellFramework";
    
    public String createSession(String clientURL);
    public void closeSession(String sessionID);
    public Vector getAlgorithmFactories();
    
    public Vector getEvents(String sessionID);
    public void putEvents(String sessionID, Vector events);
}
