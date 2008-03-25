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
 * A simple GUI for user interaction. A single {@link SelectionListener} can be
 * set to be informed when the user enters information and hits Ok.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface GUI {
	/**
     * Pops up this GUI, gets data from the user, and returns what they entered.
     * This is a convenience method that first opens the GUI, then shows the GUI 
     * to the user, who then enters in the needed information, which is then 
     * taken and put into a {@link Dictionary}, and is given to this method's 
     * caller.
     * 
	 * @return The data the user entered or <code>null</code> if the operation 
	 * 		   was cancelled
	 */
    public Dictionary openAndWait();
    
    /**
     * Opens the GUI and shows it to the user
     */
    public void open();
    
    /**
     * Closes the GUI
     */
    public void close();
    
    /**
     * Returns if the GUI is closed
     * @return If the GUI has been closed or not
     */
    public boolean isClosed();
    
    /**
     * Sets the selection listener to be informed when the user finishes 
     * entering information and hits 'Ok' or cancels
     * 
     * @param listener The listener to notify
     */
    public void setSelectionListener(SelectionListener listener);
}
