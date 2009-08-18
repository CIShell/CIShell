package org.cishell.templates.guibuilder;

import java.util.HashMap;
import java.util.Map;

import org.cishell.templates.wizards.utilities.ParameterUtilities;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.osgi.service.metatype.AttributeDefinition;

public class ParameterBuilderDelegate implements BuilderDelegate {
    public static final String[] COLUMN_LABELS = new String[] {
    	"ID",
    	"Label",
    	"Type",
    	"Description",
    	"Default",
    	"Minimum Value (Number Types Only)",
    	"Maximum Value (Number Types Only)"
    };
    
    public static final String DEFAULT_ATTRIBUTE_DEFINITION_NAME =
    	"Parameter Label";
    public static final String DEFAULT_ATTRIBUTE_DEFINITION_DESCRIPTION =
    	"Parameter Description";
    public static final String DEFAULT_ATTRIBUTE_DEFINITION_VALUE =
    	"Default Value";
    public static final int DEFAULT_ATTRIBUTE_DEFINITION_TYPE =
    	AttributeDefinition.STRING;
    public static final String DEFAULT_ATTRIBUTE_DEFINITION_MINIMUM_VALUE = "";
    public static final String DEFAULT_ATTRIBUTE_DEFINITION_MAXIMUM_VALUE = "";
    
    private Map idToAttributeMap;
    private int lastID;
    private Composite parent;
    
    public ParameterBuilderDelegate(Composite parent) {
        this.parent = parent;
        this.idToAttributeMap = new HashMap();
        this.lastID = 0;
    }

    public String[] createItem() {
        EditableAttributeDefinition attributeDefinition =
        	new EditableAttributeDefinition();
        this.lastID++;
        
        attributeDefinition.setID(Integer.toString(lastID));
        attributeDefinition.setName(DEFAULT_ATTRIBUTE_DEFINITION_NAME);
        attributeDefinition.setType(DEFAULT_ATTRIBUTE_DEFINITION_TYPE);
        attributeDefinition.setDescription(
        	DEFAULT_ATTRIBUTE_DEFINITION_DESCRIPTION);
        attributeDefinition.setDefaultValue(
        	DEFAULT_ATTRIBUTE_DEFINITION_VALUE);
        attributeDefinition.setMinValue(
        	DEFAULT_ATTRIBUTE_DEFINITION_MINIMUM_VALUE);
        attributeDefinition.setMaxValue(
        	DEFAULT_ATTRIBUTE_DEFINITION_MAXIMUM_VALUE);
        
        boolean newItemWasCreated =
        	editAttributeDefinition(attributeDefinition);
        
        if (newItemWasCreated) {
            idToAttributeMap.put(attributeDefinition.getID(),
            					 attributeDefinition);
            
            String[] item = new String[]{
                    attributeDefinition.getID(),
                    attributeDefinition.getName(),
                    getTypeString(attributeDefinition),
                    attributeDefinition.getDescription(),
                    attributeDefinition.getActualDefaultValue(),
                    attributeDefinition.getMinValue(),
                    attributeDefinition.getMaxValue()
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
        
        if (editAttributeDefinition(attribute)) {
        	item.setText(0, attribute.getID());
        	item.setText(1, attribute.getName());
        	item.setText(2, getTypeString(attribute));
        	item.setText(3, attribute.getDescription());
        	item.setText(4, attribute.getActualDefaultValue());
        	item.setText(5, attribute.getMinValue());
        	item.setText(6, attribute.getMaxValue());
        }
    }
    
    protected boolean editAttributeDefinition(
    		EditableAttributeDefinition attribute) {
        AttributeDefinitionEditor attributeDefinitionEditor =
        	new AttributeDefinitionEditor(parent, attribute);
        int returnCode = attributeDefinitionEditor.open();
        
        if (returnCode == Dialog.OK) {
            idToAttributeMap.put(attribute.getID(), attribute);
        }
        
        return returnCode == Dialog.OK;
    }

    protected String getTypeString(EditableAttributeDefinition attribute) {
    	if (ParameterUtilities.attributeHasFileType(attribute)) {
    		return "File";
    	} else if (ParameterUtilities.attributeHasDirectoryType(
    			attribute)) {
    		return "Directory";
    	} else {
    		int type = attribute.getType();
    		
        	for (int ii = 0; ii < ParameterUtilities.TYPE_VALUES.length; ii++) {
            	if (ParameterUtilities.TYPE_VALUES[ii] == type) {
                	return ParameterUtilities.TYPE_LABELS[ii];
            	}
        	}
    	}
        
        return "Unknown Type";
    }
    
    public EditableAttributeDefinition getAttributeDefinition(String id) {
        return (EditableAttributeDefinition)idToAttributeMap.get(id);
    }

    public String[] getColumns() {
        return COLUMN_LABELS;
    }
}
