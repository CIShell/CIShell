/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 14, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.service.guibuilder;

import java.util.Dictionary;

/**
 * A listener that is notified when all values entered by a {@link GUI} user 
 * have been validated and they have clicked 'Ok' to proceed or if the operation
 * was cancelled
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface SelectionListener {
	/**
	 * Notification that the user hit 'Ok' along with the data they entered
	 * 
	 * @param valuesEntered The data the user entered
	 */
    public void hitOk(Dictionary<String, Object> valuesEntered);
    
    /**
     * Notification that the user cancelled the operation
     */
    public void cancelled();
}
