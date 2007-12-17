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

import java.util.Hashtable;
import java.util.Vector;

import org.cishell.framework.algorithm.AlgorithmFactory;

public interface AlgorithmFactoryRegistry {
    public static String SERVICE_NAME = "AlgorithmFactoryRegistry";
    
    public String createParameters(String servicePID, Vector dataModelIDs);

    public String createAlgorithm(String sessionID, String servicePID, Vector dataModelIDs, Hashtable dictionary);
    
    public Hashtable getProperties(String servicePID);
    
    public AlgorithmFactory getAlgorithmFactory(String servicePID);
}
