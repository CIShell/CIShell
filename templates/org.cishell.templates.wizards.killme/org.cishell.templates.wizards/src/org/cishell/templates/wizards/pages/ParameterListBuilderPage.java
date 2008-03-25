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

import org.cishell.templates.guibuilder.AttributeDefinitionEditor;
import org.cishell.templates.guibuilder.EditableAttributeDefinition;
import org.cishell.templates.guibuilder.ParameterListBuilder;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.metatype.AttributeDefinition;


public class ParameterListBuilderPage extends WizardPage {
    ParameterListBuilder builder;

    public ParameterListBuilderPage(String pageName) {
        super(pageName);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite panel = new Composite(parent, SWT.NONE);
        panel.setLayout(new GridLayout(1,false));
        
        builder = new ParameterListBuilder(panel);
        GridData gridData = new GridData(SWT.FILL,SWT.FILL,true,true);
        builder.getComposite().setLayoutData(gridData);
        
        setControl(panel);
    }
    
    public EditableAttributeDefinition[] getAttributeDefinitions() {
        return builder.getCreatedAttributes();
    }
    
    public String toOutputString() {
        EditableAttributeDefinition[] attrs = getAttributeDefinitions();
        String output = "";
        
        for (int i=0; i < attrs.length; i++) {
            output += "\t\t<AD name=\""+attrs[i].getName()+"\" "+
                          "id=\""+attrs[i].getID()+"\" "+
                          "type=\""+getTypeString(attrs[i])+"\" "+
                          "description=\""+attrs[i].getDescription()+"\" "+
                          "default=\""+attrs[i].getDefaultValue()[0]+"\"/>\n";
        }
        
        return output;
    }
    
    private String getTypeString(AttributeDefinition attr) {
        String str = "Unknown";
        int type = attr.getType();
        for (int i=0; i < AttributeDefinitionEditor.TYPE_VALUES.length; i++) {
            if (AttributeDefinitionEditor.TYPE_VALUES[i] == type) {
                str = AttributeDefinitionEditor.TYPE_LABELS[i];
                break;
            }
        }
        
        return str;
    }
}
