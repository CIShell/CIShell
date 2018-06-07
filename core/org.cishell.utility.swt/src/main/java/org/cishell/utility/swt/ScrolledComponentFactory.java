package org.cishell.utility.swt;

import java.util.Map;

/**
 * Used with ExpandableComponentWidget<T> as part of a SWT-based layer for managing scrolling
 * components, whose contents are dynamic (as in, rows can be added and removed dynamically).
 * The original inspiration for this layer is that SWT doesn't play very nicely with this type of
 * dynamic scrolling component (i.e. the ones where content is changed real-time).
 * The scrolling components involved can be thought of as having rows of content, where all rows
 * have the same number of elements. (So, think of a grid--hence GridContainer.)
 */
public interface ScrolledComponentFactory<T> {
	/** Construct a child widget of type T to be added to componentWidget.
	 * NOTE: The term "widget" in the name "constructWidget" refers to whatever T happens to be,
	 * which is not necessarily a widget (but may wrap one or more actual widgets).
	 * Parameters:
	 * componentWidget -- the parent component of the T (or its wrapped components)
	 * that we're creating.
	 * scrolledAreaGrid -- tracks the actual SWT components so individual rows of them can easily
	 * be removed in one operation.
	 * style -- any SWT style flags that should be used when creating child components. Somewhat
	 * deprecated; perhaps should be removed.
	 * arguments -- if any implementation ever had custom arguments, this would be the channel
	 * to use.
	 * index -- the index of the constructed T within componentWidget, such that calling
	 * componentWidget.removeComponent(index) will remove this constructed T object.
	 * uniqueIndex -- has no functional purpose in this layer, but can be used for things like
	 * unique name creation. (NOTE: It may have functional purposes in users of this layer.)
	 */
	public T constructWidget(
			ExpandableComponentWidget<T> componentWidget,
			GridContainer scrolledAreaGrid,
			int style,
			Map<String, Object> arguments,
			int index,
			int uniqueIndex) throws WidgetConstructionException;

	/*
	 * Whenever a T is added or removed, the set of T may need to be reindexed so future add/remove
	 * operations behave properly.
	 * Parameters:
	 * component -- the component to reindex.
	 * newIndex -- the new index to give to component.
	 */
	public void reindexComponent(T component, int newIndex);
}