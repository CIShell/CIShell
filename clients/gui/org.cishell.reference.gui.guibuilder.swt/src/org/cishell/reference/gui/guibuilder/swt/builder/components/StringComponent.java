/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Sep 20, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.swt.builder.components;

import org.cishell.reference.gui.guibuilder.swt.builder.AbstractComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class StringComponent extends AbstractComponent {
    protected Text text;
    
    public StringComponent() {
        this(false, 1);
    }
    
    public StringComponent(boolean drawLabel, int numColumns) {
        super(drawLabel, numColumns);
    }
    
    public Control createGUI(Composite parent, int style) {
        text = new Text(parent, style | SWT.BORDER);
        
        GridData gd = new GridData(SWT.FILL,SWT.CENTER,true,false);
        gd.horizontalSpan = MAX_SPAN-1;
        gd.minimumWidth = 100;
        text.setLayoutData(gd);
        
        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                update();
            }
        }); 
        
        return text;
    }

    public Object getValue() {
        Object value = StringConverter.getInstance().stringToObject(attr, text.getText());
        
        return value;
    }

    public String validate() {
        if (getValue() == null) {
            return "Invalid basic value";
        }
        
        return attr.validate(text.getText());
    }

    public void setValue(Object value) {
        text.setText(value == null ? "" : value.toString());
    }
}
