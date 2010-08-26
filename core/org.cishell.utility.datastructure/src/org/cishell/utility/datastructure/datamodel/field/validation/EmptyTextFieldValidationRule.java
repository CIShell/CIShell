package org.cishell.utility.datastructure.datamodel.field.validation;

import java.util.Collection;

import org.cishell.utilities.StringUtilities;
import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.exception.ModelValidationException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;

public class EmptyTextFieldValidationRule implements FieldValidationRule<String> {
	public void validateField(DataModelField<String> field, DataModel model)
			throws ModelValidationException {
		if (StringUtilities.isNull_Empty_OrWhitespace(field.getValue())) {
			String exceptionMessage = "Field may not be empty.";
			throw new ModelValidationException(exceptionMessage);
		}
	}

	public void fieldUpdated(DataModelField<String> field) {
	}

	public void fieldsUpdated(Collection<DataModelField<String>> fields) {
	}

	public void fieldDisposed(DataModelField<String> field) {
	}
}