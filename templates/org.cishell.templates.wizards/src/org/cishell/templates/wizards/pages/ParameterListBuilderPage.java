/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.wizards.pages;

import org.cishell.templates.guibuilder.EditableAttributeDefinition;
import org.cishell.templates.guibuilder.ParameterListBuilder;
import org.cishell.templates.staticexecutable.providers.InputParameterProvider;
import org.cishell.templates.wizards.utilities.ParameterUtilities;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.metatype.AttributeDefinition;

/*
 * This page provides algorithm creators with an interface for specifying the
 *  GUI-based input parameters that their algorithms accept.
 */
public class ParameterListBuilderPage extends WizardPage
		implements InputParameterProvider {
    ParameterListBuilder builder;

    public ParameterListBuilderPage(String pageName) {
        super(pageName);
    }

    public void createControl(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout(1, false));
        
        builder = new ParameterListBuilder(panel);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        builder.getComposite().setLayoutData(gridData);
        
        setControl(panel);
    }
    
    public EditableAttributeDefinition[] getAttributeDefinitions() {
        return builder.getCreatedAttributes();
    }
    
    public String toOutputString() {
        EditableAttributeDefinition[] attributes = getAttributeDefinitions();
        String output = "";
        
        for (int ii = 0; ii < attributes.length; ii++) {
            output +=
            	"\t\t<AD name=\"" + attributes[ii].getName()+"\" " +
            	"id=\"" + attributes[ii].getID() + "\" " +
            	"type=\"" + getTypeString(attributes[ii]) + "\" " +
            	"description=\"" + attributes[ii].getDescription() + "\" " +
            	"default=\"" + attributes[ii].getActualDefaultValue() + "\"";
            
            switch (attributes[ii].getType()) {
            /*TODO: make explicit comment
             * There are more number types than non-number types, so make
             *  switching on number types the default.
             */
            case AttributeDefinition.BOOLEAN:
            case AttributeDefinition.CHARACTER:
            case AttributeDefinition.STRING:
            	break;
            default:
            	if (!attributes[ii].getMinValue().equals("")) {
            		output += " min=\"" + attributes[ii].getMinValue() + "\"";
            	}
            
            	if (!attributes[ii].getMaxValue().equals("")) {
            		output += " max=\"" + attributes[ii].getMaxValue() + "\"";
            	}
            }
            
            output += "/>\n";
        }
        
        return output;
    }
    
    private String getTypeString(AttributeDefinition attribute) {
        String typeString = "Unknown";
        int type = attribute.getType();
        
        for (int ii = 0; ii < ParameterUtilities.TYPE_VALUES.length; ii++) {
            if (ParameterUtilities.TYPE_VALUES[ii] == type) {
                typeString = ParameterUtilities.TYPE_LABELS[ii];
                
                break;
            }
        }
        
        return typeString;
    }
    
    public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		
		new ParameterListBuilderPage("Testing").createControl(shell);
		
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();
    }
    
    public AttributeDefinition[] getInputParameters() {
    	return getAttributeDefinitions();
    }
}
