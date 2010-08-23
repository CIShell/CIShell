package org.cishell.utility.datastructure.datamodel.field;

import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.exception.ModelValidationException;

public interface FieldValidationRule<ValueType> {
	public void validateField(DataModelField<ValueType> field, DataModel model)
			throws ModelValidationException;
	public void fieldDisposed(DataModelField<ValueType> field);
}