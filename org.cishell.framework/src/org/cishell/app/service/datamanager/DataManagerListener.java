/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 22, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.app.service.datamanager;

import org.cishell.framework.data.Data;


/**
 * A listener that is notified of changes in the {@link DataManagerService}
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface DataManagerListener {
	
	/**
	 * Notifies that a Data object has been added to the associated 
	 * {@link DataManagerService} 
	 * 
	 * @param data  The added {@link Data} object
     * @param label The label assigned to the Data object
	 */
    public void dataAdded(Data data, String label);
    
    /**
     * Notifies that a Data object has had its label changed
     * 
     * @param data  The Data object
     * @param label The new label
     */
    public void dataLabelChanged(Data data, String label);
    
    /**
     * Notifies that a Data object has been removed from the associated
     * {@link DataManagerService}
     * 
     * @param data The removed {@link Data} object
     */
    public void dataRemoved(Data data);
    
    /**
     * Notifies that a set of data objects have been selected in the associated
     * {@link DataManagerService}
     * 
     * @param data The selected {@link Data} objects
     */
    public void dataSelected(Data[] data);
}
