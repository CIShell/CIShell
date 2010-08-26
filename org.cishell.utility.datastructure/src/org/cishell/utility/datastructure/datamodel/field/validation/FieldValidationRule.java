package org.cishell.utility.datastructure.datamodel.field.validation;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.exception.ModelValidationException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;

public interface FieldValidationRule<ValueType> {
	public void validateField(DataModelField<ValueType> field, DataModel model)
			throws ModelValidationException;
	/// Update any internal states as necessary.
	public void fieldUpdated(DataModelField<ValueType> field);
	public void fieldsUpdated(Collection<DataModelField<ValueType>> fields);
	public void fieldDisposed(DataModelField<ValueType> field);
}