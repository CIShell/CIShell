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
import java.util.Iterator;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public abstract class AbstractComponent implements GUIComponent {
    protected AttributeDefinition attr;
    protected boolean drawsLabel;
    protected int numColumns;
    protected Set listeners;

    public abstract void setValue(Object value);
    public abstract Object getValue();
    public abstract String validate();
    public abstract Control createGUI(Composite parent, int style);
    
    public AbstractComponent(boolean drawsLabel, int numColumns) {
        this.drawsLabel = drawsLabel;
        this.numColumns = numColumns;
        this.listeners = new HashSet();
    }
    
    public AttributeDefinition getAttributeDefinition() {
        if (attr == null) {
            throw new IllegalStateException("AttributeDefinition has not been set");
        }
        
        return attr;
    }

    public void setAttributeDefinition(AttributeDefinition attr) {
        this.attr = attr;
    }
    public boolean drawsLabel() {
        return drawsLabel;
    }
    public int getColumns() {
        return numColumns;
    }
    
    protected void update() {
        for (Iterator i=listeners.iterator(); i.hasNext(); ) {
            ((UpdateListener) i.next()).componentUpdated(this);
        }
    }
    
    public void addUpdateListener(UpdateListener listener) {
        listeners.add(listener);
    }

    public void removeUpdateListener(UpdateListener listener) {
        listeners.remove(listener);
    }
}
