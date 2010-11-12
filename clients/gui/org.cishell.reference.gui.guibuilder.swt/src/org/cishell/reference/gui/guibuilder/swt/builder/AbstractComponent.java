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
package org.cishell.reference.gui.guibuilder.swt.builder;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public abstract class AbstractComponent implements GUIComponent {
    protected AttributeDefinition attribute;
    protected boolean drawsLabel;
    protected int columnCount;
    protected Set<UpdateListener> listeners;

    public abstract void setValue(Object value);
    public abstract Object getValue();
    public abstract String validate();
    public abstract Control createGUI(Composite parent, int style);
    
    public AbstractComponent(boolean drawsLabel, int columnCount) {
        this.drawsLabel = drawsLabel;
        this.columnCount = columnCount;
        this.listeners = new HashSet<UpdateListener>();
    }
    
    public AttributeDefinition getAttributeDefinition() {
        if (this.attribute == null) {
            throw new IllegalStateException("AttributeDefinition has not been set");
        }
        
        return this.attribute;
    }

    public void setAttributeDefinition(AttributeDefinition attribute) {
        this.attribute = attribute;
    }

    public boolean drawsLabel() {
        return this.drawsLabel;
    }

    public int getColumns() {
        return this.columnCount;
    }
    
    protected void update() {
    	for (UpdateListener listener : this.listeners) {
            listener.componentUpdated(this);
        }
    }
    
    public void addUpdateListener(UpdateListener listener) {
        this.listeners.add(listener);
    }

    public void removeUpdateListener(UpdateListener listener) {
        this.listeners.remove(listener);
    }
}
