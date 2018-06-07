package org.cishell.templates.guibuilder;

import org.cishell.templates.wizards.utilities.ParameterUtilities;
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
import org.osgi.service.metatype.AttributeDefinition;

public class AttributeDefinitionEditor extends Dialog {
    private EditableAttributeDefinition attributeDefinition;
    private Text idText;
    private Text nameText;
    private Text descriptionText;
    private Text defaultValueText;
    private Text minimumValueText;
    private Text maximumValueText;
    private Combo selectedTypeCombo;
    
    public AttributeDefinitionEditor(
    		Composite parent,
    		EditableAttributeDefinition attributeDefinition) {
        this(parent.getShell(), attributeDefinition);
    }
    
    private AttributeDefinitionEditor(
    		Shell parentShell,
    		EditableAttributeDefinition attributeDefinition) {
        super(parentShell);

        this.attributeDefinition = attributeDefinition;
    }

    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite)super.createDialogArea(parent);
        
        Composite panel = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        panel.setLayout(gridLayout);
        
        this.idText = createNewTextInput(panel, ParameterUtilities.ID_LABEL);
        this.idText.setText(attributeDefinition.getID());
        
        this.nameText = createNewTextInput(panel, ParameterUtilities.NAME_LABEL);
        this.nameText.setText(attributeDefinition.getName());
        
        this.selectedTypeCombo =
        	newListInput(panel, ParameterUtilities.INPUT_TYPE_LABEL);
        this.selectedTypeCombo.setItems(ParameterUtilities.TYPE_LABELS);
        
        this.descriptionText =
        	createNewTextInput(panel, ParameterUtilities.DESCRIPTION_LABEL);
        this.descriptionText.setText(attributeDefinition.getDescription());
        
        this.defaultValueText =
        	createNewTextInput(panel, ParameterUtilities.DEFAULT_VALUE_LABEL);
        this.defaultValueText.setText(
        	attributeDefinition.getActualDefaultValue());
        
        this.minimumValueText =
        	createNewTextInput(panel, ParameterUtilities.MINIMUM_VALUE_LABEL);
        this.minimumValueText.setText(attributeDefinition.getMinValue());
        
        this.maximumValueText =
        	createNewTextInput(panel, ParameterUtilities.MAXIMUM_VALUE_LABEL);
        this.maximumValueText.setText(attributeDefinition.getMaxValue());
        
        this.selectedTypeCombo.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent selectionEvent) {
                widgetSelected(selectionEvent);
            }

            public void widgetSelected(SelectionEvent selectionEvent) {
                int selectionIndex = selectedTypeCombo.getSelectionIndex();
                
                boolean hadFileOrDirectoryType =
                	ParameterUtilities.attributeHasFileOrDirectoryType(
                		AttributeDefinitionEditor.this.attributeDefinition);
                
                if (selectionIndex ==
                		ParameterUtilities.TYPE_VALUE_INDEX_FILE) {
                    AttributeDefinitionEditor.this.defaultValueText.setText(
                    	ParameterUtilities.DEFAULT_FILE_VALUE);
                } else if (selectionIndex ==
                		ParameterUtilities.TYPE_VALUE_INDEX_DIRECTORY) {
                    AttributeDefinitionEditor.this.defaultValueText.setText(
                    	ParameterUtilities.DEFAULT_DIRECTORY_VALUE);
                } else if (hadFileOrDirectoryType) {
                	AttributeDefinitionEditor.this.defaultValueText.setText(
                		"");
                }
                
                /*
                 * Enable the default value if the selectedTypeCombo of value
                 *  is not file or directory.
                 */
                boolean defaultValueTextEnabledStatus =
                	shouldEnableDefaultValueTextBasedOnSelectionIndex(
                		selectionIndex);
                AttributeDefinitionEditor.this.defaultValueText.setEnabled(
                	defaultValueTextEnabledStatus);
                
                /*
                 * Enable the minimum and maximum values if the
                 *  selectedTypeCombo of value is numeric.
                 */
                boolean minAndMaxTextsEnabledStatus =
                	shouldEnableMinAndMaxTextsBasedOnSelectionIndex(
                		selectionIndex);
                AttributeDefinitionEditor.this.minimumValueText.setEnabled(
                	minAndMaxTextsEnabledStatus);
                AttributeDefinitionEditor.this.maximumValueText.setEnabled(
                	minAndMaxTextsEnabledStatus);
            }
		});
        
        if (ParameterUtilities.attributeHasFileType(
        		this.attributeDefinition)) {
        	this.selectedTypeCombo.select(
        		ParameterUtilities.TYPE_VALUE_INDEX_FILE);
        } else if (ParameterUtilities.attributeHasDirectoryType(
        		this.attributeDefinition)) {
        	this.selectedTypeCombo.select(
        		ParameterUtilities.TYPE_VALUE_INDEX_DIRECTORY);
        } else {
        	for (int ii = 0;
        			ii < ParameterUtilities.TYPE_VALUES.length;
        			ii++) {
            	if (ParameterUtilities.TYPE_VALUES[ii] ==
            			this.attributeDefinition.getType()) {
                	this.selectedTypeCombo.select(ii);

                	break;
            	}
        	}
        }
        
        /*
         * This is necessary because the
         *  this.selectedTypeCombo.select(ii);
         *  line above doesn't actually fire the selected event.
         */
        boolean defaultValueTextEnabledStatus =
        	shouldEnableDefaultValueTextBasedOnAttribute(
        		this.attributeDefinition);
        this.defaultValueText.setEnabled(defaultValueTextEnabledStatus);
        boolean minAndMaxTextsEnabledStatus =
        	shouldEnableMinAndMaxTextsBasedOnAttribute(
        		this.attributeDefinition);
        AttributeDefinitionEditor.this.minimumValueText.setEnabled(
        	minAndMaxTextsEnabledStatus);
        AttributeDefinitionEditor.this.maximumValueText.setEnabled(
        	minAndMaxTextsEnabledStatus);
        
        composite.getShell().setText("Parameter Editor");
        
        return composite;
    }
    
    private Text createNewTextInput(Composite panel, String text) {
        Label label = new Label(panel, SWT.NONE);
        label.setText(text);
        GridData data = new GridData(SWT.LEFT, SWT.BEGINNING, false, false);
        label.setLayoutData(data);
        
        Text input = new Text(panel, SWT.BORDER);
        data = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        input.setLayoutData(data);
        
        return input;
    }
    
    private Combo newListInput(Composite panel, String text) {
        Label label = new Label(panel, SWT.NONE);
        label.setText(text);
        GridData data = new GridData(SWT.LEFT, SWT.BEGINNING, false, false);
        label.setLayoutData(data);
        
        Combo list = new Combo(panel, SWT.DROP_DOWN | SWT.READ_ONLY);
        
        return list;
    }
    
    /*
     * TODO: Validate the default, min, and max fields depending on the type.
     *  This may require setting up a non-editable string field for errors and
     *  throwing an exception back to here from the validation methods,
     *  or something.
     */
    protected void okPressed() {
        this.attributeDefinition.setID(cleanText(this.idText.getText()));
        this.attributeDefinition.setName(cleanText(this.nameText.getText()));
        this.attributeDefinition.setType(
        	ParameterUtilities.TYPE_VALUES[
				this.selectedTypeCombo.getSelectionIndex()]);
        this.attributeDefinition.setDescription(
        	cleanText(this.descriptionText.getText(), false));
        // TODO: cleanDefaultValue
        this.attributeDefinition.setDefaultValue(
        	cleanText(this.defaultValueText.getText()));
        // TODO: cleanNumberValue
        this.attributeDefinition.setMinValue(
        	cleanText(this.minimumValueText.getText()));
        this.attributeDefinition.setMaxValue(
        	cleanText(this.maximumValueText.getText()));
        
        super.okPressed();
    }
    
    private String cleanText(String text) {
        return cleanText(text, true);
    }
    
    private String cleanText(String text, boolean canHaveZeroLength) {
    	String cleanedText =
    		text.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    	
    	if (!canHaveZeroLength && cleanedText.length() == 0) {
    		return " ";
    	} else {
    		return cleanedText;
    	}
    }
    
    private static boolean shouldEnableDefaultValueTextBasedOnSelectionIndex(
    		int selectionIndex) {
    	switch (selectionIndex) {
    	default:
    		return true;
    	case ParameterUtilities.TYPE_VALUE_INDEX_FILE:
    	case ParameterUtilities.TYPE_VALUE_INDEX_DIRECTORY:
    		return false;
    	}
    }
    
    private static boolean shouldEnableDefaultValueTextBasedOnAttribute(
    		EditableAttributeDefinition attribute) {
    	return !ParameterUtilities.attributeHasFileOrDirectoryType(attribute);
    }
    
    private static boolean shouldEnableMinAndMaxTextsBasedOnSelectionIndex(
    		int selectionIndex) {
    	switch (selectionIndex) {
    	default:
    		return true;
    	case ParameterUtilities.TYPE_VALUE_INDEX_STRING:
    	case ParameterUtilities.TYPE_VALUE_INDEX_BOOLEAN:
    	case ParameterUtilities.TYPE_VALUE_INDEX_CHARACTER:
    	case ParameterUtilities.TYPE_VALUE_INDEX_BYTE:
    	case ParameterUtilities.TYPE_VALUE_INDEX_FILE:
    	case ParameterUtilities.TYPE_VALUE_INDEX_DIRECTORY:
    		return false;
    	}
    }
    
    private static boolean shouldEnableMinAndMaxTextsBasedOnAttribute(
    		EditableAttributeDefinition attribute) {
    	switch (attribute.getType()) {
    	default:
    		return true;
    	case AttributeDefinition.STRING:
    	case AttributeDefinition.BOOLEAN:
    	case AttributeDefinition.CHARACTER:
    	case AttributeDefinition.BYTE:
    		return false;
    	}
    }
}
