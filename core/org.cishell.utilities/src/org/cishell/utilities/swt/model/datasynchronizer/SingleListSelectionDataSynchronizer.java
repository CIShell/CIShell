package org.cishell.utilities.swt.model.datasynchronizer;

import java.util.Map;

import org.cishell.utilities.MapUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

// TODO: Make this so options can change on it.
public class SingleListSelectionDataSynchronizer implements ModelDataSynchronizer<String> {
	private List singleSelectionList;
	private BiMap<Integer, String> optionLabels;
	private Map<String, String> optionValuesByLabels;

	public SingleListSelectionDataSynchronizer(
			List singleSelectionList,
			int selectedIndex,
			java.util.List<String> optionLabels,
			Map<String, String> optionValuesByLabels) {
		this.singleSelectionList = singleSelectionList;
		this.optionLabels = HashBiMap.create(MapUtilities.mapIndexToValues(optionLabels));
		this.optionValuesByLabels = optionValuesByLabels;

		this.singleSelectionList.setItems(optionLabels.toArray(new String[0]));
		this.singleSelectionList.select(selectedIndex);
	}

	public int swtUpdateListenerCode() {
		return SWT.Selection;
	}

	public String value() {
		return this.optionValuesByLabels.get(
			this.optionLabels.get(this.singleSelectionList.getSelectionIndex()));
	}

	public String synchronizeFromGUI() {
		return this.optionLabels.get(this.singleSelectionList.getSelectionIndex());
	}

	public String synchronizeToGUI(String value) {
		this.singleSelectionList.select(this.optionLabels.inverse().get(value));

		return value();
	}

	public String reset(String defaultValue) {
		return synchronizeToGUI(defaultValue);
	}
}