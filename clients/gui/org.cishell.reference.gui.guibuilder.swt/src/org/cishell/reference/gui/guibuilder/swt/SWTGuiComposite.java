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
import java.util.Iterator;
import java.util.Set;

import org.cishell.reference.gui.guibuilder.swt.builder.ComponentProvider;
import org.cishell.reference.gui.guibuilder.swt.builder.GUIComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.UpdateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
    private ObjectClassDefinition ocd;
    private Dictionary idToComponentMap;
    private Dictionary enteredResponses;
    protected Set listeners;
    
    private Composite parent;
    private Composite userArea;
    private ScrolledComposite userScroll;
    private int style;

    public SWTGuiComposite(Composite parent, int style, 
            String id, MetaTypeProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Null MetaTypeProvider given");
        }
        
        this.idToComponentMap = new Hashtable();
        this.ocd = provider.getObjectClassDefinition(id, null);
        this.parent = parent;
        this.style = style;
        this.listeners = new HashSet();
        this.enteredResponses = new Hashtable();
        
        setupGUI();
        
        AttributeDefinition[] attrs = ocd.getAttributeDefinitions(ObjectClassDefinition.ALL);
        for (int i=0; i < attrs.length; i++) {
            GUIComponent component = ComponentProvider.getInstance().createComponent(attrs[i]);
            
            component.setAttributeDefinition(attrs[i]);
            component.createGUI(userArea, style);
            idToComponentMap.put(attrs[i].getID(), component);
            component.addUpdateListener(this);
            
            Object value = component.getValue();
            String valid = component.validate();
            
            if (value != null && (valid == null || valid.length() == 0)) {
                enteredResponses.put(component.getAttributeDefinition().getID(), value);    
            }
        }
        
        userArea.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                enteredResponses = getEnteredResponses();
            }});
    }
    
    private void setupGUI() {
        userScroll = new ScrolledComposite(parent, style);
        userScroll.setLayout(new GridLayout(1, true));
        userScroll.setExpandHorizontal(true);
        userScroll.setExpandVertical(true);
        userScroll.setAlwaysShowScrollBars(false);
        
        userArea = new Composite(userScroll, SWT.NONE);
        userArea.setLayout(new GridLayout(GUIComponent.MAX_SPAN+1,false));
        
        GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true);
        userArea.setLayoutData(gd);
        
        GridData userData = new GridData();
        userData.grabExcessVerticalSpace = true;
        userData.grabExcessHorizontalSpace = true;
        userData.verticalAlignment = SWT.FILL;
        userData.horizontalAlignment = SWT.FILL;
        
        userScroll.setLayoutData(userData);
        userScroll.setContent(userArea);   
    }
    
    
    public ObjectClassDefinition getObjectClassDefinition() {
        return ocd;
    }
    
    public Object getResponse(String id) {
        GUIComponent component = (GUIComponent) idToComponentMap.get(id);
        
        return component == null ? null : component.getValue();
    }
    
    public Dictionary getEnteredResponses() {
        return enteredResponses;
    }
    
    /**
     * Get this GUI's associated shell.
     * @return the shell
     */
    public Shell getShell() {
        return parent.getShell();
    }

    /**
     * Return the created composite GUI.
     * @return the composite
     */
    public Composite getUserArea() {        
        return userArea;
    }
    
    public Composite getComposite() {
        return userScroll;
    }
    
    public String validate() {
        String totalValid = "";
        
        AttributeDefinition[] attrs = ocd.getAttributeDefinitions(ObjectClassDefinition.REQUIRED);
        
        for (int i=0; i < attrs.length; i++) {
            GUIComponent component = (GUIComponent) idToComponentMap.get(attrs[i].getID());
            String valid = component.validate();
            if (valid != null && valid.length() > 0) {
                totalValid += "\"" + valid + "\"; ";
            }
        }
        
        return totalValid;
    }

    public void componentUpdated(GUIComponent component) {
        Object value = component.getValue();
        String valid = component.validate();
        
        if (value != null && (valid == null || valid.length() == 0)) {
            enteredResponses.put(component.getAttributeDefinition().getID(), value);    
        } else {
            enteredResponses.remove(component.getAttributeDefinition().getID());
        }
        
        for (Iterator i=listeners.iterator(); i.hasNext(); ) {
            ((UpdateListener) i.next()).componentUpdated(component);
        }
    }
    
    public void addUpdateListener(UpdateListener listener) {
        listeners.add(listener);
    }

    public void removeUpdateListener(UpdateListener listener) {
        listeners.remove(listener);
    }
}
