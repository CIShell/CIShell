package org.cishell.utility.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * This is meant to be subclassed.
 */
public class ExpandableComponentWidget<T> extends Composite {
	public static final int COLUMN_AREA_LAYOUT_VERTICAL_SPACING = 1;
	public static final int VERTICAL_SCROLL_INCREMENT = 50;

	private ScrolledComponentFactory<T> componentFactory;
	private Composite headerArea;
	private ScrolledComposite scrollingArea;
	private GridContainer scrolledAreaGrid;
	private Composite footerArea;
	private List<T> components = new ArrayList<T>();
	private int uniqueComponentCount = 0;
	private Collection<Label> columnLabels;

	public ExpandableComponentWidget(
			Composite parent, ScrolledComponentFactory<T> componentFactory) {
		super(parent, SWT.NONE);
		this.componentFactory = componentFactory;

		setLayout(createLayout());
		this.headerArea = createHeaderArea();
		this.scrollingArea = createScrollingArea();
		this.footerArea = createFooterArea();
		this.scrolledAreaGrid = createScrolledAreaGrid(this.scrollingArea);

		this.scrollingArea.setExpandHorizontal(true);
		this.scrollingArea.setExpandVertical(true);
		this.scrollingArea.setAlwaysShowScrollBars(true);
		fixSize();
		this.scrollingArea.setContent(this.scrolledAreaGrid.getActualParent());
		this.scrollingArea.getVerticalBar().setPageIncrement(VERTICAL_SCROLL_INCREMENT);
		this.columnLabels = createColumnLabels(this.scrolledAreaGrid.getActualParent(), SWT.NONE);
	}

	public Composite getHeaderArea() {
		return this.headerArea;
	}

	public Composite getFooterArea() {
		return this.footerArea;
	}

	public List<T> getComponents() {
		return Collections.unmodifiableList(this.components);
	}

	public int getColumnCount() {
		return 1;
	}

	public T addComponent(int style, Map<String, Object> arguments)
			throws WidgetConstructionException {
		// TODO: Fix this terrible hack?
		if (this.components.size() == 0) {
			for (Label columnLabel : this.columnLabels) {
				columnLabel.setVisible(true);
			}
		}

		final int componentCount = this.components.size();
		T component = this.componentFactory.constructWidget(
			this,
			this.scrolledAreaGrid,
			style,
			arguments,
			componentCount,
			this.uniqueComponentCount);
		this.uniqueComponentCount++;

		fixSize();

		this.components.add(component);

		return component;
	}

	public void removeComponent(int index) {
		this.scrolledAreaGrid.removeRow(index);
		this.components.remove(index);
		fixSize();

		for (int ii = 0; ii < this.components.size(); ii++) {
			this.componentFactory.reindexComponent(this.components.get(ii), ii);
		}

		// TODO: Fix this terrible hack?
		if (this.components.size() == 0) {
			for (Label columnLabel : this.columnLabels) {
				columnLabel.setVisible(false);
			}
		}
	}

	public Collection<Label> createColumnLabels(Composite parent, int style) {
		List<Label> columnLabels = new ArrayList<Label>();

		for (String columnLabelText : createColumnLabelTexts()) {
			Label columnLabel = new Label(parent, style);
			columnLabel.setLayoutData(createColumnLabelLayoutData());
			columnLabel.setText(columnLabelText);
			columnLabels.add(columnLabel);
		}

		return columnLabels;
	}

	public Collection<String> createColumnLabelTexts() {
		List<String> columnLabelTexts = new ArrayList<String>();

		for (int ii = 0; ii < getColumnCount(); ii++) {
			columnLabelTexts.add("Column " + ii);
		}

		return columnLabelTexts;
	}

	private void fixSize() {
		Composite scrolledArea = this.scrolledAreaGrid.getActualParent();
		this.scrollingArea.setMinSize(scrolledArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledArea.pack();
	}

	protected Composite createHeaderArea() {
		Composite headerArea = new Composite(this, SWT.NONE);
		headerArea.setLayoutData(createHeaderAreaLayoutData());
		headerArea.setLayout(createHeaderLayout());

		return headerArea;
	}

	protected GridData createHeaderAreaLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);

		return layoutData;
	}

	protected GridLayout createHeaderLayout() {
		GridLayout layout = new GridLayout(1, false);
		GUIBuilderUtilities.clearMargins(layout);
		GUIBuilderUtilities.clearSpacing(layout);

		return layout;
	}

	protected ScrolledComposite createScrollingArea() {
		ScrolledComposite scrollingArea =
			new ScrolledComposite(this, SWT.BORDER | SWT.V_SCROLL);
		scrollingArea.setLayoutData(createScrollingAreaLayoutData());
		scrollingArea.setLayout(createScrollingLayout());

		return scrollingArea;
	}

	protected GridData createScrollingAreaLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);

		return layoutData;
	}

	private GridLayout createScrollingLayout() {
		GridLayout layout = new GridLayout(1, true);
		GUIBuilderUtilities.clearMargins(layout);
		GUIBuilderUtilities.clearSpacing(layout);

		return layout;
	}

	protected Composite createFooterArea() {
		Composite footerArea = new Composite(this, SWT.BORDER);
		footerArea.setLayoutData(createFooterAreaLayoutData());
		footerArea.setLayout(createFooterLayout());

		return footerArea;
	}

	protected GridData createFooterAreaLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);

		return layoutData;
	}

	protected GridLayout createFooterLayout() {
		GridLayout layout = new GridLayout(1, false);
		GUIBuilderUtilities.clearMargins(layout);
		GUIBuilderUtilities.clearSpacing(layout);

		return layout;
	}

	private GridContainer createScrolledAreaGrid(Composite parent) {
		Composite columnArea = new Composite(parent, SWT.NONE);
		columnArea.setLayoutData(createScrolledAreaLayoutData());
		final int columnCount = getColumnCount();
		columnArea.setLayout(createScrolledAreaLayout(columnCount));

		return new GridContainer(columnArea, columnCount);
	}

	protected GridData createScrolledAreaLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);

		return layoutData;
	}

	protected GridLayout createScrolledAreaLayout(int columnCount) {
		GridLayout layout = new GridLayout(columnCount, false);
//		GUIBuilderUtilities.clearMargins(layout);
//		GUIBuilderUtilities.clearSpacing(layout);

		return layout;
	}

	protected GridData createColumnLabelLayoutData() {
		GridData layoutData = new GridData(SWT.CENTER, SWT.CENTER, false, false);

		return layoutData;
	}

	protected GridData createComponentLayoutData() {
		GridData layoutData = new GridData(SWT.FILL, SWT.TOP, true, false);

		return layoutData;
	}

	private static GridLayout createLayout() {
		GridLayout layout = new GridLayout(1, true);
		GUIBuilderUtilities.clearMargins(layout);
		GUIBuilderUtilities.clearSpacing(layout);

		return layout;
	}
}