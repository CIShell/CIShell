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
import org.eclipse.swt.graphics.Point;
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

public class SWTGui implements GUI, UpdateListener {    
	private static final int MAXIMUM_INITIAL_DIALOGUE_HEIGHT = 500;

	public static final int TEXT_WRAP_LENGTH = 350;
	
    private Shell shell;
    private SWTGuiComposite composite;
    private SelectionListener listener;
    private boolean hitOk = false;
    
    private Button okButton;

    public SWTGui(final Shell shell, int style, String id, MetaTypeProvider provider) {
        this.shell = shell;
        
        if (provider == null) {
            throw new IllegalArgumentException("Null MetaTypeProvider given");
        }
        
        ObjectClassDefinition ocd = provider.getObjectClassDefinition(id, null);
        this.shell.setText(ocd.getName());

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        this.shell.setLayout(gridLayout);

        Font defaultFont = new Font(this.shell.getDisplay(), "SanSerif", 8, SWT.NONE);
        
        //stuff to display a message
        String message = ocd.getDescription();
        if(message != null && !message.equals("")){
            Label msg = new Label(this.shell, SWT.WRAP);
            msg.setText(message);
            msg.pack(true);
            GridData labelData = new GridData();
            labelData.horizontalAlignment = GridData.CENTER;
            labelData.grabExcessHorizontalSpace = true;
            if (msg.getSize().x > TEXT_WRAP_LENGTH) {
            	labelData.widthHint = TEXT_WRAP_LENGTH;
            }
            msg.setLayoutData(labelData);
        }

        //set up the user area where the main GUI will be set up using Parameters
        composite = new SWTGuiComposite(this.shell, style, id, provider);
        composite.addUpdateListener(this);
        
        //the group w/ ok and cancel
        Composite buttonsGroup = new Composite(this.shell, SWT.NONE);
        FillLayout rowLayout = new FillLayout();
        rowLayout.spacing = 5;
        buttonsGroup.setLayout(rowLayout);

        //place them at the bottom right
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.END;
        gridData.grabExcessHorizontalSpace = false;
        buttonsGroup.setLayoutData(gridData);

        this.okButton = new Button(buttonsGroup, SWT.PUSH);
        this.okButton.setText("OK");
        this.okButton.setSize(40, 20);
        this.okButton.setFont(defaultFont);
        this.okButton.addSelectionListener(new SelectionAdapter() {
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

        
        this.shell.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                if (!hitOk && listener != null) {
                    listener.cancelled();
                }
            }});
        
        this.shell.setDefaultButton(this.okButton);
        
        validate();
    }
    
    /**
     * @see org.cishell.service.guibuilder.GUI#close()
     */
    public void close() {
        this.shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                SWTGui.this.shell.close();
                SWTGui.this.shell.dispose();
            }});
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#isClosed()
     */
    public boolean isClosed() {
        return this.shell.isDisposed();
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#open()
     */
    public void open() {
        this.shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                SWTGui.this.shell.pack();
                resizeShell(SWTGui.this.shell);
                SWTGui.this.shell.open();
            }

			private void resizeShell(Shell shell) {
				Point shellSize = shell.getSize();
				shell.setSize(shellSize.x, calculateNewDialogHeight(shellSize.y));
			}

			private int calculateNewDialogHeight(int proposedHeight) {
				return Math.min(MAXIMUM_INITIAL_DIALOGUE_HEIGHT, proposedHeight);
			}});
    }

    /**
     * @see org.cishell.service.guibuilder.GUI#openAndWait()
     */
    public Dictionary<String, Object> openAndWait() {
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
        Dictionary<String, Object> valuesEntered = null;

        public void cancelled() {}

        public void hitOk(Dictionary<String, Object> valuesEntered) {
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
        
        // If valid is a string then the string is the error message.
        if ((valid != null) && (valid.length() > 0)) {
            this.okButton.setEnabled(false);
        } else {
            this.okButton.setEnabled(true);
        }
        
        return valid;
    }

    public void componentUpdated(GUIComponent component) {
        validate();
    }
}
