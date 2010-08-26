package org.cishell.utility.datastructure.datamodel.field.validation;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;

/**
 * FieldValidators potentially validate multiple fields all in one batch.
 * Fields may need multiple FieldValidators if they belong to multiple groupings of
 * validation rules ({@link FieldValidationRule}).
 */
public interface FieldValidator<ValueType> {
	/// Add a field to perform validation on.
	public void addFieldToValidate(DataModelField<ValueType> field);
	/// Add a validation rule that this validator should use for validation.
	public void addValidationRule(FieldValidationRule<ValueType> rule);
	/// Update any internal states as necessary.
	public void fieldUpdated(DataModelField<ValueType> field);
	/// Update any internal states as necessary.
	public void fieldsUpdated(Collection<DataModelField<ValueType>> fields);
	/// Perform validation on all fields added for validation.
	public Collection<String> runValidation(DataModel model);
	/// Called by the field when the field has been disposed.
	public void fieldDisposed(DataModelField<ValueType> field);
}