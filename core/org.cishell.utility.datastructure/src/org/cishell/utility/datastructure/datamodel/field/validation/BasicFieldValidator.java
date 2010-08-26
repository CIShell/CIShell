package org.cishell.utility.datastructure.datamodel.field.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.exception.ModelValidationException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;

public class BasicFieldValidator<ValueType> implements FieldValidator<ValueType> {
	private String baseFieldName;
	private Collection<DataModelField<ValueType>> fieldsToValidate =
		new HashSet<DataModelField<ValueType>>();
	private Collection<FieldValidationRule<ValueType>> rules =
		new HashSet<FieldValidationRule<ValueType>>();

	public BasicFieldValidator(String baseFieldName) {
		this.baseFieldName = baseFieldName;
	}

	public void addFieldToValidate(DataModelField<ValueType> field) {
		this.fieldsToValidate.add(field);
	}

	public void addValidationRule(FieldValidationRule<ValueType> rule) {
		this.rules.add(rule);
	}

	public Collection<String> runValidation(DataModel model) {
		Collection<String> errorMessages = new ArrayList<String>();

		for (DataModelField<ValueType> field : this.fieldsToValidate) {
			String errorHeader = String.format(
				"(%s%s, %s): ", this.baseFieldName, field.getName(), field.getValue().toString());

			for (FieldValidationRule<ValueType> rule : this.rules) {
				try {
					rule.validateField(field, model);
				} catch (ModelValidationException e) {
					String errorMessage = errorHeader + e.getMessage();
					errorMessages.add(errorMessage);
				}
			}
		}

		return errorMessages;
	}

	public void fieldUpdated(DataModelField<ValueType> field) {
		for (FieldValidationRule<ValueType> rule : this.rules) {
			rule.fieldUpdated(field);
		}
	}

	public void fieldsUpdated(Collection<DataModelField<ValueType>> fields) {
		for (FieldValidationRule<ValueType> rule : this.rules) {
			rule.fieldsUpdated(fields);
		}
	}

	public void fieldDisposed(DataModelField<ValueType> field) {
		for (FieldValidationRule<ValueType> rule : this.rules) {
			rule.fieldDisposed(field);
		}

		this.fieldsToValidate.remove(field);
	}
}