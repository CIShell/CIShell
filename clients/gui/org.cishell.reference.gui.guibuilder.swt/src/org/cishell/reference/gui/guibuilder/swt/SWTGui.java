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

import org.cishell.reference.gui.guibuilder.swt.builder.GUIComponent;
import org.cishell.reference.gui.guibuilder.swt.builder.UpdateListener;
import org.cishell.service.guibuilder.GUI;
import org.cishell.service.guibuilder.SelectionListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class SWTGui implements GUI, UpdateListener {    
    private Shell shell;
    private SWTGuiComposite composite;
    private SelectionListener listener;
    private boolean hitOk = false;
    
    private Button okButton;

    public SWTGui(final Shell shell, int style, 
            String id, MetaTypeProvider provider) {
        this.shell = shell;
        
        if (provider == null) {
            throw new IllegalArgumentException("Null MetaTypeProvider given");
        }
        
        ObjectClassDefinition ocd = provider.getObjectClassDefinition(id, null);
        shell.setText(ocd.getName());
                
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        shell.setLayout(gridLayout);

        Font defaultFont = new Font(shell.getDisplay(), "SanSerif", 8, SWT.NONE);
        
        //stuff to display a message
        String message = ocd.getDescription();
        if(message != null && !message.equals("")){
            Label msg = new Label(shell, SWT.WRAP);
            msg.setText(message);
            
            GridData labelData = new GridData();
            labelData.horizontalAlignment = SWT.CENTER;            
            msg.setLayoutData(labelData);
        }

        //set up the user area where the main GUI will be set up using Parameters
        composite = new SWTGuiComposite(shell, style, id, provider);
        composite.addUpdateListener(this);
        
        //the group w/ ok and cancel
        Composite buttonsGroup = new Composite(shell, SWT.NONE);
        FillLayout rowLayout = new FillLayout();
        rowLayout.spacing = 5;
        buttonsGroup.setLayout(rowLayout);

        //place them at the bottom right
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.grabExcessHorizontalSpace = false;
        buttonsGroup.setLayoutData(gridData);

        okButton = new Button(buttonsGroup, SWT.PUSH);
        okButton.setText("OK");
        okButton.setSize(40, 20);
        okButton.setFont(defaultFont);
        okButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                hitOk = true;
                close();
                
                if (listener != null) {
                    listener.hitOk(composite.getEnteredResponses());
                }
            }
        });

        Button cancel = new Button(buttonsGroup, SWT.NONE);
        cancel.setText("Cancel");
        cancel.setSize(40, 20);
        cancel.setFont(defaultFont);
        cancel.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {                    
                    close();
                }
            });

        
        shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                if (!hitOk && listener != null) {
                    listener.cancelled();
                }
            }});
        
        shell.setDefaultButton(okButton);
        
        validate();
    }
    
    /**
     * @see org.cishell.service.guibuilder.GUI#close()
     */
    public void close() {
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                shell.close();
                shell.dispose();
            }});
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#isClosed()
     */
    public boolean isClosed() {
        return shell.isDisposed();
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#open()
     */
    public void open() {
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                shell.pack();
                shell.open();
            }});
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#openAndWait()
     */
    public Dictionary openAndWait() {
        open();
        final Display display = shell.getDisplay();
        
        OpenAndWaitListener listener = new OpenAndWaitListener();
        setSelectionListener(listener);
        
        display.syncExec(new Runnable() {
            public void run() {
                while (!isClosed()) {
                    if (!display.readAndDispatch()) display.sleep();
                }
            }});
        
        return listener.valuesEntered;
    }
    
    private static class OpenAndWaitListener implements SelectionListener {
        Dictionary valuesEntered = null;

        public void cancelled() {}

        public void hitOk(Dictionary valuesEntered) {
            this.valuesEntered = valuesEntered;
        }
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#setSelectionListener(org.cishell.service.guibuilder.SelectionListener)
     */
    public void setSelectionListener(SelectionListener listener) {
        this.listener = listener;
    }
    
    public String validate() {
        String valid = composite.validate();
        
        //if valid is a string then the string is the error message
        if (valid != null && valid.length() > 0) {
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
        }
        
        return valid;
    }

    public void componentUpdated(GUIComponent component) {
        validate();
    }
}
