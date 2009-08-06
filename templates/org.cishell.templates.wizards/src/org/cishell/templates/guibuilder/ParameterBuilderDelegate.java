package org.cishell.templates.guibuilder;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.service.metatype.AttributeDefinition;

public class ParameterBuilderDelegate implements BuilderDelegate {
    public static final String[] COLUMN_LABELS = new String[] {
    	"id", "Type", "Label"
    };
    
    private Map idToAttributeMap;
    private int lastID;
    private Composite parent;
    
    public ParameterBuilderDelegate(Composite parent) {
        this.parent = parent;
        idToAttributeMap = new HashMap();
        lastID = 0;
    }

    public String[] createItem() {
        EditableAttributeDefinition attribute = new EditableAttributeDefinition();
        lastID++;
        attribute.setID("" + lastID);
        attribute.setName("Parameter Label");
        attribute.setDescription("Parameter Description");
        attribute.setDefaultValue(new String[] { "Default value" });
        attribute.setType(AttributeDefinition.STRING);
        
        boolean success = edit(attribute);
        
        if (success) {
            idToAttributeMap.put(attribute.getID(), attribute);
            
            String[] item = new String[]{
                    attribute.getID(),
                    getTypeString(attribute.getType()),
                    attribute.getName()
            };
            
            return item;
        } else {
            return null;
        }
    }

    public void edit(TableItem item) {
        String itemID = item.getText(0);
        
        EditableAttributeDefinition attribute = 
            (EditableAttributeDefinition)idToAttributeMap.get(itemID);
        
        edit(attribute);

        item.setText(0, attribute.getID());
        item.setText(1, getTypeString(attribute.getType()));
        item.setText(2, attribute.getName());
    }
    
    protected boolean edit(EditableAttributeDefinition attribute) {
        AttributeDefinitionEditor attributeDefinitionEditor =
        	new AttributeDefinitionEditor(parent, attribute);
        int returnCode = attributeDefinitionEditor.open();
        
        if (returnCode == Dialog.OK) {
            idToAttributeMap.put(attribute.getID(), attribute);
        }
        
        return returnCode == Dialog.OK;
    }

    protected String getTypeString(int type) {
        String typeString = "Unknown";
        
        for (int ii = 0;
        		ii < AttributeDefinitionEditor.TYPE_VALUES.length;
        		ii++) {
            if (AttributeDefinitionEditor.TYPE_VALUES[ii] == type) {
                typeString = AttributeDefinitionEditor.TYPE_LABELS[ii];
                break;
            }
        }
        
        return typeString;
    }
    
    public EditableAttributeDefinition getAttributeDefinition(String id) {
        return (EditableAttributeDefinition)idToAttributeMap.get(id);
    }

    public String[] getColumns() {
        return COLUMN_LABELS;
    }
}
