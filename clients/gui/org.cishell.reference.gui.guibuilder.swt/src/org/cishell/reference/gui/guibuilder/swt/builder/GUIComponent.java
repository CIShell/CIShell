/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Sep 15, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.swt.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface GUIComponent {
    public static final int MAX_SPAN = 3;
    public static final Color ERROR_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_RED);
    
    public Control createGUI(Composite parent, int style);
    public int getColumns();
    public boolean drawsLabel();
    
    public Object getValue();
    public void setValue(Object value);
    public String validate();
    
    public void setAttributeDefinition(AttributeDefinition attr);
    public AttributeDefinition getAttributeDefinition();
    
    public void addUpdateListener(UpdateListener listener);
    public void removeUpdateListener(UpdateListener listener);
}
