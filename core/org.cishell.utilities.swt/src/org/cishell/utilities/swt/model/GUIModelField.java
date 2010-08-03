package org.cishell.utilities.swt.model;

import org.cishell.utilities.swt.model.datasynchronizer.ModelDataSynchronizer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class GUIModelField<T, U extends Widget, V extends ModelDataSynchronizer<T>> {
	private String name;
	private T defaultValue;
	private T previousValue;
	private T value;
	private U widget;
	private V dataSynchronizer;

	public GUIModelField(
			String name,
			T defaultValue,
			U widget,
			V dataSynchronizer) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.value = this.defaultValue;
		this.widget = widget;
		this.dataSynchronizer = dataSynchronizer;

		this.widget.addListener(this.dataSynchronizer.swtUpdateListenerCode(), new Listener() {
			public void handleEvent(Event event) {
				if (event.type == GUIModelField.this.dataSynchronizer.swtUpdateListenerCode()) {
					GUIModelField.this.previousValue = GUIModelField.this.value;
					GUIModelField.this.value =
						GUIModelField.this.dataSynchronizer.synchronizeFromGUI();
				}
			}
		});
	}

	public String getName() {
		return this.name;
	}

	public T getPreviousValue() {
		return this.previousValue;
	}

	public T getValue() {
		return this.value;
	}

	public U getWidget() {
		return this.widget;
	}

	public V getDataSynchronizer() {
		return this.dataSynchronizer;
	}

	public void setValue(T value) {
		this.value = this.dataSynchronizer.synchronizeToGUI(value);
	}

	public void reset() {
		this.value = this.dataSynchronizer.reset(this.defaultValue);
	}
}