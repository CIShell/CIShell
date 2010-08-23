package org.cishell.utility.datastructure.datamodel.group;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;
import org.cishell.utility.datastructure.datamodel.field.DataModelFieldContainer;

/**
 * Model groups are organizational structures for related DataModelFields.
 * They are not tied to the physical structure of a GUI.
 * Anything that wishes to retrieve data from a DataModel can ask for ModelGroups.
 */
public interface DataModelGroup extends DataModelFieldContainer {
	public String getName();

	public Collection<String> getFieldNames();
	public Collection<DataModelField<?>> getFields();
	public DataModelField<?> getField(String fieldName);
	public<T> void addField(DataModelField<T> field)
			throws ClassCastException, UniqueNameException;
	public boolean fieldDisposed(String fieldName);
	public<T> boolean fieldDisposed(DataModelField<T> field);
}