package org.cishell.utility.datastructure.datamodel.gui;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.area.DataModelArea;
import org.cishell.utility.datastructure.datamodel.exception.ModelStructureException;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;
import org.cishell.utility.datastructure.datamodel.group.BasicModelGroup;
import org.cishell.utility.datastructure.datamodel.group.DataModelGroup;

/// TODO: Document this.
/// TODO: Figure out a way to generically express styles for both widgets and areas.
public abstract class AbstractGUIDataModel<
			BaseGUIComponentType, GUIContainerComponentType extends BaseGUIComponentType>
		implements DataModel {
	private Map<String, DataModelArea> areas = new HashMap<String, DataModelArea>();
	private Map<String, DataModelGroup> groups = new HashMap<String, DataModelGroup>();
	private GUIContainerComponentType currentParentComponent;

	public AbstractGUIDataModel() {
	}

	// Miscellaneous methods

	@SuppressWarnings("unchecked")
	public void setCurrentParentComponent(Object currentParentComponent)
			throws ClassCastException {
		this.currentParentComponent = (GUIContainerComponentType) currentParentComponent;
	}

	public GUIContainerComponentType getCurrentParentComponent() {
		return this.currentParentComponent;
	}

	// DataModelAreaContainer methods (via DataModel)

	public Collection<String> getAreaNames() {
		return this.areas.keySet();
	}

	public Collection<DataModelArea> getAreas() {
		return this.areas.values();
	}

	public DataModelArea getArea(String name) {
		return this.areas.get(name);
	}

	public DataModelArea createArea(String name) throws UniqueNameException {
		if (getArea(name) != null) {
			String exceptionMessage = String.format(
				"The area '%s' already exists.  All areas must have unique names.", name);
			throw new UniqueNameException(exceptionMessage);
		} else {
			DataModelArea area = createGUISpecificArea(name);
			this.areas.put(name, area);

			return area;
		}
	}

	public abstract DataModelArea createArea(String name, Object componentForArea)
			throws ClassCastException, ModelStructureException, UniqueNameException;

	protected abstract DataModelArea createGUISpecificArea(String name);

	public void addArea(DataModelArea area)
			throws ClassCastException, ModelStructureException, UniqueNameException {
		String name = area.getName();

		if (getArea(name) != null) {
			String exceptionMessage = String.format(
				"The area '%s' already exists.  All areas must have unique names.", name);
			throw new UniqueNameException(exceptionMessage);
		} else {
			this.areas.put(name, area);
		}
	}

	public boolean areaDisposed(String name) {
		if (this.areas.containsKey(name)) {
			this.areas.remove(name);

			return true;
		} else {
			return false;
		}
	}

	public boolean areaDisposed(DataModelArea area) {
		return areaDisposed(area.getName());
	}

	// Group methods

	public Collection<String> getGroupNames() {
		return this.groups.keySet();
	}

	public Collection<DataModelGroup> getGroups() {
		return this.groups.values();
	}

	public DataModelGroup getGroup(String name) {
		return this.groups.get(name);
	}

	public DataModelGroup createGroup(String name) throws UniqueNameException {
		if (getGroup(name) != null) {
			String exceptionMessage = String.format(
				"The group '%s' already exists.  All groups must have unique names.", name);
			throw new UniqueNameException(exceptionMessage);
		} else {
			DataModelGroup group = new BasicModelGroup(name);
			this.groups.put(name, group);

			return group;
		}
	}

	// Add Field methods

	public abstract DataModelField<Boolean> addCheckBox(
			String name,
			String areaName,
			String groupName,
			boolean defaultOn) throws UniqueNameException;
	
	public abstract<T> DataModelField<T> addDropDown(
			String name,
			String areaName,
			String groupName,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, T> optionValuesByLabels) throws UniqueNameException;
	
	public abstract<T> DataModelField<T> addList(
			String name,
			String areaName,
			String groupName,
			int selectedIndex,
			Collection<String> unorderedOptionLabels,
			Map<String, T> optionValuesByLabels) throws UniqueNameException;
	
	public abstract DataModelField<String> addText(
				String name,
				String areaName,
				String groupName,
				String defaultValue,
				boolean isMultiLined) throws UniqueNameException;
	
	public abstract<T> void addField(String areaName, String groupName, DataModelField<T> field)
			throws UniqueNameException;
}