package org.cishell.utilities.swt.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cishell.utilities.swt.model.datasynchronizer.CheckBoxDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.DateDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.DropDownDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.ModelDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.TimeDataSynchronizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;

public class GUIModel {
	private Map<String, GUIModelField<?>> inputFieldsByName =
		new HashMap<String, GUIModelField<?>>();

	public GUIModel() {
	}

	public Collection<String> getFieldNames() {
		return this.inputFieldsByName.keySet();
	}

	public Collection<GUIModelField<?>> getFields() {
		return this.inputFieldsByName.values();
	}

	public GUIModelField<?> getField(String name) {
		return this.inputFieldsByName.get(name);
	}

	public Button addCheckBox(String name, boolean on, Composite parent, int style) {
		Button checkBox = new Button(parent, style | SWT.CHECK);
		ModelDataSynchronizer<Boolean> dataSynchronizer =
			new CheckBoxDataSynchronizer(checkBox, on);
		GUIModelField<Boolean> field =
			new GUIModelField<Boolean>(name, on, checkBox, dataSynchronizer);
		addField(field);

		return checkBox;
	}

	public Combo addDropDown(
			String name,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, String> optionValuesByLabels,
			Composite parent,
			int style) {
		List<String> orderedOptionLabels = new ArrayList<String>(unorderedOptionLabels);
		Combo dropDown = new Combo(parent, style | SWT.DROP_DOWN);
		ModelDataSynchronizer<String> dataSynchronizer = new DropDownDataSynchronizer(
			dropDown, selectedIndex, orderedOptionLabels, optionValuesByLabels);
		GUIModelField<String> field = new GUIModelField<String>(
			name, orderedOptionLabels.get(selectedIndex), dropDown, dataSynchronizer);
		addField(field);

		return dropDown;
	}

	// TODO: Test this out.
	public DateTime addDate(
			String name, org.joda.time.DateTime date, Composite parent, int style) {
		DateTime dateSelector = new DateTime(parent, style | SWT.DATE);
		ModelDataSynchronizer<org.joda.time.DateTime> dataSynchronizer =
			new DateDataSynchronizer(dateSelector, date);
		GUIModelField<org.joda.time.DateTime> field = new GUIModelField<org.joda.time.DateTime>(
			name, date, dateSelector, dataSynchronizer);
		addField(field);

		return dateSelector;
	}

	// TODO: Test this out.
	public DateTime addTime(
			String name, org.joda.time.DateTime time, Composite parent, int style) {
		DateTime timeSelector = new DateTime(parent, style | SWT.TIME);
		ModelDataSynchronizer<org.joda.time.DateTime> dataSynchronizer =
			new TimeDataSynchronizer(timeSelector, time);
		GUIModelField<org.joda.time.DateTime> field = new GUIModelField<org.joda.time.DateTime>(
			name, time, timeSelector, dataSynchronizer);
		addField(field);

		return timeSelector;
	}

	// TODO: addCalendar

//	public List addSingleSelectionList(
//			String name, 

	public<T> void addField(GUIModelField<T> field) {
		String fieldName = field.getName();

		if (this.inputFieldsByName.containsKey(fieldName)) {
			String exceptionMessage =
				"A field with the name \"" + fieldName + "\" already exists.  Unable to continue.";
			throw new ModelFieldException(exceptionMessage);
		}

		this.inputFieldsByName.put(fieldName, field);
	}
}