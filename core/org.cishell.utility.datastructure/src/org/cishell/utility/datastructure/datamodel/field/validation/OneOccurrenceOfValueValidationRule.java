package org.cishell.utility.datastructure.datamodel.field.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.utilities.MapUtilities;
import org.cishell.utilities.StringUtilities;
import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.exception.ModelValidationException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;

public class OneOccurrenceOfValueValidationRule<ValueType>
		implements FieldValidationRule<ValueType> {
	private String baseFieldName;
	private Collection<ValueType> targetValue = new ArrayList<ValueType>(1);
	private Map<String, ValueType> fieldValuesByNames = new HashMap<String, ValueType>();

	public OneOccurrenceOfValueValidationRule(String baseFieldName, ValueType targetValue) {
		this.baseFieldName = baseFieldName;
		this.targetValue.add(targetValue);
	}

	public void validateField(DataModelField<ValueType> field, DataModel model)
			throws ModelValidationException {
		String fieldName = field.getName();
		ValueType fieldValue = field.getValue();

		this.fieldValuesByNames.put(fieldName, fieldValue);
		Collection<String> namesOfFieldsWithValue = MapUtilities.getValidKeysOfTypesInMap(
			this.fieldValuesByNames, this.targetValue, new ArrayList<String>());

		if (namesOfFieldsWithValue.size() > 1) {
			String exceptionMessage = String.format(
				"Field's value must be not identical.  Matches %sfields: [%s]",
				this.baseFieldName,
				StringUtilities.implodeItems(namesOfFieldsWithValue, ", "));
			throw new ModelValidationException(exceptionMessage);
		}
	}

	public void fieldUpdated(DataModelField<ValueType> field) {
		this.fieldValuesByNames.put(field.getName(), field.getValue());
	}

	public void fieldsUpdated(Collection<DataModelField<ValueType>> fields) {
		for (DataModelField<ValueType> field : fields) {
			fieldUpdated(field);
		}
	}

	public void fieldDisposed(DataModelField<ValueType> field) {
		this.fieldValuesByNames.remove(field.getName());
	}
}