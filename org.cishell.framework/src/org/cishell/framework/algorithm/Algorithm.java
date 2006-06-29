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

import org.cishell.framework.datamodel.DataModel;

/**
 * An algorithm. In CIShell an algorithm can be basically any arbitrary code
 * execution cycle. What happens when the execute method is run is entirely
 * up to the Algorithm writer. Some algorithms may be primed with a data model
 * that it analyzes and returns a derivitive data model or it may convert from
 * one data model to another or not take in any data model and based on some 
 * given parameters create an entirely new data model. 
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
    * Executes and optionally returns a data model. Algorithms are usually 
    * primed ahead of time with all the necessary data needed. This allows
    * an Algorithm to be set up, then scheduled for later execution.
    * 
    * @return A data model that was created or <code>null</code>
    */
   public DataModel[] execute(); 
}
