package org.cishell.templates.wizards.staticexecutable;

import java.util.HashMap;
import java.util.Map;

import org.cishell.templates.guibuilder.BuilderDelegate;
import org.cishell.templates.wizards.utilities.ParameterUtilities;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class StaticExecutableOutputDataDelegate
		implements BuilderDelegate {
	public static final String ID_LABEL = "ID";
	public static final String FILE_NAME_LABEL = "File Name";
	public static final String LABEL_LABEL = "Label";
	public static final String DATA_TYPE_LABEL = "Data Type";
	public static final String MIME_TYPE_LABEL = "Mime Type";

	public static final String[] COLUMN_LABELS = new String[] {
		ID_LABEL,
		FILE_NAME_LABEL,
		LABEL_LABEL,
		DATA_TYPE_LABEL,
		MIME_TYPE_LABEL
	};
    
    private Map idToOutputDataItemMap;
    private int lastID;
    private Composite parent;
    
    public StaticExecutableOutputDataDelegate(Composite parent) {
        this.parent = parent;
        idToOutputDataItemMap = new HashMap();
        lastID = 0;
    }
    
    public Composite getParent() {
    	return this.parent;
    }
    
    public Map getIDToOutputDataItemMap() {
    	return this.idToOutputDataItemMap;
    }

    public String[] createItem() {
        OutputDataItem outputDataItem = new OutputDataItem();
        outputDataItem.setFileName("File Name for Output " + this.lastID);
        outputDataItem.setLabel("Output File " + this.lastID);
        outputDataItem.setDataType("Data Type for Output " + this.lastID);
        outputDataItem.setMimeType("Mime Type for Output " + this.lastID);
        
        boolean success = editOutputDataItem(outputDataItem);
        
        if (success) {
            idToOutputDataItemMap.put(Integer.toString(this.lastID),
            						  outputDataItem);
            
            String[] item = new String[] {
            	"" + this.lastID,
            	outputDataItem.getFileName(),
            	outputDataItem.getLabel(),
            	outputDataItem.getDataType(),
            	outputDataItem.getMimeType()
            };
            
            this.lastID++;
            
            return item;
        } else {
        	return null;
        }
    }

    public void edit(TableItem tableItem) {
        String itemID = tableItem.getText(0);
        
        OutputDataItem outputDataItem =
        	(OutputDataItem)idToOutputDataItemMap.get(itemID);
        
        boolean success = editOutputDataItem(outputDataItem);

        if (success) {
        	tableItem.setText(1, outputDataItem.getFileName());
        	tableItem.setText(2, outputDataItem.getLabel());
        	tableItem.setText(3, outputDataItem.getDataType());
        	tableItem.setText(4, outputDataItem.getMimeType());
        }
    }
    
    protected boolean editOutputDataItem(OutputDataItem outputDataItem) {
        OutputDataItemEditor outputDataItemEditor =
        	new OutputDataItemEditor(parent, outputDataItem);
        int returnCode = outputDataItemEditor.open();
        
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