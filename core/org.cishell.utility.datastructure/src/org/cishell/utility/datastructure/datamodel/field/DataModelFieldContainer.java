package org.cishell.utility.datastructure.datamodel.field;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.exception.UniqueNameException;

/** A DataModelFieldContainer is anything that contains DataModelFields.
 */
public interface DataModelFieldContainer {
	public Collection<String> getFieldNames();
	public Collection<DataModelField<?>> getFields();
	public DataModelField<?> getField(String fieldName);
	public<T> void addField(DataModelField<T> field) throws UniqueNameException;
	public boolean fieldDisposed(String fieldName);
	public<T> boolean fieldDisposed(DataModelField<T> field);
}