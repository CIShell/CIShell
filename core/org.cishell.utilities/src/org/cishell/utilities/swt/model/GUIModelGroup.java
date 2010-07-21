package org.cishell.utilities.swt.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.cishell.utilities.swt.model.datasynchronizer.ModelDataSynchronizer;
import org.eclipse.swt.widgets.Widget;

public class GUIModelGroup {
	private String name;
	private Map<String, GUIModelField<?, ? extends Widget, ? extends ModelDataSynchronizer<?>>>
		inputFieldsByName = new HashMap<
			String, GUIModelField<?, ? extends Widget, ? extends ModelDataSynchronizer<?>>>();

	public GUIModelGroup(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public Collection<String> getFieldNames() {
		return this.inputFieldsByName.keySet();
	}

	public Collection<
			GUIModelField<?, ? extends Widget, ? extends ModelDataSynchronizer<?>>> getFields() {
		return this.inputFieldsByName.values();
	}

	public GUIModelField<
			?, ? extends Widget, ? extends ModelDataSynchronizer<?>> getField(String name) {
		return this.inputFieldsByName.get(name);
	}

	public<T> void addField(
			GUIModelField<T, ? extends Widget, ? extends ModelDataSynchronizer<?>> field) {
		String fieldName = field.getName();

		if (this.inputFieldsByName.containsKey(fieldName)) {
			String exceptionMessage =
				"A field with the name \"" + fieldName + "\" already exists.  Unable to continue.";
			throw new ModelFieldException(exceptionMessage);
		}

		this.inputFieldsByName.put(fieldName, field);
	}

	public<T> void removeField(
			GUIModelField<T, ? extends Widget, ? extends ModelDataSynchronizer<?>> field) {
		if (this.inputFieldsByName.containsValue(field)) {
			this.inputFieldsByName.remove(field.getName());
		}
	}
}