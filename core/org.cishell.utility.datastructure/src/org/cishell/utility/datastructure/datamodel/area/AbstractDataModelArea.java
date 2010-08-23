package org.cishell.utility.datastructure.datamodel.area;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.utility.datastructure.datamodel.exception.ModelStructureException;
import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;

public abstract class AbstractDataModelArea<
		BaseGUIComponentType, GUIContainerComponentType extends BaseGUIComponentType>
		implements DataModelArea {
	private DataModelArea parentArea;
	private GUIContainerComponentType parentComponent;
	private String name;

	private Map<String, DataModelArea> areas = new HashMap<String, DataModelArea>();
	private Map<String, DataModelField<?>> fields = new HashMap<String, DataModelField<?>>();

	public AbstractDataModelArea(
			DataModelArea parentArea, GUIContainerComponentType parentComponent, String name) {
		this.parentArea = parentArea;
		this.parentComponent = parentComponent;
		this.name = name;
	}

	// Misceallaneous methods

	public DataModelArea getParentArea() {
		return this.parentArea;
	}

	public Object getParentComponent() {
		return this.parentComponent;
	}

	public GUIContainerComponentType getParentComponentWithType() {
		return this.parentComponent;
	}

	public String getName() {
		return this.name;
	}

	// DataModelAreaContainer methods

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
			DataModelArea area = internalCreateArea(name);
			this.areas.put(name, area);

			return area;
		}
	}

	public abstract DataModelArea createArea(String name, Object componentForArea)
			throws ClassCastException, ModelStructureException, UniqueNameException;

	protected abstract DataModelArea internalCreateArea(String name);

	public void addArea(DataModelArea area)
			throws ClassCastException, ModelStructureException, UniqueNameException {
		String name = area.getName();

		if (getArea(name) != null) {
			String exceptionMessage = String.format(
				"The area '%s' already exists.  All areas must have unique names.", name);
			throw new UniqueNameException(exceptionMessage);
		} else if (area.getParentComponent() != getParentComponent()) {
			String exceptionMessage = String.format(
				"Tried to manually add area %s to area %s, but parent components do not match.",
				name,
				getName());
			throw new ModelStructureException(exceptionMessage);
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

	// DataModelFieldContainer methods

	public Collection<String> getFieldNames() {
		return this.fields.keySet();
	}

	public Collection<DataModelField<?>> getFields() {
		return this.fields.values();
	}

	public DataModelField<?> getField(String fieldName) {
		return this.fields.get(fieldName);
	}

	public<T> void addField(DataModelField<T> field) throws UniqueNameException {
		String fieldName = field.getName();

		if (getField(fieldName) != null) {
			String format =
				"The field '%s' already exists in this area (%s).  " +
				"All fields must have unique names.";
			String exceptionMessage = String.format(
				format, fieldName, this.name);
			throw new UniqueNameException(exceptionMessage);
		} else {
			this.fields.put(fieldName, field);
			field.addToContainer(this);
		}
	}

	public boolean fieldDisposed(String fieldName) {
		if (this.fields.containsKey(fieldName)) {
			this.fields.remove(fieldName);

			return true;
		} else {
			return false;
		}
	}

	public<T> boolean fieldDisposed(DataModelField<T> field) {
		return fieldDisposed(field.getName());
	}
}
