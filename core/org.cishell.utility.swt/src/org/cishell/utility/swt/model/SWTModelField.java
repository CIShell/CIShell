package org.cishell.utility.swt.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.cishell.utility.datastructure.datamodel.DataModel;
import org.cishell.utility.datastructure.datamodel.ModelDataSynchronizer;
import org.cishell.utility.datastructure.datamodel.exception.ModelValidationException;
import org.cishell.utility.datastructure.datamodel.field.DataModelField;
import org.cishell.utility.datastructure.datamodel.field.DataModelFieldContainer;
import org.cishell.utility.datastructure.datamodel.field.FieldValidationAction;
import org.cishell.utility.datastructure.datamodel.field.FieldValidationRule;
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
	private String name;
	private Composite parentComponent;
	private ValueType defaultValue;
	private ValueType previousValue;
	private ValueType value;
	private BaseGUIComponentType widget;
	private DataSynchronizerType dataSynchronizer;
	private Collection<FieldValidationRule<ValueType>> validators =
		new ArrayList<FieldValidationRule<ValueType>>();
	private Collection<FieldValidationAction<ValueType>> validationActions =
		new ArrayList<FieldValidationAction<ValueType>>();

	public SWTModelField(
			final DataModel model,
			String name,
			Composite parentComponent,
			ValueType defaultValue,
			BaseGUIComponentType widget,
			DataSynchronizerType dataSynchronizer) {
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
					validate(model);
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

	public void addValidationRule(
			FieldValidationRule<ValueType> validator, boolean validateNow, DataModel model) {
		this.validators.add(validator);

		if (validateNow) {
			validate(model);
		}
	}

	public void addValidationAction(FieldValidationAction<ValueType> validationAction) {
		this.validationActions.add(validationAction);
	}

	public void validate(DataModel model) {
		Collection<ModelValidationException> reasonsInvalid = attemptValidation(model);
		performValidationActions(model, reasonsInvalid);
	}

	public void dispose() {
		for (DataModelFieldContainer container : this.containers) {
			container.fieldDisposed(this);
		}

		for (FieldValidationRule<ValueType> validator : this.validators) {
			validator.fieldDisposed(this);
		}

		for (FieldValidationAction<ValueType> validationAction : this.validationActions) {
			validationAction.fieldDisposed(this);
		}
	}

	private Collection<ModelValidationException> attemptValidation(DataModel model) {
		Collection<ModelValidationException> reasonsInvalid =
			new ArrayList<ModelValidationException>();

		for (FieldValidationRule<ValueType> validator : this.validators) {
			try {
				validator.validateField(this, model);
			} catch (ModelValidationException e) {
				reasonsInvalid.add(e);
			}
		}

		return reasonsInvalid;
	}

	private void performValidationActions(
			DataModel model, Collection<ModelValidationException> reasonsInvalid) {
		if (reasonsInvalid.size() == 0) {
			for (FieldValidationAction<ValueType> validationAction : this.validationActions) {
				validationAction.fieldDoesValidate(this);
			}
		} else {
			for (FieldValidationAction<ValueType> validationAction : this.validationActions) {
				validationAction.fieldDoesNotValidate(this, reasonsInvalid);
			}
		}
	}
}