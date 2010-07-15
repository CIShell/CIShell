package org.cishell.utilities.swt.model.datasynchronizer;

import java.util.List;
import java.util.Map;

import org.cishell.utilities.MapUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

// TODO: Make this so options can change on it.
public class DropDownDataSynchronizer implements ModelDataSynchronizer<String> {
	private Combo dropDown;
	private BiMap<Integer, String> optionLabels;
	private BiMap<String, String> optionValuesByLabels;

	public DropDownDataSynchronizer(
			Combo dropDown,
			int selectedIndex,
			List<String> optionLabels,
			Map<String, String> optionValuesByLabels) {
		this.dropDown = dropDown;

		setOptions(optionLabels, optionValuesByLabels);
		this.dropDown.select(selectedIndex);
	}

	public int swtUpdateListenerCode() {
		return SWT.Selection;
	}

	public String value() {
		return this.optionValuesByLabels.get(
			this.optionLabels.get(this.dropDown.getSelectionIndex()));
	}

	public String synchronizeFromGUI() {
		return value();
	}

	public String synchronizeToGUI(String value) {
		String label = this.optionValuesByLabels.inverse().get(value);
		this.dropDown.select(this.optionLabels.inverse().get(label));

		return value();
	}

	public String reset(String defaultValue) {
		return synchronizeToGUI(defaultValue);
	}

	public void setOptions(List<String> optionLabels, Map<String, String> optionValuesByLabels) {
		this.optionLabels = HashBiMap.create(MapUtilities.mapIndexToValues(optionLabels));
		this.optionValuesByLabels = HashBiMap.create(optionValuesByLabels);

		this.dropDown.setItems(optionLabels.toArray(new String[0]));
		this.dropDown.select(0);
	}
}