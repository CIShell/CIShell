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

import java.util.Vector;

import org.cishell.reference.gui.guibuilder.swt.builder.AbstractComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.ComponentProvider;
import org.cishell.reference.gui.guibuilder.swt.builder.GUIComponent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.metatype.AttributeDefinition;

//FIXME: Finish implementation...
public class MultiValuedComponent extends AbstractComponent {
    protected Composite panel;
    protected Vector componentList;
    protected Vector controlList;
    protected int maxElements;
    protected int style;

    public MultiValuedComponent(boolean drawsLabel, int numColumns) {
        super(false, 1);
        componentList = new Vector();
        controlList = new Vector();
    }

    public Control createGUI(Composite parent, int style) {
        this.style = style;
        panel = new Composite(parent, style);
        panel.setLayout(new GridLayout(MAX_SPAN+1,false));
        
        GridData gd = new GridData(SWT.FILL,SWT.TOP,true,false);
        gd.horizontalSpan = MAX_SPAN-1;
        panel.setLayoutData(gd);
        
        addComponent(0);
        
        return panel;
    }
    
    protected synchronized void addComponent(int position) {
        GUIComponent component = ComponentProvider.getInstance().createBasicComponent(attribute);
        Control control = component.createGUI(panel, style);
        
        
        
        componentList.add(component);
        controlList.add(control);
    }
    
    protected synchronized GUIComponent getComponent(int position) {
        return (GUIComponent) componentList.get(position);
    }
    
    protected synchronized void removeComponent(int position) {
        if (position < componentList.size()) {
            componentList.remove(position);
            Control control = (Control) controlList.remove(position);
            
            control.dispose();
        }
    }
    
    public void setAttributeDefinition(AttributeDefinition attr) {
        super.setAttributeDefinition(attr);
        
        maxElements = Math.abs(attr.getCardinality());
        if (maxElements == 0) maxElements++;
    }
    

    public Object getValue() {
        return null;
    }

    public void setValue(Object value) {

    }

    public String validate() {
        return null;
    }
}
