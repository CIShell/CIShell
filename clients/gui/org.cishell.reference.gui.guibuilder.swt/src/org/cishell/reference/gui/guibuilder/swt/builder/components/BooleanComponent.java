/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Sep 26, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.swt.builder.components;

import org.cishell.reference.gui.guibuilder.swt.builder.AbstractComponent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class BooleanComponent extends AbstractComponent {
    Button checkbox;

    public BooleanComponent() {
        super(true, 1);
    }

    public Control createGUI(Composite parent, int style) {
        checkbox = new Button(parent, SWT.CHECK);
        
        GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        gridData.horizontalSpan = MAX_SPAN;
        checkbox.setLayoutData(gridData);

        String label = attribute.getName();
        if(label != null)
            checkbox.setText(label);
        else    
            checkbox.setText("");

        checkbox.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {                    
                update();
            }});
        
        return checkbox;
    }

    public Object getValue() {
    	return Boolean.valueOf(this.checkbox.getSelection());
    }

    public void setValue(Object value) {
        if ((value instanceof Boolean) && (value != null)) {
            checkbox.setSelection(((Boolean) value).booleanValue());
        } else {
            checkbox.setSelection(false);
        }
    }

    public String validate() {
        return attribute.validate("" + checkbox.getSelection());
    }
}
