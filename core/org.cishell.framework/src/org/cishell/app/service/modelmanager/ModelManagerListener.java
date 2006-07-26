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
package org.cishell.app.service.modelmanager;

import org.cishell.framework.datamodel.DataModel;


/**
 * A listener that is notified of changes in the {@link ModelManagerService}.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface ModelManagerListener {
	
	/**
	 * Notifies that a model has been added to the associated 
	 * {@link ModelManagerService} 
	 * 
	 * @param dm    The added {@link DataModel}
     * @param label The label assigned to the datamodel
	 */
    public void modelAdded(DataModel dm, String label);
    
    /**
     * Notifies that a model has had its label changed
     * 
     * @param dm    The DataModel
     * @param label The new label
     */
    public void modelLabelChanged(DataModel dm, String label);
    
    /**
     * Notifies that a model has been removed from the associated
     * {@link ModelManagerService}
     * 
     * @param dm The removed {@link DataModel}
     */
    public void modelRemoved(DataModel dm);
    
    /**
     * Notifies that a set of models have been selected in the associated
     * {@link ModelManagerService}
     * 
     * @param dm The selected {@link DataModel}s
     */
    public void modelsSelected(DataModel[] dm);
}
