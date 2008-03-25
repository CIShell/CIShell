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
package org.cishell.templates.guibuilder;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class AttributeDefinitionEditor extends Dialog {
    protected EditableAttributeDefinition attr;
    public static final String[] TYPE_LABELS = new String[]{
        "String","Integer","Long","Short","Double","Float","Boolean", "Char", 
        "Byte", "File", "Directory"
    };
    public static final int[] TYPE_VALUES = new int[] {
        1,3,2,4,7,8,11,5,6,1,1
    };
    
    protected Text id;
    protected Text name;
    protected Text description;
    protected Text defaultValue;
    protected Combo type;
    
    protected AttributeDefinitionEditor(Composite parent, EditableAttributeDefinition attr) {
        this(parent.getShell(), attr);
    }
    
    protected AttributeDefinitionEditor(Shell parentShell, EditableAttributeDefinition attr) {
        super(parentShell);

        this.attr = attr;
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2,false);
        panel.setLayout(gridLayout);
        
        id = newTextInput(panel,"Unique ID");
        id.setText(attr.getID());
        
        name = newTextInput(panel,"Name");
        name.setText(attr.getName());
        
        description = newTextInput(panel,"Description");
        description.setText(attr.getDescription());
        
        defaultValue = newTextInput(panel, "Default Value");
        defaultValue.setText(attr.getDefaultValue()[0]);
        
        type = newListInput(panel, "Input Type");
        type.setItems(TYPE_LABELS);
        
        type.addSelectionListener(new SelectionListener(){
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            public void widgetSelected(SelectionEvent e) {
                int i = type.getSelectionIndex();
                if (i == 9) { //file
                    defaultValue.setText("file:");
                }
                if (i == 10) { //directory
                    defaultValue.setText("directory:");
                }
                defaultValue.setEnabled(i != 9 && i != 10);
            }});
        
        for (int i=0; i < TYPE_VALUES.length; i++) {
            if (TYPE_VALUES[i] == attr.getType()) {
                type.select(i);
                break;
            }
        }
        
        composite.getShell().setText("Parameter Editor");
        
        return composite;
    }
    
    protected Text newTextInput(Composite panel, String text) {
        Label label = new Label(panel, SWT.NONE);
        label.setText(text);
        GridData data = new GridData(SWT.LEFT, SWT.BEGINNING, false, false);
        label.setLayoutData(data);
        
        Text input = new Text(panel, SWT.NONE);
        data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        input.setLayoutData(data);
        
        return input;
    }
    
    protected Combo newListInput(Composite panel, String text) {
        Label label = new Label(panel, SWT.NONE);
        label.setText(text);
        GridData data = new GridData(SWT.LEFT, SWT.BEGINNING, false, false);
        label.setLayoutData(data);
        
        Combo list = new Combo(panel, SWT.DROP_DOWN | SWT.READ_ONLY);
        
        return list;
    }
    
    protected void okPressed() {
        attr.setID(cleanText(id.getText()));
        attr.setName(cleanText(name.getText()));
        String desc = cleanText(description.getText());
        if (desc.length() == 0) {
            desc = " ";
        }
        attr.setDescription(desc);
        attr.setDefaultValue(new String[]{cleanText(defaultValue.getText())});
        attr.setType(TYPE_VALUES[type.getSelectionIndex()]);
        
        super.okPressed();
    }
    
    protected String cleanText(String text) {
        text = text.replaceAll("<", "&lt;");
        text = text.replaceAll(">", "&gt;");
        
        return text;
    }
}
