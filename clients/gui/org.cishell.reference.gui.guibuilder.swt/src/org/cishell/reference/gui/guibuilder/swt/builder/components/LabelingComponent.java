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
import org.cishell.reference.gui.guibuilder.swt.builder.GUIComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.StringConverter;
import org.cishell.reference.gui.guibuilder.swt.builder.UpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class LabelingComponent extends AbstractComponent implements UpdateListener {
    private GUIComponent childComponent;
    private Label label;
    private Label description;

    public LabelingComponent(GUIComponent childComponent) {
        super(true, childComponent.getColumns());
        this.childComponent = childComponent;
        setAttributeDefinition(childComponent.getAttributeDefinition());

        if (!childComponent.drawsLabel()) {
            numColumns++;
        }
        
        String description = attr.getDescription();
        if (description != null && description.length() > 0) {
            numColumns++;
        }
        childComponent.addUpdateListener(this);
    }

    public Control createGUI(Composite parent, int style) {
        if (drawsLabel && !childComponent.drawsLabel()) {
            String labelText = attr.getName();
            
            label = new Label(parent, SWT.NONE);
            if (labelText == null) labelText = "";
            label.setText(labelText);
        }

        Control control = childComponent.createGUI(parent, style);
        setDefaultValue();
        
        description = new Label(parent, SWT.NONE);
        GridData gd = new GridData(SWT.END,SWT.CENTER,false,false);
        description.setLayoutData(gd);
        
        //add a description tooltip and add to the gui
        String descText = attr.getDescription();
        if (descText != null && descText.length() > 0) { 
            if (label != null) {
                label.setToolTipText(descText);
            } else {
                control.setToolTipText(descText);
            }
            
            Image image = parent.getDisplay().getSystemImage(SWT.ICON_QUESTION);
            
            Rectangle r = image.getBounds();
            
            image = new Image(null, image.getImageData().scaledTo(r.width/2, r.height/2));
            
            description.setToolTipText(descText);
            description.setImage(image);
        }
        
        return control;
    }
    
    protected void setDefaultValue() {
        String[] defaults = attr.getDefaultValue();
        
        if (defaults != null && defaults.length > 0) {
        	
            Object value = StringConverter.getInstance().stringToObject(attr, defaults[0]);
            setValue(value);
        }
    }

    public Object getValue() {
        return childComponent.getValue();
    }

    public void setValue(Object value) {
        childComponent.setValue(value);
    }

    public String validate() {
        return childComponent.validate();
    }

    public void componentUpdated(GUIComponent component) {        
        if (!childComponent.drawsLabel()) {
            String valid = validate();
            
            //if valid is a string then the string is the error message
            if (valid != null && valid.length() > 0) {
                label.setForeground(ERROR_COLOR);
            } else {
                label.setForeground(null);
            }
        }
        update();
    }
}
