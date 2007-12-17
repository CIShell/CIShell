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


import org.cishell.framework.algorithm.Algorithm;

public interface AlgorithmRegistry {
    public static String SERVICE_NAME = "AlgorithmRegistry";
    
    public void execute(String sessionID, String algorithmID);
    
    public void unregisterAlgorithm(String algorithmID);
    public String registerAlgorithm(Algorithm algorithm);
    
    public Algorithm getAlgorithm(String algorithmID);
}
