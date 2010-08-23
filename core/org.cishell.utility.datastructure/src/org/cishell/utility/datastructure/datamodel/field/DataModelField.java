package org.cishell.utility.datastructure.datamodel.field;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.DataModel;

/**
 * DataModelFields are the meat of DataModels.
 * They contain the actual data and internal behavior/user-interaction logic.
 */
public interface DataModelField<ValueType> {
	public Collection<DataModelFieldContainer> getContainers();
	public boolean addToContainer(DataModelFieldContainer container);

	public String getName();
	public Object getParentComponent();
	public ValueType getDefaultValue();
	public ValueType getPreviousValue();
	public ValueType getValue();
	public ValueType setValue(ValueType value);
	public ValueType reset();

	public void addValidationRule(
			FieldValidationRule<ValueType> validator, boolean validateNow, DataModel model);
	public void addValidationAction(FieldValidationAction<ValueType> validationAction);
	public void validate(DataModel model);

	public void dispose();
}