package org.cishell.gui.prefgui.customfields;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class DoubleFieldEditor extends StringFieldEditor {
	
	public DoubleFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		 setEmptyStringAllowed(false);
	}
	
	protected void doLoad() {
		  Text text = getTextControl();
	        if (text != null) {
	            double value = getPreferenceStore().getDouble(getPreferenceName());
	            text.setText("" + value);
	        }
	}
	
	protected void doLoadDefault() {
		  Text text = getTextControl();
	        if (text != null) {
	            double value = getPreferenceStore().getDefaultDouble(getPreferenceName());
	            text.setText("" + value);//$NON-NLS-1$
	        }
	        valueChanged();
	}
	
	protected void doStore() {
		 Text text = getTextControl();
	        if (text != null) {
	            Double i = Double.valueOf(text.getText());
	            getPreferenceStore().setValue(getPreferenceName(), i.doubleValue());
	        }
	}
	
	public double getDoubleValue() {
		return Double.parseDouble(this.getStringValue());
	}
	
	public boolean checkState() {
		try {
		Double.parseDouble(this.getStringValue());
		return true;
		} catch (NumberFormatException e) {
			//if parsing throws an error, it's invalid.
			return false;
		}
	}
}
