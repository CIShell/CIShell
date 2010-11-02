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

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;

public class FileComponent extends StringComponent {
    protected Button browseButton;
    private static Object currentValue;

    public FileComponent() {
        this(false, 2);        
    }
    
    public FileComponent(boolean drawLabel, int numColumns) {
        super(drawLabel, numColumns);
    }
    
    public Control createGUI(Composite parent, int style) {
    	// Creates the textField component.
        super.createGUI(parent, style);
        
        Object layoutData = this.textField.getLayoutData();
        
        if (layoutData != null && layoutData instanceof GridData) {
            GridData gridData = (GridData)layoutData;
            gridData.horizontalSpan--;//make room for the browseButton button
        }
        
        this.browseButton = new Button(parent, SWT.PUSH);
        this.browseButton.setText("Browse");
        
        // When click "Browse", here is the listener.
        browseButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                String fileName = getFile(textField.getText());
                
                // Remember this new file/directory selection.
                currentValue = fixPath(fileName);
                
                if (fileName != null) {
                    textField.setText(fileName);
                    update();
                }
            }});  
        
        return this.textField;
    }
    
    /**
     * Pop up a dialog and get the user's input on the File they want.
     * @param defaultPath the path to start at
     * @return the file they chose
     */
    protected String getFile(String defaultPath) {
        FileDialog fileSelectorDialog =
        	new FileDialog(textField.getShell(), SWT.OPEN);
        fileSelectorDialog.setText("Select a File");
        fileSelectorDialog.setFilterPath(defaultPath);
        String chosenFilePath = fileSelectorDialog.open();
        
        return fixPath(chosenFilePath);
    }
    
    protected String validate(File file) {
        if (!file.exists() || !file.isFile()) {
            return "Invalid file location";
        } else {
            return "";
        }
    }
    
    public String validate() {
        File file = new File(textField.getText());
        
        String valid = validate(file);
        if (valid.length() > 0) { //length > 0 means an error
            return valid;
        } else {
            return super.validate();
        }
    }
    
    protected String getKeyword() {
        return "file:";
    }
    
    public Object getValue() {
    	return fixPath(super.getValue().toString());
    }
    
    public void setValue(Object value) {
        if (value != null && value.toString().equals(getKeyword())) {

//          value = System.getProperty("user.home");  
        	
        	//by default, point to NWB or CIShell application installation directory
	        if (currentValue == null) {
	        	value = new File(System.getProperty("osgi.install.area").replace("file:","")).getAbsolutePath();	       
                currentValue = value;                
            }
	        else {
	        	value = currentValue;
	        }
           	
        }
        
        super.setValue(fixPath(value.toString()));
    }
    
    public static String fixPath(String path) {
    	if (path != null) {
    		String fixedPath = path.replace('\\', '/');
    		
    		return fixedPath;
    	} else {
    		return null;
    	}
    }
}
