package org.cishell.utility.swt.model.datasynchronizer;

import java.util.Map;

import org.cishell.utilities.MapUtilities;
import org.cishell.utility.datastructure.datamodel.ModelDataSynchronizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.List;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

// TODO: Make this so options can change on it.
public class SingleListSelectionDataSynchronizer<T> implements ModelDataSynchronizer<T> {
	private List singleSelectionList;
	private BiMap<Integer, String> optionLabels;
	private Map<String, T> optionValuesByLabels;

	public SingleListSelectionDataSynchronizer(
			List singleSelectionList,
			int selectedIndex,
			java.util.List<String> optionLabels,
			Map<String, T> optionValuesByLabels) {
		this.singleSelectionList = singleSelectionList;
		this.optionLabels = HashBiMap.create(MapUtilities.mapIndexToValues(optionLabels));
		this.optionValuesByLabels = optionValuesByLabels;

		this.singleSelectionList.setItems(optionLabels.toArray(new String[0]));
		this.singleSelectionList.select(selectedIndex);
	}

	public int updateListenerCode() {
		return SWT.Selection;
	}

	public T value() {
		return this.optionValuesByLabels.get(
			this.optionLabels.get(this.singleSelectionList.getSelectionIndex()));
	}

	public T synchronizeFromGUI() {
		return value();
	}

	public T synchronizeToGUI(T value) {
		this.singleSelectionList.select(this.optionLabels.inverse().get(value));

		return value();
	}

	public T reset(T defaultValue) {
		return synchronizeToGUI(defaultValue);
	}
}