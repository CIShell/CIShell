package org.cishell.utility.swt.model.datasynchronizer;
//package org.cishell.utility.swt.model.datasynchronizer;
//
//import org.eclipse.swt.SWT;
//
//public class TimeDataSynchronizer implements ModelDataSynchronizer<org.joda.time.DateTime> {
//	private org.eclipse.swt.widgets.DateTime timeSelector;
//
//	public TimeDataSynchronizer(
//			org.eclipse.swt.widgets.DateTime timeSelector, org.joda.time.DateTime time) {
//		this.timeSelector = timeSelector;
//		synchronizeToGUI(time);
//	}
//
//	public int swtUpdateListenerCode() {
//		return SWT.Selection;
//	}
//
//	public org.joda.time.DateTime value() {
//		return new org.joda.time.DateTime(
//			0,
//			0,
//			0,
//			this.timeSelector.getHours(),
//			this.timeSelector.getMinutes(),
//			this.timeSelector.getSeconds(),
//			0);
//	}
//
//	public org.joda.time.DateTime synchronizeFromGUI() {
//		return value();
//	}
//
//	public org.joda.time.DateTime synchronizeToGUI(org.joda.time.DateTime time) {
//		this.timeSelector.setTime(
//			time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute());
//
//		return value();
//	}
//
//	public org.joda.time.DateTime reset(org.joda.time.DateTime defaultValue) {
//		return synchronizeToGUI(defaultValue);
//	}
//}