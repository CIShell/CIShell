package org.cishell.utility.swt.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.cishell.utilities.StringUtilities;
import org.cishell.utility.datastructure.datamodel.area.DataModelArea;
import org.cishell.utility.datastructure.datamodel.exception.ModelStructureException;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;
import org.cishell.utility.datastructure.datamodel.group.DataModelGroup;
import org.cishell.utility.datastructure.datamodel.gui.AbstractGUIDataModel;
import org.cishell.utility.swt.model.datasynchronizer.CheckBoxDataSynchronizer;
import org.cishell.utility.swt.model.datasynchronizer.DropDownDataSynchronizer;
import org.cishell.utility.swt.model.datasynchronizer.SingleListSelectionDataSynchronizer;
import org.cishell.utility.swt.model.datasynchronizer.TextDataSynchronizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class SWTModel extends AbstractGUIDataModel<Widget, Composite> {
	private int newAreaStyle;

	public SWTModel(int newAreaStyle) {
		this.newAreaStyle = newAreaStyle;
	}

	@Override
	public DataModelArea createArea(String name, Object componentForArea)
			throws ClassCastException, ModelStructureException, UniqueNameException {
		if (getArea(name) != null) {
			String exceptionMessage = String.format(
				"The area '%s' already exists.  All areas must have unique names.", name);
			throw new UniqueNameException(exceptionMessage);
		} else {
			DataModelArea area = new SWTModelArea(
				null,
				null,
				name,
				(Composite) componentForArea,
				this.newAreaStyle);
			addArea(area);

			return area;
		}
	}

	@Override
	protected DataModelArea createGUISpecificArea(String name) {
		Composite parent = getCurrentParentComponent();

		return new SWTModelArea(null, parent, name, this.newAreaStyle);
	}

	// Add Field methods

	@Override
	public DataModelField<Boolean> addCheckBox(
			String name, String areaName, String groupName, boolean defaultOn)
			throws UniqueNameException {
		return addCheckBox(
			name, areaName, groupName, defaultOn, getCurrentParentComponent(), SWT.NONE);
	}

	public SWTModelField<Boolean, Button, CheckBoxDataSynchronizer> addCheckBox(
			String name,
			String areaName,
			String groupName,
			boolean on,
			Composite parent,
			int style)
			throws UniqueNameException {
		Button checkBox = new Button(parent, style | SWT.CHECK);
		CheckBoxDataSynchronizer dataSynchronizer = new CheckBoxDataSynchronizer(checkBox, on);
		SWTModelField<Boolean, Button, CheckBoxDataSynchronizer> field =
			new SWTModelField<Boolean, Button, CheckBoxDataSynchronizer>(
				this, name, parent, on, checkBox, dataSynchronizer);
		addField(areaName, groupName, field);

		return field;
	}

	@Override
	public<T> DataModelField<T> addDropDown(
			String name,
			String areaName,
			String groupName,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, T> optionValuesByLabels) throws UniqueNameException {
		return addDropDown(
			name,
			areaName,
			groupName,
			selectedIndex,
			unorderedOptionLabels,
			optionValuesByLabels,
			getCurrentParentComponent(),
			SWT.BORDER | SWT.READ_ONLY);
	}

	public<T> SWTModelField<T, Combo, DropDownDataSynchronizer<T>> addDropDown(
			String name,
			String areaName,
			String groupName,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, T> optionValuesByLabels,
			Composite parent,
			int style) throws UniqueNameException {
		java.util.List<String> orderedOptionLabels = new ArrayList<String>(unorderedOptionLabels);
		Combo dropDown = new Combo(parent, style | SWT.DROP_DOWN);
		DropDownDataSynchronizer<T> dataSynchronizer = new DropDownDataSynchronizer<T>(
			dropDown, selectedIndex, orderedOptionLabels, optionValuesByLabels);
		SWTModelField<T, Combo, DropDownDataSynchronizer<T>> field =
			new SWTModelField<T, Combo, DropDownDataSynchronizer<T>>(
				this,
				name,
				parent,
				optionValuesByLabels.get(orderedOptionLabels.get(selectedIndex)),
				dropDown,
				dataSynchronizer);
		addField(areaName, groupName, field);

		return field;
	}

	@Override
	public<T> DataModelField<T> addList(
			String name,
			String areaName,
			String groupName,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, T> optionValuesByLabels) throws UniqueNameException {
		return addList(
			name,
			areaName,
			groupName,
			selectedIndex,
			unorderedOptionLabels,
			optionValuesByLabels,
			getCurrentParentComponent(),
			SWT.NONE);
	}

	public<T> SWTModelField<T, List, SingleListSelectionDataSynchronizer<T>> addList(
			String name,
			String areaName,
			String groupName,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, T> optionValuesByLabels,
			Composite parent,
			int style) throws UniqueNameException {
		java.util.List<String> orderedOptionLabels = new ArrayList<String>(unorderedOptionLabels);
		List list = new List(parent, style | SWT.SINGLE);
		SingleListSelectionDataSynchronizer<T> dataSynchronizer =
			new SingleListSelectionDataSynchronizer<T>(
				list, selectedIndex, orderedOptionLabels, optionValuesByLabels);
		SWTModelField<T, List, SingleListSelectionDataSynchronizer<T>> field =
			new SWTModelField<T, List, SingleListSelectionDataSynchronizer<T>>(
				this, name, parent, dataSynchronizer.value(), list, dataSynchronizer);
		addField(areaName, groupName, field);

		return field;
	}

	@Override
	public DataModelField<String> addText(
				String name,
				String areaName,
				String groupName,
				String defaultValue,
				boolean isMultiLined)
				throws UniqueNameException {
		return addText(
			name,
			areaName,
			groupName,
			defaultValue,
			isMultiLined,
			getCurrentParentComponent(),
			SWT.NONE);
	}

	public SWTModelField<String, Text, TextDataSynchronizer> addText(
			String name,
			String areaName,
			String groupName,
			String defaultValue,
			boolean isMultiLined,
			Composite parent,
			int style) throws UniqueNameException {
		if (isMultiLined) {
			style = style | SWT.MULTI;
		} else {
			style = style | SWT.SINGLE;
		}

		Text text = new Text(parent, style);
		TextDataSynchronizer dataSynchronizer = new TextDataSynchronizer(text, defaultValue);
		SWTModelField<String, Text, TextDataSynchronizer> field =
			new SWTModelField<String, Text, TextDataSynchronizer>(
				this, name, parent, defaultValue, text, dataSynchronizer);
		addField(areaName, groupName, field);

		return field;
	}

	@Override
	public<T> void addField(String areaName, String groupName, DataModelField<T> field)
			throws UniqueNameException {
		DataModelArea area = getArea(areaName);

		if (area != null) {
			area.addField(field);
		}

		if (StringUtilities.isNull_Empty_OrWhitespace(groupName)) {
			groupName = DEFAULT_GROUP_NAME;
		}

		DataModelGroup group = getGroup(groupName);

		if (group == null) {
			group = createGroup(groupName);
		}

		group.addField(field);
	}
}