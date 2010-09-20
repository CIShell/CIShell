package org.cishell.utility.datastructure.datamodel.field;

import java.util.Collection;

import org.cishell.utility.datastructure.datamodel.field.validation.FieldValidationAction;
import org.cishell.utility.datastructure.datamodel.field.validation.FieldValidator;

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

	/** Add a validator that should validate this field. */
	public void addValidator(FieldValidator<ValueType> validator);
	/** Add all of the specified validators as validators that validate this field. */
	public void addValidators(Collection<FieldValidator<ValueType>> validators);
	/** Add validators that should be considered when performing validation actions. */
	public void addOtherValidators(Collection<FieldValidator<ValueType>> otherValidators);
	/** Add an action to perform, given if everything validated or not. */
	public void addValidationAction(FieldValidationAction action);

	/** Notify everything that has added this field that this field has been disposed. */ 
	public void dispose();
	/** Has this field been disposed? */
	public boolean isDisposed();
}