package org.cishell.utility.swt;

import java.util.Map;

public interface ScrolledComponentFactory<T> {
	public T constructWidget(
			ExpandableComponentWidget<T> componentWidget,
			GridContainer scrolledAreaGrid,
			int style,
			Map<String, Object> arguments,
			int index,
			int uniqueIndex) throws WidgetConstructionException;

	public void reindexComponent(T component, int newIndex);
}