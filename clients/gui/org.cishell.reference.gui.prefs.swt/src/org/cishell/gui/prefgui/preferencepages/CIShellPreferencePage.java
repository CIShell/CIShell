package org.cishell.gui.prefgui.preferencepages;

import java.io.IOException;

import org.cishell.gui.prefgui.customfields.DoubleFieldEditor;
import org.cishell.gui.prefgui.customfields.FloatFieldEditor;
import org.cishell.reference.gui.prefs.swt.CIShellPreferenceStore;
import org.cishell.reference.prefs.admin.PreferenceAD;
import org.cishell.reference.prefs.admin.PreferenceOCD;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PathEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.ObjectClassDefinition;

public class CIShellPreferencePage extends FieldEditorPreferencePage {

	private PreferenceOCD prefOCD;
	
	private LogService log;
	
    public CIShellPreferencePage(LogService log, PreferenceOCD prefOCD,
    		CIShellPreferenceStore prefStore) {
    	super(FieldEditorPreferencePage.FLAT);
    	this.setTitle(prefOCD.getName());
    	
    	this.prefOCD = prefOCD;
    	
    	this.setPreferenceStore(prefStore);
	}
	
	protected void createFieldEditors() {
		PreferenceAD[] prefADs = 
    		prefOCD.getPreferenceAttributeDefinitions(ObjectClassDefinition.ALL);
    	
    	for (int ii = 0; ii < prefADs.length; ii++) {
    		PreferenceAD prefAD = prefADs[ii];
    		
    		int attrType = prefAD.getPreferenceType();
    		if (attrType == PreferenceAD.BOOLEAN) {
    			BooleanFieldEditor bField = 
    				new BooleanFieldEditor(prefAD.getID(), prefAD.getName(), getFieldEditorParent());
    			addField(bField);
    		} else if (attrType == PreferenceAD.INTEGER) {			
    			IntegerFieldEditor iField =
    				new IntegerFieldEditor(prefAD.getID(), prefAD.getName(), getFieldEditorParent());
    			addField(iField);
    		} else if (attrType == PreferenceAD.CHOICE) {   			
    			String[] optionLabels = prefAD.getOptionLabels();
    			String[] optionValues = prefAD.getOptionValues();		
    			String [][] labelAndValues = new String[optionLabels.length][2];
    			
    			for (int jj = 0; jj < labelAndValues.length; jj++) {
    				labelAndValues[jj][0] = optionLabels[jj];
    				labelAndValues[jj][1] = optionValues[jj];
    			}
    			
    			RadioGroupFieldEditor rgField 
    			= new RadioGroupFieldEditor(
    					prefAD.getID(),
    					prefAD.getName(),
    					1,
    					labelAndValues,
    					getFieldEditorParent(),
    					true);
    			addField(rgField);
    		} else if (attrType == PreferenceAD.FONT) {		
    			FontFieldEditor foField = 
    				new FontFieldEditor(prefAD.getID(), prefAD.getName(), getFieldEditorParent());
    			addField(foField);
    		} else if (attrType == PreferenceAD.DIRECTORY) {		
    			DirectoryFieldEditor dField = 
    				new DirectoryFieldEditor(prefAD.getID(), prefAD.getName(), getFieldEditorParent());
    			dField.setEmptyStringAllowed(true);
    			addField(dField);
    		} else if (attrType == PreferenceAD.FILE) {			
    			FileFieldEditor fiField = 
    				new FileFieldEditor(prefAD.getID(), prefAD.getName(), getFieldEditorParent());
    			fiField.setEmptyStringAllowed(true);
    			addField(fiField);
    		} else if (attrType == PreferenceAD.PATH) {			
    			PathEditor pField = 
    				new PathEditor(prefAD.getID(), prefAD.getName(), prefAD.getName(), getFieldEditorParent());
    			addField(pField);
    		} else if (attrType == PreferenceAD.TEXT) {		
    			StringFieldEditor sField = 
    				new StringFieldEditor(prefAD.getID(), prefAD.getName(), getFieldEditorParent());
    			addField(sField);
    		} else if (attrType == PreferenceAD.DOUBLE) {		
    			DoubleFieldEditor dField = 
    				new DoubleFieldEditor(prefAD.getID(), prefAD.getName(), getFieldEditorParent());
    			addField(dField);
    		} else if (attrType == PreferenceAD.FLOAT) {			
    			FloatFieldEditor fField = 
    				new FloatFieldEditor(prefAD.getID(), prefAD.getName(), getFieldEditorParent());
    			addField(fField);
    		} else if (attrType == PreferenceAD.COLOR) {			
    			ColorFieldEditor cField = 
    				new ColorFieldEditor(prefAD.getID(), prefAD.getName(), getFieldEditorParent());
    			addField(cField);
    		}
    	}
	}
	
	public void performApply() {
		super.performApply(); 
		//WARNING: this will not work if the PreferenceStore is ever not the CIShellPreferenceStore
		
		/*
		 * necessary because we need the preference store to actually save in order to
		 * distribute the changes we have made, unlike the usual way a preferenceStore operates
		 * where you can simply set the changes to the preference store and they propagate correctly.
		 */
		
		try {
			if (this.getPreferenceStore() instanceof CIShellPreferenceStore) {
				CIShellPreferenceStore realPrefStore = (CIShellPreferenceStore) this.getPreferenceStore();
				realPrefStore.save();
			}
		} catch (ClassCastException e) {
			super.performApply(); 
		} catch (IOException e) {
			this.log.log(LogService.LOG_WARNING, "Unable to save preferences due to I/O Exception", e);
		}
	}
}
