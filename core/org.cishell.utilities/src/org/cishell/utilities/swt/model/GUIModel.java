package org.cishell.utilities.swt.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.cishell.utilities.swt.model.datasynchronizer.CheckBoxDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.DateDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.DropDownDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.ModelDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.SingleListSelectionDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.TextDataSynchronizer;
import org.cishell.utilities.swt.model.datasynchronizer.TimeDataSynchronizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class GUIModel {
	private Map<String, GUIModelField<?, ? extends Widget, ? extends ModelDataSynchronizer<?>>>
		inputFieldsByName = new HashMap<
			String, GUIModelField<?, ? extends Widget, ? extends ModelDataSynchronizer<?>>>();

	public GUIModel() {
	}

	public Collection<String> getFieldNames() {
		return this.inputFieldsByName.keySet();
	}

	public Collection<
			GUIModelField<?, ? extends Widget, ? extends ModelDataSynchronizer<?>>> getFields() {
		return this.inputFieldsByName.values();
	}

	public GUIModelField<
			?, ? extends Widget, ? extends ModelDataSynchronizer<?>> getField(String name) {
		return this.inputFieldsByName.get(name);
	}

	public GUIModelField<Boolean, Button, CheckBoxDataSynchronizer> addCheckBox(
			String name, boolean on, Composite parent, int style) {
		Button checkBox = new Button(parent, style | SWT.CHECK);
		CheckBoxDataSynchronizer dataSynchronizer = new CheckBoxDataSynchronizer(checkBox, on);
		GUIModelField<Boolean, Button, CheckBoxDataSynchronizer> field =
			new GUIModelField<Boolean, Button, CheckBoxDataSynchronizer>(
				name, on, checkBox, dataSynchronizer);
		addField(field);

		return field;
	}

	public GUIModelField<String, Combo, DropDownDataSynchronizer> addSingleSelectionDropDown(
			String name,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, String> optionValuesByLabels,
			Composite parent,
			int style) {
		java.util.List<String> orderedOptionLabels = new ArrayList<String>(unorderedOptionLabels);
		Collections.sort(orderedOptionLabels);
		Combo dropDown = new Combo(parent, style | SWT.DROP_DOWN);
		DropDownDataSynchronizer dataSynchronizer = new DropDownDataSynchronizer(
			dropDown, selectedIndex, orderedOptionLabels, optionValuesByLabels);
		GUIModelField<String, Combo, DropDownDataSynchronizer> field =
			new GUIModelField<String, Combo, DropDownDataSynchronizer>(
				name, orderedOptionLabels.get(selectedIndex), dropDown, dataSynchronizer);
		addField(field);

		return field;
	}

	// TODO: addMultiSelectionDropDown

	// TODO: Test this out.
	public GUIModelField<
			org.joda.time.DateTime, org.eclipse.swt.widgets.DateTime, DateDataSynchronizer>
				addDate(String name, org.joda.time.DateTime date, Composite parent, int style) {
		org.eclipse.swt.widgets.DateTime dateSelector =
			new org.eclipse.swt.widgets.DateTime(parent, style | SWT.DATE);
		DateDataSynchronizer dataSynchronizer = new DateDataSynchronizer(dateSelector, date);
		GUIModelField<
			org.joda.time.DateTime, org.eclipse.swt.widgets.DateTime, DateDataSynchronizer> field =
				new GUIModelField<
					org.joda.time.DateTime,
					org.eclipse.swt.widgets.DateTime,
					DateDataSynchronizer>(
						name, date, dateSelector, dataSynchronizer);
		addField(field);

		return field;
	}

	// TODO: Test this out.
	public GUIModelField<
			org.joda.time.DateTime, org.eclipse.swt.widgets.DateTime, TimeDataSynchronizer>
				addTime(String name, org.joda.time.DateTime time, Composite parent, int style) {
		org.eclipse.swt.widgets.DateTime timeSelector =
			new org.eclipse.swt.widgets.DateTime(parent, style | SWT.TIME);
		TimeDataSynchronizer dataSynchronizer = new TimeDataSynchronizer(timeSelector, time);
		GUIModelField<
			org.joda.time.DateTime, org.eclipse.swt.widgets.DateTime, TimeDataSynchronizer> field =
				new GUIModelField<
					org.joda.time.DateTime,
					org.eclipse.swt.widgets.DateTime,
					TimeDataSynchronizer>(
						name, time, timeSelector, dataSynchronizer);
		addField(field);

		return field;
	}

	// TODO: addCalendar

	// TODO: Test this out.
	public GUIModelField<String, List, SingleListSelectionDataSynchronizer> addSingleSelectionList(
			String name,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, String> optionValuesByLabels,
			Composite parent,
			int style) {
		java.util.List<String> orderedOptionLabels = new ArrayList<String>(unorderedOptionLabels);
		List list = new List(parent, style | SWT.SINGLE);
		SingleListSelectionDataSynchronizer dataSynchronizer =
			new SingleListSelectionDataSynchronizer(
				list, selectedIndex, orderedOptionLabels, optionValuesByLabels);
		GUIModelField<String, List, SingleListSelectionDataSynchronizer> field =
			new GUIModelField<String, List, SingleListSelectionDataSynchronizer>(
				name, list.getItem(selectedIndex), list, dataSynchronizer);
		addField(field);

		return field;
	}

	// TODO: addMultiSelectionList
	// TODO: addProgressBar
	// TODO: addSash?
	// TODO: addSlider
	// TODO: addScale
	// TODO: addSpinner
	// TODO: addStyledText

	public GUIModelField<String, Text, TextDataSynchronizer> addUnstyledText(
			String name,
			String value,
			boolean isMultiLined,
			Composite parent,
			int style) {
		if (isMultiLined) {
			style = style | SWT.MULTI;
		} else {
			style = style | SWT.SINGLE;
		}

		Text text = new Text(parent, style);
		TextDataSynchronizer dataSynchronizer = new TextDataSynchronizer(text, value);
		GUIModelField<String, Text, TextDataSynchronizer> field =
			new GUIModelField<String, Text, TextDataSynchronizer>(
				name, value, text, dataSynchronizer);
		addField(field);

		return field;
	}

	public<T> void addField(
			GUIModelField<T, ? extends Widget, ? extends ModelDataSynchronizer<?>> field) {
		String fieldName = field.getName();

		if (this.inputFieldsByName.containsKey(fieldName)) {
			String exceptionMessage =
				"A field with the name \"" + fieldName + "\" already exists.  Unable to continue.";
			throw new ModelFieldException(exceptionMessage);
		}

		this.inputFieldsByName.put(fieldName, field);
	}
}