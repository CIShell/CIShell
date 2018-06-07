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
 * An abstract adapter class for notification of changes in the 
 * {@link DataManagerService}. The methods in this class are empty. This class
 * exists as a convenience for creating listener objects.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class DataManagerAdapter implements DataManagerListener {
    /**
     * @see org.cishell.app.service.datamanager.DataManagerListener#dataAdded(org.cishell.framework.data.Data, java.lang.String)
     */
    public void dataAdded(Data data, String label) {}
    
    /**
     * @see org.cishell.app.service.datamanager.DataManagerListener#dataRemoved(org.cishell.framework.data.Data)
     */
    public void dataRemoved(Data data) {}

    /**
     * @see org.cishell.app.service.datamanager.DataManagerListener#dataSelected(org.cishell.framework.data.Data[])
     */
    public void dataSelected(Data[] data) {}

    /**
     * @see org.cishell.app.service.datamanager.DataManagerListener#dataLabelChanged(org.cishell.framework.data.Data, java.lang.String)
     */
    public void dataLabelChanged(Data data, String label) {}
}
