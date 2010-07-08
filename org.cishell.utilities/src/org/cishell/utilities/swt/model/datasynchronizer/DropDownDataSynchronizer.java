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
	private Map<String, String> optionValuesByLabels;

	public DropDownDataSynchronizer(
			Combo dropDown,
			int selectedIndex,
			List<String> optionLabels,
			Map<String, String> optionValuesByLabels) {
		this.dropDown = dropDown;
		this.optionLabels = HashBiMap.create(MapUtilities.mapIndexToValues(optionLabels));
		this.optionValuesByLabels = optionValuesByLabels;

		this.dropDown.setItems(optionLabels.toArray(new String[0]));
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
		return this.optionLabels.get(this.dropDown.getSelectionIndex());
	}

	public String synchronizeToGUI(String value) {
		this.dropDown.select(this.optionLabels.inverse().get(value));

		return value();
	}

	public String reset(String defaultValue) {
		return synchronizeToGUI(defaultValue);
	}
}