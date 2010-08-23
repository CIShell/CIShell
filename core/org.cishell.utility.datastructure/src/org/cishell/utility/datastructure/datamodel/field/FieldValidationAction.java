package org.cishell.utility.datastructure.datamodel.field;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.exception.ModelValidationException;

public interface FieldValidationAction<ValueType> {
	public void fieldDoesValidate(DataModelField<ValueType> field);
	public void fieldDoesNotValidate(
			DataModelField<ValueType> field, Collection<ModelValidationException> reasons);
	public void fieldDisposed(DataModelField<ValueType> field);
}