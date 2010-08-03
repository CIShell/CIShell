package org.cishell.utilities.swt;

import java.util.Map;

public interface ScrolledComponentFactory<T> {
	public T constructWidget(
			ExpandableComponentWidget<T> componentWidget,
			GridContainer scrolledAreaGrid,
			int style,
			Map<String, Object> arguments,
			int index,
			int uniqueIndex);

	public void reindexComponent(T component, int newIndex);
}