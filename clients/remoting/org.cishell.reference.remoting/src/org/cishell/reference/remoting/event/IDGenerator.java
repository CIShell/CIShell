/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 13, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.event;


public class IDGenerator {
    private long currentID;
    private String prefix;
    
    public IDGenerator(String prefix) {
        this.prefix = prefix;
        currentID = 0;
    }

    public synchronized String newID() {
        currentID++;
        
        if (currentID == Long.MAX_VALUE) {
            currentID = 0;
        }
        
        return prefix + currentID;
    }
}
