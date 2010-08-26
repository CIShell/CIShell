package org.cishell.utility.datastructure.datamodel.field.validation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.utilities.MapUtilities;
import org.cishell.utilities.StringUtilities;
import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.exception.ModelValidationException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;

public class UniqueValueValidationRule<ValueType> implements FieldValidationRule<ValueType> {
	private String baseFieldName;
	private Map<String, ValueType> fieldValuesByNames = new HashMap<String, ValueType>();
//	private Multimap<ValueType, String> fieldNamesByValues = HashMultimap.create();

	public UniqueValueValidationRule(String baseFieldName) {
		this.baseFieldName = baseFieldName;
	}

	public void validateField(DataModelField<ValueType> field, DataModel model)
			throws ModelValidationException {
		String fieldName = field.getName();
		ValueType fieldValue = field.getValue();
		Collection<String> fieldNameForFiltering = Arrays.asList(fieldName);
		@SuppressWarnings("unchecked")
		Collection<ValueType> fieldValueForFiltering = Arrays.asList(fieldValue);

		this.fieldValuesByNames.put(fieldName, fieldValue);
		Collection<String> namesOfFieldsWithValue = MapUtilities.getValidKeysOfTypesInMap(
			this.fieldValuesByNames, fieldValueForFiltering, fieldNameForFiltering);

		if (namesOfFieldsWithValue.size() > 0) {
			String exceptionMessage = String.format(
				"Field's value must be identical.  Matches %sfields: [%s]",
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