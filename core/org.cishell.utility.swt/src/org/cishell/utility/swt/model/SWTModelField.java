package org.cishell.utility.swt.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.ModelDataSynchronizer;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;
import org.cishell.utility.datastructure.datamodel.field.DataModelFieldContainer;
import org.cishell.utility.datastructure.datamodel.field.validation.FieldValidationAction;
import org.cishell.utility.datastructure.datamodel.field.validation.FieldValidator;
import org.cishell.utility.swt.model.field.validation.Utilities;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class SWTModelField<
			ValueType,
			BaseGUIComponentType extends Widget,
			DataSynchronizerType extends ModelDataSynchronizer<ValueType>>
		implements DataModelField<ValueType> {
	private Set<DataModelFieldContainer> containers = new HashSet<DataModelFieldContainer>();
	private DataModel model;
	private String name;
	private Composite parentComponent;
	private ValueType defaultValue;
	private ValueType previousValue;
	private ValueType value;
	private BaseGUIComponentType widget;
	private DataSynchronizerType dataSynchronizer;
	private Collection<FieldValidator<ValueType>> validators =
		new ArrayList<FieldValidator<ValueType>>();
	private Collection<FieldValidator<ValueType>> otherValidators =
		new ArrayList<FieldValidator<ValueType>>();
	private Collection<FieldValidationAction> validationActions =
		new HashSet<FieldValidationAction>();
	private boolean isDisposed = false;

	public SWTModelField(
			DataModel model,
			String name,
			Composite parentComponent,
			ValueType defaultValue,
			BaseGUIComponentType widget,
			DataSynchronizerType dataSynchronizer) {
		this.model = model;
		this.name = name;
		this.parentComponent = parentComponent;
		this.defaultValue = defaultValue;
		this.value = this.defaultValue;
		this.widget = widget;
		this.dataSynchronizer = dataSynchronizer;

		this.widget.addListener(this.dataSynchronizer.updateListenerCode(), new Listener() {
			public void handleEvent(Event event) {
				if (event.type == SWTModelField.this.dataSynchronizer.updateListenerCode()) {
					SWTModelField.this.previousValue = SWTModelField.this.value;
					SWTModelField.this.value =
						SWTModelField.this.dataSynchronizer.synchronizeFromGUI();
					validate();
				}
			}
		});
	}

	// DataModelField methods

	public Collection<DataModelFieldContainer> getContainers() {
		return this.containers;
	}

	public boolean addToContainer(DataModelFieldContainer container) {
		return this.containers.add(container);
	}

	public String getName() {
		return this.name;
	}

	public Object getParentComponent() {
		return this.parentComponent;
	}

	public ValueType getDefaultValue() {
		return this.defaultValue;
	}

	public ValueType getPreviousValue() {
		return this.previousValue;
	}

	public ValueType getValue() {
		return this.value;
	}

	public BaseGUIComponentType getWidget() {
		return this.widget;
	}

	public DataSynchronizerType getDataSynchronizer() {
		return this.dataSynchronizer;
	}

	public ValueType setValue(ValueType value) {
		this.value = this.dataSynchronizer.synchronizeToGUI(value);

		return this.value;
	}

	public ValueType reset() {
		this.value = this.dataSynchronizer.reset(this.defaultValue);

		return this.value;
	}

	public void addValidator(FieldValidator<ValueType> validator) {
		validator.addFieldToValidate(this);
		this.validators.add(validator);
		// Just in case validator was added after other validators were added.
		this.otherValidators.remove(validator);
	}

	public void addValidators(Collection<FieldValidator<ValueType>> validators) {
		for (FieldValidator<ValueType> validator : validators) {
			addValidator(validator);
		}
	}

	public void addOtherValidators(Collection<FieldValidator<ValueType>> validators) {
		this.otherValidators.addAll(
			Utilities.allFieldValidatorsExcept(validators, this.validators));
	}

	public void addValidationAction(FieldValidationAction action) {
		this.validationActions.add(action);
	}

	public void dispose() {
		this.isDisposed = true;

		for (DataModelFieldContainer container : this.containers) {
			container.fieldDisposed(this);
		}

		for (FieldValidator<ValueType> validator : this.validators) {
			validator.fieldDisposed(this);
		}

		validate();
	}

	public boolean isDisposed() {
		return this.isDisposed;
	}

	public void validate() {
		Collection<String> errorMessages = attemptValidation(model);
		performValidationActions(this.model, errorMessages);
	}

	private Collection<String> attemptValidation(DataModel model) {
		Collection<String> errorMessages = attemptValidationOnValidators(this.validators, true);
		errorMessages.addAll(attemptValidationOnValidators(this.otherValidators, false));

		return errorMessages;
	}

	private void performValidationActions(DataModel model, Collection<String> errorMessages) {
		if (errorMessages.size() == 0) {
			for (FieldValidationAction validationAction : this.validationActions) {
				validationAction.doesValidate();
			}
		} else {
			for (FieldValidationAction validationAction : this.validationActions) {
				validationAction.doesNotValidate(errorMessages);
			}
		}
	}

	private Collection<String> attemptValidationOnValidators(
			Collection<FieldValidator<ValueType>> validators, boolean update) {
		Collection<String> errorMessages = new ArrayList<String>();

		for (FieldValidator<ValueType> validator : validators) {
			if (update && !this.isDisposed) {
				validator.fieldUpdated(this);
			}

			Collection<String> temporaryErrorMessage = validator.runValidation(model);
			errorMessages.addAll(temporaryErrorMessage);
		}

		return errorMessages;
	}
}