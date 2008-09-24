/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Sep 12, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.workspace;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.update.ui.UpdateManagerUI;

public class ManageConfigurationAction implements IWorkbenchWindowActionDelegate {
    Shell s;
    
    public void run(IAction action) {
        UpdateManagerUI.openConfigurationManager(s);
    }
    
    public void init(IWorkbenchWindow window) {
        s = window.getShell();
    }
    public void dispose() {}
    public void selectionChanged(IAction action, ISelection selection) {}
}
