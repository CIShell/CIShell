package org.cishell.gui.prefgui.customfields;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class FloatFieldEditor extends StringFieldEditor {
	
	public FloatFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		 setEmptyStringAllowed(false);
	}
	
	protected void doLoad() {
		  Text text = getTextControl();
	        if (text != null) {
	            float value = getPreferenceStore().getFloat(getPreferenceName());
	            text.setText("" + value);
	        }
	}
	
	protected void doLoadDefault() {
		  Text text = getTextControl();
	        if (text != null) {
	            float value = getPreferenceStore().getDefaultFloat(getPreferenceName());
	            text.setText("" + value);//$NON-NLS-1$
	        }
	        valueChanged();
	}
	
	protected void doStore() {
		 Text text = getTextControl();
	        if (text != null) {
	        	Float i = Float.valueOf(text.getText());
	            getPreferenceStore().setValue(getPreferenceName(), i.floatValue());
	        }
	}
	
	public float getFloatValue() {
		return Float.parseFloat(this.getStringValue());
	}
	
	public boolean checkState() {
		try {
		Float.parseFloat(this.getStringValue());
		return true;
		} catch (NumberFormatException e) {
			//if parsing throws an error, it's invalid.
			return false;
		}
	}
}
