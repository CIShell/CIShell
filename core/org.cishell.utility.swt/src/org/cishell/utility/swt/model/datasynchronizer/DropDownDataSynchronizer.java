package org.cishell.utility.swt.model.datasynchronizer;

import java.util.List;
import java.util.Map;

import org.cishell.utilities.MapUtilities;
import org.cishell.utility.datastructure.datamodel.ModelDataSynchronizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

// TODO: Make this so options can change on it.
public class DropDownDataSynchronizer<T> implements ModelDataSynchronizer<T> {
	private Combo dropDown;
	private BiMap<Integer, String> optionLabels;
	private BiMap<String, T> optionValuesByLabels;

	public DropDownDataSynchronizer(
			Combo dropDown,
			int selectedIndex,
			List<String> optionLabels,
			Map<String, T> optionValuesByLabels) {
		this.dropDown = dropDown;

		setOptions(optionLabels, optionValuesByLabels);
		this.dropDown.select(selectedIndex);
	}

	public int updateListenerCode() {
		return SWT.Selection;
	}

	public T value() {
		return this.optionValuesByLabels.get(
			this.optionLabels.get(this.dropDown.getSelectionIndex()));
	}

	public T synchronizeFromGUI() {
		return value();
	}

	public T synchronizeToGUI(T value) {
		String label = this.optionValuesByLabels.inverse().get(value);
		this.dropDown.select(this.optionLabels.inverse().get(label));

		return value();
	}

	public T reset(T defaultValue) {
		return synchronizeToGUI(defaultValue);
	}

	public void setOptions(List<String> optionLabels, Map<String, T> optionValuesByLabels) {
		this.optionLabels = HashBiMap.create(MapUtilities.mapIndexToValues(optionLabels));
		this.optionValuesByLabels = HashBiMap.create(optionValuesByLabels);

		this.dropDown.setItems(optionLabels.toArray(new String[0]));
		this.dropDown.select(0);
	}
}