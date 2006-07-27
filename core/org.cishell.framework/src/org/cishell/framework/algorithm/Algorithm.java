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
 * In CIShell, an algorithm can be basically any arbitrary code
 * execution cycle. What happens when the execute method is run is entirely
 * up to the Algorithm writer. Some algorithms may be primed with a Data array
 * that it analyzes and returns a derivitive Data array or it may convert from
 * one Data array to another or not take in any Data array and based on some 
 * given parameters create an entirely new Data array. 
 * 
 * Algorithms are typically also given a {@link CIShellContext} by which they 
 * can gain access to standard services like logging, preferences, and gui 
 * creation. If an algorithm only uses these standard services and does not pop
 * up any graphical gui (aside from using the GUIBuilderService) then this 
 * Algorithm may be safely run remotely. 
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface Algorithm {
    
   /**
    * Executes and optionally returns a Data array. Algorithms are usually 
    * primed ahead of time with all the necessary data needed. This allows
    * an Algorithm to be set up, then scheduled for later execution.
    * 
    * @return A Data array that was created or <code>null</code>
    */
   public Data[] execute(); 
}
