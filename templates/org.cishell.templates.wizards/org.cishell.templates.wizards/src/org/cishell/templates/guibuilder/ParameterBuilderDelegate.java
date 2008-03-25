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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.service.metatype.AttributeDefinition;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class ParameterBuilderDelegate implements BuilderDelegate {
    protected static final String[] COLUMN_LABELS = new String[]{"id","Type","Label"};
    protected Map idToAttrMap;
    protected int lastID;
    protected Composite parent;
    
    public ParameterBuilderDelegate(Composite parent) {
        this.parent = parent;
        idToAttrMap = new HashMap();
        lastID = 0;
    }

    /**
     * @see org.cishell.templates.guibuilder.BuilderDelegate#createItem()
     */
    public String[] createItem() {
        EditableAttributeDefinition attr = new EditableAttributeDefinition();
        lastID++;
        attr.setID(""+lastID);
        attr.setName("Parameter Label");
        attr.setDescription("Parameter Description");
        attr.setDefaultValue(new String[]{"Default value"});
        attr.setType(AttributeDefinition.STRING);
        
        boolean success = edit(attr);
        
        if (success) {
            idToAttrMap.put(attr.getID(), attr);
            
            String[] item = new String[]{
                    attr.getID(),
                    getTypeString(attr.getType()),
                    attr.getName()
            };
            return item;
        } else {
            return null;
        }
    }

    /**
     * @see org.cishell.templates.guibuilder.BuilderDelegate#edit(org.eclipse.swt.widgets.TableItem)
     */
    public void edit(TableItem item) {
        String id = item.getText(0);
        
        EditableAttributeDefinition attr = 
            (EditableAttributeDefinition) idToAttrMap.get(id);
        
        edit(attr);

        item.setText(0, attr.getID());
        item.setText(1, getTypeString(attr.getType()));
        item.setText(2, attr.getName());
    }
    
    protected boolean edit(EditableAttributeDefinition attr) {
        AttributeDefinitionEditor editor = new AttributeDefinitionEditor(parent, attr);
        int returnCode = editor.open();
        
        if (returnCode == Dialog.OK) {
            idToAttrMap.put(attr.getID(), attr);
        }
        
        return returnCode == Dialog.OK;
    }

    protected String getTypeString(int type) {
        String str = "Unknown";
        
        for (int i=0; i < AttributeDefinitionEditor.TYPE_VALUES.length; i++) {
            if (AttributeDefinitionEditor.TYPE_VALUES[i] == type) {
                str = AttributeDefinitionEditor.TYPE_LABELS[i];
                break;
            }
        }
        
        return str;
    }
    
    public EditableAttributeDefinition getAttributeDefinition(String id) {
        return (EditableAttributeDefinition) idToAttrMap.get(id);
    }
    
    /**
     * @see org.cishell.templates.guibuilder.BuilderDelegate#getColumns()
     */
    public String[] getColumns() {
        return COLUMN_LABELS;
    }
}
