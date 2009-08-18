package org.cishell.templates.wizards.staticexecutable;

import java.util.HashMap;
import java.util.Map;

import org.cishell.templates.guibuilder.BuilderDelegate;
import org.cishell.templates.wizards.utilities.ParameterUtilities;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class StaticExecutableInputDataDelegate
		implements BuilderDelegate {
	public static final String ID_LABEL = "ID";
	public static final String MIME_TYPE_LABEL = "Mime Type";

	public static final String[] COLUMN_LABELS = new String[] {
		ID_LABEL,
		MIME_TYPE_LABEL
	};
    
    private Map idToInputDataItemMap;
    private int lastID;
    private Composite parent;
    
    public StaticExecutableInputDataDelegate(Composite parent) {
        this.parent = parent;
        idToInputDataItemMap = new HashMap();
        lastID = 0;
    }
    
    public Composite getParent() {
    	return this.parent;
    }
    
    public Map getIDToInputDataItemMap() {
    	return this.idToInputDataItemMap;
    }

    public String[] createItem() {
        InputDataItem inputDataItem =
        	new InputDataItem("Mime Type for Input " + this.lastID);
        
        boolean success = editInputDataItem(inputDataItem);
        
        if (success) {
            idToInputDataItemMap.put("" + this.lastID, inputDataItem);
            
            String[] item = new String[] {
            	Integer.toString(this.lastID),
            	inputDataItem.getMimeType()
            };
            
            this.lastID++;
            
            return item;
        } else {
        	return null;
        }
    }

    public void edit(TableItem tableItem) {
        String itemID = tableItem.getText(0);
        
        InputDataItem inputDataItem =
        	(InputDataItem)idToInputDataItemMap.get(itemID);
        
        boolean success = editInputDataItem(inputDataItem);

        if (success) {
        	tableItem.setText(1, inputDataItem.getMimeType());
        }
    }
    
    protected boolean editInputDataItem(InputDataItem inputDataItem) {
        InputDataItemEditor inputDataItemEditor =
        	new InputDataItemEditor(parent, inputDataItem);
        int returnCode = inputDataItemEditor.open();
        
        if (returnCode == Dialog.OK) {
            return true;
        } else {
        	return false;
        }
    }

    protected String getTypeString(int type) {
        String typeString = "Unknown";
        
        for (int ii = 0; ii < ParameterUtilities.TYPE_VALUES.length; ii++) {
            if (ParameterUtilities.TYPE_VALUES[ii] == type) {
                typeString = ParameterUtilities.TYPE_LABELS[ii];
                break;
            }
        }
        
        return typeString;
    }

    public String[] getColumns() {
        return COLUMN_LABELS;
    }
}