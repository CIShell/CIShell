/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Sep 14, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.swt;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.cishell.reference.gui.guibuilder.swt.builder.ComponentProvider;
import org.cishell.reference.gui.guibuilder.swt.builder.GUIComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.UpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class SWTGuiComposite implements UpdateListener {
    private ObjectClassDefinition objectClassDefinition;
    private Dictionary<String, GUIComponent> idToComponentMap;
    private Dictionary<String, Object> enteredResponses;
    protected Set<UpdateListener> updateListeners;
    
    private Composite parent;
    private Composite parameterArea;
    private ScrolledComposite scrollingArea;

    public SWTGuiComposite(Composite parent, int style, String id, MetaTypeProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Null MetaTypeProvider given");
        }
        
        this.idToComponentMap = new Hashtable<String, GUIComponent>();
        this.objectClassDefinition = provider.getObjectClassDefinition(id, null);
        this.parent = parent;
        this.updateListeners = new HashSet<UpdateListener>();
        this.enteredResponses = new Hashtable<String, Object>();
        
        setupGUI();

        for (AttributeDefinition attribute :
        		this.objectClassDefinition.getAttributeDefinitions(ObjectClassDefinition.ALL)) {
            GUIComponent component = ComponentProvider.getInstance().createComponent(attribute);

            component.setAttributeDefinition(attribute);
            component.createGUI(this.parameterArea, style);
            this.idToComponentMap.put(attribute.getID(), component);
            component.addUpdateListener(this);
            
            Object value = component.getValue();
            String valid = component.validate();
            
            if ((value != null) && ((valid == null) || (valid.length() == 0))) {
                this.enteredResponses.put(component.getAttributeDefinition().getID(), value);    
            }
        }
        
        setScrollDimensions(this.scrollingArea, this.parameterArea);
        
        this.parameterArea.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent event) {
                SWTGuiComposite.this.enteredResponses = getEnteredResponses();
            }});
    }

	private void setScrollDimensions(ScrolledComposite scroll, Composite innards) {
		Point parameterAreaSize = innards.computeSize(SWT.DEFAULT, SWT.DEFAULT);
        scroll.setMinWidth(parameterAreaSize.x);
        scroll.setMinHeight(parameterAreaSize.y);
	}
    
    private void setupGUI() {
        this.scrollingArea = new ScrolledComposite(this.parent, SWT.H_SCROLL | SWT.V_SCROLL);
        this.scrollingArea.setLayout(new GridLayout(1, true));
        this.scrollingArea.setExpandHorizontal(true);
        this.scrollingArea.setExpandVertical(true);
        this.scrollingArea.setAlwaysShowScrollBars(false);
        
        this.parameterArea = new Composite(scrollingArea, SWT.NONE);
        this.parameterArea.setLayout(new GridLayout(GUIComponent.MAX_SPAN + 1, false));
        
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        this.parameterArea.setLayoutData(gridData);
        
        GridData userData = new GridData();
        userData.grabExcessVerticalSpace = true;
        userData.grabExcessHorizontalSpace = true;
        userData.verticalAlignment = SWT.FILL;
        userData.horizontalAlignment = SWT.FILL;
        
        this.scrollingArea.setLayoutData(userData);
        this.scrollingArea.setContent(this.parameterArea);   
    }
    
    
    public ObjectClassDefinition getObjectClassDefinition() {
        return this.objectClassDefinition;
    }
    
    public Object getResponse(String id) {
        GUIComponent component = this.idToComponentMap.get(id);

        if (component != null) {
        	return component.getValue();
        } else {
        	return null;
        }
//        return component == null ? null : component.getValue();
    }
    
    public Dictionary<String, Object> getEnteredResponses() {
        return this.enteredResponses;
    }
    
    /**
     * Get this GUI's associated shell.
     * @return the shell
     */
    public Shell getShell() {
        return this.parent.getShell();
    }

    /**
     * Return the created composite GUI.
     * @return the composite
     */
    public Composite getUserArea() {        
        return this.parameterArea;
    }
    
    public Composite getComposite() {
        return this.scrollingArea;
    }
    
    public String validate() {
        String totalValid = "";

        for (AttributeDefinition attribute : this.objectClassDefinition.getAttributeDefinitions(
        		ObjectClassDefinition.REQUIRED)) {
            GUIComponent component = this.idToComponentMap.get(attribute.getID());
            String valid = component.validate();

            if ((valid != null) && (valid.length() > 0)) {
                totalValid += "\"" + valid + "\"; ";
            }
        }
        
        return totalValid;
    }

    public void componentUpdated(GUIComponent component) {
        Object value = component.getValue();
        String valid = component.validate();
        
        if ((value != null) && ((valid == null) || (valid.length() == 0))) {
            this.enteredResponses.put(component.getAttributeDefinition().getID(), value);    
        } else {
            this.enteredResponses.remove(component.getAttributeDefinition().getID());
        }

        for (UpdateListener listener : this.updateListeners) {
            listener.componentUpdated(component);
        }
    }
    
    public void addUpdateListener(UpdateListener listener) {
        this.updateListeners.add(listener);
    }

    public void removeUpdateListener(UpdateListener listener) {
        this.updateListeners.remove(listener);
    }
}
