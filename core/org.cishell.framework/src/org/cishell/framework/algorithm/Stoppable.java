/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 13, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework.algorithm;

public interface Stoppable {
    public void stop();
    public boolean isStopped();
}
