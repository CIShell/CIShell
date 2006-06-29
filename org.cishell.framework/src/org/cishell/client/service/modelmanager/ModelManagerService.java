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
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.client.service.modelmanager;

import org.cishell.framework.datamodel.DataModel;

public interface ModelManagerService {
    public void addModel(DataModel model);
    public void removeModel(DataModel model);
    public void setSelectedModels(DataModel[] models);
    
    public DataModel[] getSelectedModels();
    public DataModel[] getModels();
    
    public void addModelManagerListener(ModelManagerListener listener);
    public void removeModelManagerListener(ModelManagerListener listener);
}
