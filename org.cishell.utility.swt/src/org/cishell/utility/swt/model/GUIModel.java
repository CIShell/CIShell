package org.cishell.utility.swt.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.utility.swt.model.datasynchronizer.CheckBoxDataSynchronizer;
import org.cishell.utility.swt.model.datasynchronizer.DropDownDataSynchronizer;
import org.cishell.utility.swt.model.datasynchronizer.ModelDataSynchronizer;
import org.cishell.utility.swt.model.datasynchronizer.SingleListSelectionDataSynchronizer;
import org.cishell.utility.swt.model.datasynchronizer.TextDataSynchronizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class GUIModel {
	private Map<String, GUIModelGroup> groups = new HashMap<String, GUIModelGroup>();

	public GUIModel() {
	}

	public Collection<String> getGroupNames() {
		return this.groups.keySet();
	}

	public Collection<GUIModelGroup> getGroups() {
		return this.groups.values();
	}

	public GUIModelGroup getGroup(String name) {
		if (!this.groups.containsKey(name)) {
			GUIModelGroup newGroup = new GUIModelGroup(name);
			this.groups.put(name, newGroup);

			return newGroup;
		} else {
			return this.groups.get(name);
		}
	}

	public GUIModelField<Boolean, Button, CheckBoxDataSynchronizer> addCheckBox(
			String groupName, String name, boolean on, Composite parent, int style) {
		Button checkBox = new Button(parent, style | SWT.CHECK);
		CheckBoxDataSynchronizer dataSynchronizer = new CheckBoxDataSynchronizer(checkBox, on);
		GUIModelField<Boolean, Button, CheckBoxDataSynchronizer> field =
			new GUIModelField<Boolean, Button, CheckBoxDataSynchronizer>(
				name, on, checkBox, dataSynchronizer);
		addField(groupName, field);

		return field;
	}

	public GUIModelField<String, Combo, DropDownDataSynchronizer> addDropDown(
			String groupName,
			String name,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, String> optionValuesByLabels,
			Composite parent,
			int style) {
		java.util.List<String> orderedOptionLabels = new ArrayList<String>(unorderedOptionLabels);
		Combo dropDown = new Combo(parent, style | SWT.DROP_DOWN);
		DropDownDataSynchronizer dataSynchronizer = new DropDownDataSynchronizer(
			dropDown, selectedIndex, orderedOptionLabels, optionValuesByLabels);
		GUIModelField<String, Combo, DropDownDataSynchronizer> field =
			new GUIModelField<String, Combo, DropDownDataSynchronizer>(
				name,
				optionValuesByLabels.get(orderedOptionLabels.get(selectedIndex)),
				dropDown,
				dataSynchronizer);
		addField(groupName, field);

		return field;
	}

	// TODO: addMultiSelectionDropDown

	// TODO: Test this out.
	// TODO: Make it so the build works with this stuff.
//	public GUIModelField<
//			org.joda.time.DateTime, org.eclipse.swt.widgets.DateTime, DateDataSynchronizer>
//				addDate(String name, org.joda.time.DateTime date, Composite parent, int style) {
//		org.eclipse.swt.widgets.DateTime dateSelector =
//			new org.eclipse.swt.widgets.DateTime(parent, style | SWT.DATE);
//		DateDataSynchronizer dataSynchronizer = new DateDataSynchronizer(dateSelector, date);
//		GUIModelField<
//			org.joda.time.DateTime, org.eclipse.swt.widgets.DateTime, DateDataSynchronizer> field =
//				new GUIModelField<
//					org.joda.time.DateTime,
//					org.eclipse.swt.widgets.DateTime,
//					DateDataSynchronizer>(
//						name, date, dateSelector, dataSynchronizer);
//		addField(field);
//
//		return field;
//	}

	// TODO: Test this out.
//	public GUIModelField<
//			org.joda.time.DateTime, org.eclipse.swt.widgets.DateTime, TimeDataSynchronizer>
//				addTime(String name, org.joda.time.DateTime time, Composite parent, int style) {
//		org.eclipse.swt.widgets.DateTime timeSelector =
//			new org.eclipse.swt.widgets.DateTime(parent, style | SWT.TIME);
//		TimeDataSynchronizer dataSynchronizer = new TimeDataSynchronizer(timeSelector, time);
//		GUIModelField<
//			org.joda.time.DateTime, org.eclipse.swt.widgets.DateTime, TimeDataSynchronizer> field =
//				new GUIModelField<
//					org.joda.time.DateTime,
//					org.eclipse.swt.widgets.DateTime,
//					TimeDataSynchronizer>(
//						name, time, timeSelector, dataSynchronizer);
//		addField(field);
//
//		return field;
//	}

	// TODO: addCalendar

	// TODO: Test this out.
	public GUIModelField<String, List, SingleListSelectionDataSynchronizer> addList(
			String groupName,
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
		addField(groupName, field);

		return field;
	}

	// TODO: addMultiSelectionList
	// TODO: addProgressBar
	// TODO: addSash?
	// TODO: addSlider
	// TODO: addScale
	// TODO: addSpinner
	// TODO: addStyledText

	public GUIModelField<String, Text, TextDataSynchronizer> addText(
			String groupName,
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
		addField(groupName, field);

		return field;
	}

	public<T> void addField(
			String groupName,
			GUIModelField<T, ? extends Widget, ? extends ModelDataSynchronizer<?>> field) {
		GUIModelGroup group = getGroup(groupName);
		group.addField(field);
	}

	public<T> void removeField(
			GUIModelField<T, ? extends Widget, ? extends ModelDataSynchronizer<?>> field) {
		for (GUIModelGroup group : this.groups.values()) {
			group.removeField(field);
		}
	}
}