package org.cishell.utilities.swt.model.datasynchronizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;

public class TextDataSynchronizer implements ModelDataSynchronizer<String> {
	private Text text;

	public TextDataSynchronizer(Text text, String value) {
		this.text = text;

		this.text.setText(value);
	}

	public int swtUpdateListenerCode() {
		return SWT.Modify;
	}

	public String value() {
		return this.text.getText();
	}

	public String synchronizeFromGUI() {
		return value();
	}

	public String synchronizeToGUI(String value) {
		this.text.setText(value);

		return value();
	}

	public String reset(String defaultValue) {
		return synchronizeToGUI(defaultValue);
	}
}