/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.guibuilder;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;


public class ListBuilderTester {
    public static void main(String[] args) {
        Display display = new Display();
        
        Shell shell = new Shell(display);
        
        GridLayout gridLayout = new GridLayout(1, false);
        shell.setLayout(gridLayout);
        
        GridData gridData = new GridData(GridData.FILL, GridData.FILL, true, true);
        
        new ListBuilder(shell, new ParameterBuilderDelegate(shell))
                .getPanel().setLayoutData(gridData);
        
        shell.setSize(800, 600);
        shell.open();
        
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
    }
}
