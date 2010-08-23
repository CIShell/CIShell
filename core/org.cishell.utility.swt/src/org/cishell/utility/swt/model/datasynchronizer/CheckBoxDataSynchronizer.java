package org.cishell.utility.swt.model.datasynchronizer;

import org.cishell.utility.datastructure.datamodel.ModelDataSynchronizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

public class CheckBoxDataSynchronizer implements ModelDataSynchronizer<Boolean> {
	private Button checkBox;

	public CheckBoxDataSynchronizer(Button checkBox, boolean on) {
		this.checkBox = checkBox;
		this.checkBox.setSelection(on);
	}

	public int updateListenerCode() {
		return SWT.Selection;
	}

	public Boolean value() {
		return this.checkBox.getSelection();
	}

	public Boolean synchronizeFromGUI() {
		return value();
	}

	public Boolean synchronizeToGUI(Boolean value) {
		this.checkBox.setSelection(value);

		return value;
	}

	public Boolean reset(Boolean defaultValue) {
		return synchronizeToGUI(defaultValue);
	}
}