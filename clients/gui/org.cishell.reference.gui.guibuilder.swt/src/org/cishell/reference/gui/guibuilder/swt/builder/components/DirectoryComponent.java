/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Sep 27, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.swt.builder.components;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;

public class DirectoryComponent extends FileComponent {

    protected String getFile(String defaultPath) {
        DirectoryDialog directorySelectorDialog =
        	new DirectoryDialog(textField.getShell(), SWT.OPEN);
        directorySelectorDialog.setText("Select a Directory");
        directorySelectorDialog.setFilterPath(defaultPath);
        String chosenDirectoryPath = directorySelectorDialog.open();
        
        return fixPath(chosenDirectoryPath);
    }

    protected String validate(File file) {
        if (!file.exists() || !file.isDirectory()) {
            return "Invalid directory location";
        } else {
            return "";
        }
    }
    
    protected String getKeyword() {
        return "directory:";
    }
}
