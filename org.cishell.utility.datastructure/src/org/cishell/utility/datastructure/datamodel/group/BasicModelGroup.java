package org.cishell.utility.datastructure.datamodel.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;

public class BasicModelGroup implements DataModelGroup {
	private String name;
	private Map<String, DataModelField<?>> fields = new HashMap<String, DataModelField<?>>();

	public BasicModelGroup(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public Collection<String> getFieldNames() {
		return this.fields.keySet();
	}

	public Collection<DataModelField<?>> getFields() {
		Collection<DataModelField<?>> fields = new ArrayList<DataModelField<?>>();
		fields.addAll(this.fields.values());

		return fields;
	}

	public DataModelField<?> getField(String name) {
		return this.fields.get(name);
	}

	public<T> void addField(DataModelField<T> field)
			throws ClassCastException, UniqueNameException {
		String fieldName = field.getName();

		if (this.fields.containsKey(fieldName)) {
			String format =
				"The field '%s' already exists in this group (%s).  " +
				"All fields must have unique names.";
			String exceptionMessage = String.format(
				format, fieldName, this.name);
			throw new UniqueNameException(exceptionMessage);
		}

		this.fields.put(fieldName, field);
		field.addToContainer(this);
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