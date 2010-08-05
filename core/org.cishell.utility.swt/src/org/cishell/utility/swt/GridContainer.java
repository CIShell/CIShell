package org.cishell.utility.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public class GridContainer {
	private Composite actualParent;
	private int columnCount;
	private List<GridRow> rows = new ArrayList<GridRow>();

	public GridContainer(Composite actualParent, int columnCount) {
		this.actualParent = actualParent;
		this.columnCount = columnCount;
	}

	public Composite getActualParent() {
		return this.actualParent;
	}

	public int getColumnCount() {
		return this.columnCount;
	}

	public int getRowCount() {
		return this.rows.size();
	}

	public GridRow addComponent(Widget component) {
		GridRow lastRow = getOrCreateLastUsableRow();
		lastRow.addComponent(component);

		return lastRow;
	}

	public void removeRow(int rowIndex) {
		this.rows.get(rowIndex).dispose();
		this.rows.remove(rowIndex);
	}

	private GridRow getOrCreateLastUsableRow() {
		final int rowCount = getRowCount();

		if (rowCount == 0) {
			return addNewRow();
		} else {
			GridRow lastRow = this.rows.get(rowCount - 1);

			if (lastRow.componentCount < getColumnCount()) {
				return lastRow;
			} else {
				return addNewRow();
			}
		}
	}

	private GridRow addNewRow() {
		GridRow row = new GridRow(getRowCount());
		this.rows.add(row);

		return row;
	}

	public class GridRow {
		private int rowIndex;
		private int componentCount = 0;
		private Collection<Widget> components =
			new ArrayList<Widget>(GridContainer.this.columnCount);

		private GridRow(int rowIndex) {
			this.rowIndex = rowIndex;
		}

		public int getRowIndex() {
			return this.rowIndex;
		}

		private void addComponent(Widget component) {
			this.components.add(component);
			this.componentCount++;
		}

		private void dispose() {
			for (Widget component : this.components) {
				component.dispose();
			}
		}
	}
}
