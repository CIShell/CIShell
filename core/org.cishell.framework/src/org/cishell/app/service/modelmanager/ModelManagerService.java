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
package org.cishell.app.service.modelmanager;

import org.cishell.framework.data.Data;

/**
 * A service for managing loaded {@link Data}s. 
 * {@link ModelManagerListener}s may be registered to be notified of changes
 * in the model manager.
 * 
 * Clients are encouraged to use this service for managing the models they have 
 * loaded into memory. Algorithm writers are encouraged not to use this service 
 * as it is not guaranteed to be available like the standard CIShell services 
 * are.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface ModelManagerService {
	/**
	 * Adds a Data to the manager
	 * 
	 * @param model The data model
	 */
    public void addModel(Data model);
    
    /**
     * Removes a Data from the manager
     * 
     * @param model The data model
     */
    public void removeModel(Data model);
    
    /**
     * Sets which data models are selected in the manager. If a given Data
     * in the array of DataModels is not in the model manager, then it will be
     * automatically added before selection.
     * 
     * @param models The data models to select
     */
    public void setSelectedModels(Data[] models);
    
    /**
     * Returns the models that have been selected in the manager
     * 
     * @return An array of DataModels, length may be zero
     */
    public Data[] getSelectedModels();
    
    /**
     * Returns all of the DataModels loaded into the manager
     * 
     * @return An array of DataModels, length may be zero
     */
    public Data[] getAllModels();
    
    /**
     * Adds a {@link ModelManagerListener} that will be notified as DataModels
     * are added, removed, and selected
     * 
     * @param listener The listener to be notified of events
     */
    public void addModelManagerListener(ModelManagerListener listener);
    
    /**
     * Removes the {@link ModelManagerListener} from the listener group and will
     * no longer notify it of events
     * 
     * @param listener The listener to be removed
     */
    public void removeModelManagerListener(ModelManagerListener listener);
    

    /**
     * Returns the label for a stored Data
     * 
     * @param dm The Data
     * @return A label for the Data
     */
    public String getLabel(Data dm);
    
    /**
     * Set the label to be used for the data model. The model manager is free
     * to change the label so that it is unique.
     * 
     * @param dm The Data
     * @param label The new label for the data model.
     */
    public void setLabel(Data dm, String label);
}
