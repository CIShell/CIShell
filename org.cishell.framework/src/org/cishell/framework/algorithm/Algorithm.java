/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 7, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework.algorithm;

import org.cishell.framework.data.Data;

/**
 * A class which executes some arbitrary code and optionally returns any data 
 * produced. What happens when the execute method is run is entirely
 * up to the Algorithm developer. Algorithms should be primed with whatever data
 * is needed, usually by its associated {@link AlgorithmFactory}, before 
 * execution. This allows an Algorithm to be set up, then scheduled for later 
 * execution.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface Algorithm {
    
   /**
    * Executes and optionally returns a Data array. 
    * 
    * @return A Data array that was created or <code>null</code>
    */
   public Data[] execute(); 
}
