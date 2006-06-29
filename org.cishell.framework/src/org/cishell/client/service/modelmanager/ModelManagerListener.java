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
package org.cishell.client.service.modelmanager;

import org.cishell.framework.datamodel.DataModel;


public interface ModelManagerListener {
    public void modelAdded(DataModel dm);
    public void modelRemoved(DataModel dm);
    public void modelsSelected(DataModel[] dm);
}
