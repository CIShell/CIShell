package org.cishell.utilities.swt.model;

import org.cishell.utilities.swt.model.datasynchronizer.ModelDataSynchronizer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

public class GUIModelField<T> {
	private String name;
	private T defaultValue;
	private T value;
	private Widget widget;
	private ModelDataSynchronizer<T> dataSynchronizer;

	public GUIModelField(
			String name,
			T defaultValue,
			Widget widget,
			ModelDataSynchronizer<T> dataSynchronizer) {
		this.name = name;
		this.defaultValue = defaultValue;
		this.value = this.defaultValue;
		this.widget = widget;
		this.dataSynchronizer = dataSynchronizer;

		this.widget.addListener(this.dataSynchronizer.swtUpdateListenerCode(), new Listener() {
			public void handleEvent(Event event) {
				if (event.type == GUIModelField.this.dataSynchronizer.swtUpdateListenerCode()) {
					GUIModelField.this.value =
						GUIModelField.this.dataSynchronizer.synchronizeFromGUI();
				}
			}
		});
	}

	public String getName() {
		return this.name;
	}

	public T getValue() {
		return this.value;
	}

	public void setValue(T value) {
		this.value = this.dataSynchronizer.synchronizeToGUI(value);
	}

	public void reset() {
		this.value = this.dataSynchronizer.reset(this.defaultValue);
	}
}