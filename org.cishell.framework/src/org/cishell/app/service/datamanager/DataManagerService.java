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
package org.cishell.app.service.datamanager;

import org.cishell.framework.data.Data;

/**
 * A service for managing loaded {@link Data} objects. 
 * {@link DataManagerListener}s may be registered to be notified of changes
 * in the data manager.
 * 
 * Application developers are encouraged to use this service for managing the
 * models they have loaded into memory. Algorithm developers are encouraged not 
 * to use this service as it is not guaranteed to be available like the standard
 * CIShell services are.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface DataManagerService {
	/**
	 * Adds a Data object to the manager
	 * 
	 * @param data The data object
	 */
    public void addData(Data data);
    
    /**
     * Removes a Data object from the manager
     * 
     * @param data The data object
     */
    public void removeData(Data data);
    
    /**
     * Sets which Data objects are selected in the manager. If a given Data object
     * in the array of Data objects is not in the data manager, then it will be
     * automatically added before selection.
     * 
     * @param data The data objects to select
     */
    public void setSelectedData(Data[] data);
    
    /**
     * Returns The Data objects that have been selected in the manager
     * 
     * @return An array of Data objects, length may be zero
     */
    public Data[] getSelectedData();
    
    /**
     * Returns all of the Data objects loaded into the manager
     * 
     * @return An array of DataModels, length may be zero
     */
    public Data[] getAllData();
    
    /**
     * Adds a {@link DataManagerListener} that will be notified as Data objects
     * are added, removed, and selected
     * 
     * @param listener The listener to be notified of events
     */
    public void addDataManagerListener(DataManagerListener listener);
    
    /**
     * Removes the {@link DataManagerListener} from the listener group and will
     * no longer notify it of events
     * 
     * @param listener The listener to be removed
     */
    public void removeDataManagerListener(DataManagerListener listener);
    
    /**
     * Returns the label for a stored Data object
     * 
     * @param data The Data object
     * @return A label for the Data object
     */
    public String getLabel(Data data);
    
    /**
     * Set the label to be used for the Data object. The model manager is free
     * to change the label so that it is unique.
     * 
     * @param data The Data
     * @param label The new label for the data model
     */
    public void setLabel(Data data, String label);
}
