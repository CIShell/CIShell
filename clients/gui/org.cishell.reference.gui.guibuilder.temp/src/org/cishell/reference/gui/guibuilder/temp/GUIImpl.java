/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 23, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.guibuilder.temp;

import java.util.Dictionary;

import org.cishell.service.guibuilder.GUI;
import org.cishell.service.guibuilder.SelectionListener;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.metatype.MetaTypeProvider;

import edu.iu.iv.common.guibuilder.GUIBuilder;
import edu.iu.iv.gui.builder.SwtGUIBuilder;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class GUIImpl implements GUI {
    boolean closed;
    GUIBuilder builder;
    ParameterMapAdapter pmap;

    public GUIImpl(String id, MetaTypeProvider provider) {
        pmap = new ParameterMapAdapter(provider, id);
        final String title = pmap.getObjectClassDefinition().getName();
        final String message = pmap.getObjectClassDefinition().getDescription();
                
        GUIBuilder.setGUIBuilder(SwtGUIBuilder.getGUIBuilder());
        
        PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
            public void run() {
                builder = GUIBuilder.createGUI(title, message, pmap);
            }});
        
        closed = false;
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#close()
     */
    public void close() {
        builder.close();
        closed = true;
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#isClosed()
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#open()
     */
    public void open() {
        builder.open();
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#openAndWait()
     */
    public synchronized Dictionary openAndWait() {
        final WaitingSelectionListener listener = new WaitingSelectionListener();
        
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                open();
                
                setSelectionListener(listener);
            }});
        
        while (!listener.gotResult) {
            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return listener.result;
    }
    
    private class WaitingSelectionListener implements SelectionListener {
        Dictionary result;
        boolean gotResult;

        public void cancelled() {
            result = null;
            gotResult = true;
            GUIImpl.this.notifyAll();
        }

        public void hitOk(Dictionary valuesEntered) {
            result = valuesEntered;
            gotResult = true;
            GUIImpl.this.notifyAll();
        }
    }
    

    /**
     * @see org.cishell.service.guibuilder.GUI#setSelectionListener(org.cishell.service.guibuilder.SelectionListener)
     */
    public void setSelectionListener(SelectionListener listener) {
        builder.addSelectionListener(new SelectionListenerAdapter(listener));
    }
    
    private class SelectionListenerAdapter implements edu.iu.iv.common.guibuilder.SelectionListener, SelectionListener {
        SelectionListener realListener;
        
        public SelectionListenerAdapter(SelectionListener realListener) {
            this.realListener = realListener;
        }

        public void widgetSelected() {
            close();
            hitOk(pmap.createDictionary());
        }
        
        public void cancelled() {}
        
        public void hitOk(Dictionary valuesEntered) {
            realListener.hitOk(valuesEntered);
        }        
    }
}
