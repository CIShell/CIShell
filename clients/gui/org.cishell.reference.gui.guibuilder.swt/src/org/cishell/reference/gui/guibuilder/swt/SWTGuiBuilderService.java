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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.reference.gui.guibuilder.swt.builder.AbstractDialog;
import org.cishell.service.guibuilder.GUI;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.cishell.service.guibuilder.SelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class SWTGuiBuilderService implements GUIBuilderService {
    private static final GUI NULL_GUI = new GUI() {
        public void open() {}
        public void close() {}
        
        public boolean isClosed() {
            return true;
        }

        public Dictionary openAndWait() {
            return new Hashtable();
        }

        public void setSelectionListener(SelectionListener listener) {
            if (listener != null) 
                listener.cancelled();
        }};
        
    private Display display;
    private Shell lastShell;
        
    
    public SWTGuiBuilderService(Display display) {
        this.display = display;
    }

    
    public GUI createGUI(String id, MetaTypeProvider parameters) {
        boolean validParams = true;
        
        try {
            validParams = parameters.getObjectClassDefinition(id, null) != null;
        } catch (IllegalArgumentException e) {
            validParams = false;
        }
        
        if (validParams) {
            GUICreator creator = new GUICreator(id, parameters);
            display.syncExec(creator);
            
            return creator.gui;
        } else {
            return NULL_GUI;
        }
    }
    
    private class GUICreator implements Runnable {
        GUI gui = NULL_GUI;
        String id;
        MetaTypeProvider parameters;
        
        public GUICreator(String id, MetaTypeProvider parameters) {
            this.id = id;
            this.parameters = parameters;
        }
        
        public void run() {
            try {
                Shell activeShell = getActiveShell();
                
                Shell shell = new Shell(activeShell, SWT.DIALOG_TRIM | SWT.RESIZE);
                if (activeShell != null) {
                    shell.setImage(activeShell.getImage());
                }
                
                gui = new SWTGui(shell,SWT.NONE,id,parameters);
            } catch (IllegalArgumentException e) {}
        }
    }
    
    private static class Returner {
        Object returnValue;
    }
    
    private Shell getActiveShell() {
        final Returner returner = new Returner();
        guiRun(new Runnable() {
            public void run() {
                returner.returnValue = display.getActiveShell();
                
                if (returner.returnValue == null) {
                    if (lastShell != null) {
                        returner.returnValue = lastShell;
                    } else {
                        returner.returnValue = new Shell();
                    }
                }
            }});
        
        lastShell = (Shell) returner.returnValue;
        return (Shell) returner.returnValue;
    }

    private void guiRun(Runnable runner) {
        if (display.getThread() == Thread.currentThread()) {
            runner.run();
        } else {
            display.syncExec(runner);
        }
    }
    
    public Dictionary createGUIandWait(String id, MetaTypeProvider parameters) {
        return createGUI(id, parameters).openAndWait();
    }
    
    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showConfirm(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean showConfirm(final String title, final String message, final String detail) {
        final Returner returner = new Returner();
        
        guiRun(new Runnable() {
            public void run() {
                returner.returnValue = new Boolean(
                        AbstractDialog.openConfirm(getActiveShell(), title, message, detail));
            }});
            
        return ((Boolean)returner.returnValue).booleanValue();
    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showError(java.lang.String, java.lang.String, java.lang.String)
     */
    public void showError(final String title, final String message, final String detail) {
        guiRun(new Runnable() {
            public void run() {
                AbstractDialog.openError(getActiveShell(), title, message, detail);
            }});   
    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showError(java.lang.String, java.lang.String, java.lang.Throwable)
     */
    public void showError(String title, String message, Throwable error) {
        if (error != null) {
            StringWriter writer = new StringWriter();
            error.printStackTrace(new PrintWriter(writer));
            
            showError(title, message, writer.getBuffer().toString());
        }
    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showInformation(java.lang.String, java.lang.String, java.lang.String)
     */
    public void showInformation(final String title, final String message, final String detail) {
        guiRun(new Runnable() {
            public void run() {
                AbstractDialog.openInformation(getActiveShell(), title, message, detail);
            }});
    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showQuestion(java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean showQuestion(final String title, final String message, final String detail) {
        final Returner returner = new Returner();
        
        guiRun(new Runnable() {
            public void run() {
                returner.returnValue = new Boolean(
                        AbstractDialog.openQuestion(getActiveShell(), title, message, detail));
            }});
            
        return ((Boolean)returner.returnValue).booleanValue();
    }

    /**
     * @see org.cishell.service.guibuilder.GUIBuilderService#showWarning(java.lang.String, java.lang.String, java.lang.String)
     */
    public void showWarning(final String title, final String message, final String detail) {
        guiRun(new Runnable() {
            public void run() {
                AbstractDialog.openWarning(getActiveShell(), title, message, detail);
            }});
    }
}
