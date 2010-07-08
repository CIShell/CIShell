package org.cishell.utilities.swt.model.datasynchronizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DateTime;

public class DateDataSynchronizer implements ModelDataSynchronizer<org.joda.time.DateTime> {
	private DateTime dateSelector;

	public DateDataSynchronizer(DateTime dateSelector, org.joda.time.DateTime date) {
		this.dateSelector = dateSelector;
		synchronizeToGUI(date);
	}

	public int swtUpdateListenerCode() {
		return SWT.Selection;
	}

	public org.joda.time.DateTime value() {
		return new org.joda.time.DateTime(
			this.dateSelector.getYear(),
			this.dateSelector.getMonth(),
			this.dateSelector.getDay(),
			0,
			0,
			0,
			0);
	}

	public org.joda.time.DateTime synchronizeFromGUI() {
		return value();
	}

	public org.joda.time.DateTime synchronizeToGUI(org.joda.time.DateTime date) {
		this.dateSelector.setDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth());

		return value();
	}

	public org.joda.time.DateTime reset(org.joda.time.DateTime defaultValue) {
		return synchronizeToGUI(defaultValue);
	}
}