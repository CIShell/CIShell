/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, A Novel Algorithm Integration Framework.
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
package org.cishell.client.service.modelmanager;

import org.cishell.framework.datamodel.DataModel;

/**
 * 
 * @author
 */
public class ModelManagerAdapter implements ModelManagerListener {
    /**
     * @see org.cishell.client.service.modelmanager.ModelManagerListener#modelAdded(org.cishell.framework.datamodel.DataModel)
     */
    public void modelAdded(DataModel dm) {}

    /**
     * @see org.cishell.client.service.modelmanager.ModelManagerListener#modelRemoved(org.cishell.framework.datamodel.DataModel)
     */
    public void modelRemoved(DataModel dm) {}

    /**
     * @see org.cishell.client.service.modelmanager.ModelManagerListener#modelsSelected(org.cishell.framework.datamodel.DataModel[])
     */
    public void modelsSelected(DataModel[] dm) {}
}
