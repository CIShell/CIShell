package org.cishell.utilities;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.TabExpander;

import junit.framework.TestCase;

import org.junit.Test;

import prefuse.data.Table;
import prefuse.util.collections.IntIterator;

public class TableUtilitiesTests extends TestCase {
	Table table;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.table = new Table();
		this.table.addColumn("String", String.class);
		this.table.addColumn("int", int.class);
		this.table.addColumn("Integer[]", Integer[].class);
		for (int i = 0; i < 10; i++) {
			int r = this.table.addRow();
			this.table.set(r, "String", "Hello!" + r);
			this.table.set(r, "int", r);
			this.table.set(r, "Integer[]", new Integer[] { 1, 2, r });
		}
		//
		// for (IntIterator rows = this.ttable.rows(); rows.hasNext();) {
		// int row = (Integer) rows.next();
		// System.out.println(this.table.getTuple(row));
		// }
	}

	@Test
	public void testCopyTable() {
		Table t2 = TableUtilities.copyTable(this.table);

		for (IntIterator rows = t2.rows(); rows.hasNext();) {
			int row = (Integer) rows.next();
			for (int col = 0; col < t2.getColumnCount(); col++) {
				assertTrue(t2.get(row, col) + " " + this.table.get(row, col)
						+ " didn't match.",
						t2.get(row, col).equals(this.table.get(row, col)));
			}

		}

		this.table.removeRow(3);
		try {
			this.table.get(3, 1);
			fail();
		} catch (IllegalArgumentException e) {
			// ok, row three is gone!
		}
		
		// Make sure row three is still in the table we copied.
		t2.get(3, 1);

		t2 = TableUtilities.copyTable(this.table);
		for (IntIterator t2Rows = t2.rows(), tableRows = this.table.rows(); t2Rows
				.hasNext();) {
			int t2Row = (Integer) t2Rows.next();
			int tableRow = (Integer) tableRows.next();
			for (int col = 0; col < t2.getColumnCount(); col++) {
				Object t2Value = t2.get(t2Row, col);
				Object tableValue = this.table.get(tableRow, col);
				assertTrue("3 should have been removed!", !t2Value.equals(3));
				assertTrue(t2Value + " " + tableValue + " didn't match.",
						t2Value.equals(tableValue));
			}

		}
	}
	
	@Test
	public void testCopyNRowsFromTableUsingIntIterator() {
		Table tbl = TableUtilities.copyNRowsFromTableUsingIntIterator(
				this.table, this.table.rows(), Integer.MAX_VALUE, true);
		assertTrue(tbl.getRowCount() == this.table.getRowCount());

		tbl = TableUtilities.copyNRowsFromTableUsingIntIterator(this.table,
				this.table.rows(), Integer.MAX_VALUE, false);
		assertTrue(tbl.getRowCount() == this.table.getRowCount());

		tbl = TableUtilities.copyNRowsFromTableUsingIntIterator(this.table,
				this.table.rows(), 0, false);
		assertTrue(tbl.getRowCount() == 0);

		try {
			TableUtilities.copyNRowsFromTableUsingIntIterator(this.table,
					this.table.rows(), -100, false);
			fail();
		} catch (IllegalArgumentException e) {
			// good, negative is not allowed
		}

		tbl = TableUtilities.copyNRowsFromTableUsingIntIterator(this.table,
				this.table.rows(), 3, true);
		assertTrue(tbl.getRowCount() == 3);
		tbl = TableUtilities.copyNRowsFromTableUsingIntIterator(this.table,
				this.table.rows(), 3, false);
		assertTrue(tbl.getRowCount() == 3);
		
		tbl =  TableUtilities.copyNRowsFromTableUsingIntIterator(this.table, this.table.rowsSortedBy("int", true), 1, true);
		assertTrue(tbl.getRowCount() == 1);
		assertTrue(tbl.get(0, "int").equals(9));
		
		tbl =  TableUtilities.copyNRowsFromTableUsingIntIterator(this.table, this.table.rowsSortedBy("int", true), 1, false);
		assertTrue(tbl.getRowCount() == 1);
		assertTrue(tbl.get(0, "int").equals(0));
		
		tbl =  TableUtilities.copyNRowsFromTableUsingIntIterator(this.table, this.table.rowsSortedBy("int", true), 3, true);
		assertTrue(tbl.getRowCount() == 3);
		assertTrue(tbl.get(0, "int").equals(9));
		assertTrue(tbl.get(1, "int").equals(8));
		assertTrue(tbl.get(2, "int").equals(7));
		
		tbl =  TableUtilities.copyNRowsFromTableUsingIntIterator(this.table, this.table.rowsSortedBy("int", true), 3, false);
		assertTrue(tbl.getRowCount() == 3);
		assertTrue(tbl.get(0, "int").equals(0));
		assertTrue(tbl.get(1, "int").equals(1));
		assertTrue(tbl.get(2, "int").equals(2));
	}
	
	@Test
	public void testCopyTableRow() {
		/*
		 * Check the good, normal case
		 */
		
		Table table2 = TableUtilities.copyTable(this.table);
		int row = table2.addRow();
		
		TableUtilities.copyTableRow(row, 0, table2, this.table);
		
		for (int i = 0; i < table2.getColumnCount(); i++) {			
			assertTrue(table2.get(0, i).equals(table2.get(row, i)));
		}
		
		/*
		 * Check crazy values
		 */
		try {
			TableUtilities.copyTableRow(row, 89239, table2, this.table);
			fail();
		} catch (IllegalArgumentException e) {
			// Good, it was noticed that was a bad row
			assertTrue(e.getMessage().contains("Invalid row number"));
		}
		
		try {
			TableUtilities.copyTableRow(-15, 2, table2, this.table);
			fail();
		} catch (IllegalArgumentException e) {
			// Good, it was noticed that was a bad row
			assertTrue(e.getMessage().contains("Invalid row number"));
		}
		
		try {
			TableUtilities.copyTableRow(0, 0, null, this.table);
			fail();
		} catch (NullPointerException e) {
			// Good, it was caught.
		}
		
		try {
			TableUtilities.copyTableRow(0, 0, this.table, null);
			fail();
		} catch (NullPointerException e) {
			// Good, it was caught.
		}
		
		/*
		 * Check unassignable tables
		 */
		table2 = new Table();
		table2.addColumn("String", String.class);
		table2.addColumn("int", int.class);
		
		row = table2.addRow();
		
		try{
			TableUtilities.copyTableRow(row, 0, table2, this.table);
			fail();
		}catch(IllegalArgumentException e) {
			// The tables can't be assigned from each other, so it should give an error.
		}
		
	}
	
	@Test
	public void testGetAllColumnNames() {
		List<String> names = TableUtilities.getAllColumnNames(this.table.getSchema());
		assertTrue(names.containsAll(Arrays.asList(new String[]{"String", "int", "Integer[]"})));
		
		this.table.addColumn("FooBarz1!", Class.class);
		names = TableUtilities.getAllColumnNames(this.table.getSchema());
		assertTrue(names.containsAll(Arrays.asList(new String[]{"String", "int", "Integer[]", "FooBarz1!"})));
		
		this.table.removeColumn("FooBarz1!");
		names = TableUtilities.getAllColumnNames(this.table.getSchema());
		assertFalse(names.contains("FooBarz1!"));
		
		Table table2 = new Table();
		try {
			names = TableUtilities.getAllColumnNames(table2.getSchema());
			fail();
		} catch (ColumnNotFoundException e) {
			// Good, there are no column names!
		}
		
		table2.addColumn("", Class.class);
		names = TableUtilities.getAllColumnNames(table2.getSchema());
		assertTrue(names.size() == 1);
		assertTrue(names.contains(""));
	}
	
	@Test
	public void testGetValidXColumnNamesInTable() {
		Table t = new Table();
		t.addColumn("S1", String.class);
		t.addColumn("S2", String.class);
		t.addColumn("int1", int.class);
		t.addColumn("int2", Integer[].class);
		t.addColumn("float1", float.class);
		t.addColumn("number1", Number.class);
		t.addColumn("date1", Date.class);
		t.addColumn("boolean1", boolean.class);
		
		String[] stringColumns = TableUtilities.getValidStringColumnNamesInTable(t);
		assertTrue(Arrays.asList(stringColumns).containsAll(Arrays.asList(new String[]{"S1", "S2"})));
		assertFalse(Arrays.asList(stringColumns).contains("int1"));
		assertFalse(Arrays.asList(stringColumns).contains("int2"));
		assertFalse(Arrays.asList(stringColumns).contains("float1"));
		assertFalse(Arrays.asList(stringColumns).contains("number1"));
		assertFalse(Arrays.asList(stringColumns).contains("date1"));
		assertFalse(Arrays.asList(stringColumns).contains("boolean1"));
		
		String[] numberColumns = TableUtilities.getValidNumberColumnNamesInTable(t);
		assertTrue(Arrays.asList(numberColumns).containsAll(Arrays.asList(new String[]{"int1", "float1", "int2"})));
		assertFalse(Arrays.asList(numberColumns).contains("S1"));
		assertFalse(Arrays.asList(numberColumns).contains("S2"));
		assertFalse(Arrays.asList(numberColumns).contains("date1"));
		assertFalse(Arrays.asList(numberColumns).contains("boolean1"));
		
		String[] dateColumns = TableUtilities.getValidDateColumnNamesInTable(t);
		assertTrue(Arrays.asList(dateColumns).containsAll(Arrays.asList(new String[]{"int1", "S1", "S2", "date1", "int2"})));
		assertFalse(Arrays.asList(dateColumns).contains("float1"));
		assertFalse(Arrays.asList(dateColumns).contains("number1"));	
		assertFalse(Arrays.asList(dateColumns).contains("boolean1"));
		
		//int
		String[] intColumns = TableUtilities.getValidIntegerColumnNamesInTable(t);
		assertTrue(Arrays.asList(intColumns).containsAll(Arrays.asList(new String[]{"int1", "int2"})));
		assertFalse(Arrays.asList(intColumns).contains("S1"));
		assertFalse(Arrays.asList(intColumns).contains("S2"));
		assertFalse(Arrays.asList(intColumns).contains("float1"));
		assertFalse(Arrays.asList(intColumns).contains("number1"));
		assertFalse(Arrays.asList(intColumns).contains("date1"));
		assertFalse(Arrays.asList(intColumns).contains("boolean1"));
		
		t = new Table();
		try {
			TableUtilities.getValidStringColumnNamesInTable(t);
			fail();
		} catch (ColumnNotFoundException e) {
			// Good, there are no columns in the table
		}

		try {
			TableUtilities.getValidNumberColumnNamesInTable(t);
			fail();
		} catch (ColumnNotFoundException e) {
			// Good, there are no columns in the table
		}

		try {
			TableUtilities.getValidDateColumnNamesInTable(t);
			fail();
		} catch (ColumnNotFoundException e) {
			// Good, there are no columns in the table
		}

		try {
			TableUtilities.getValidIntegerColumnNamesInTable(t);
			fail();
		} catch (ColumnNotFoundException e) {
			// Good, there are no columns in the table
		}

		t = null;
		try {
			TableUtilities.getValidStringColumnNamesInTable(t);
			fail();
		} catch (NullPointerException e) {
			// Good, the table was null
			assertTrue(e.getMessage().contains("The table should not be null"));
		}

		try {
			TableUtilities.getValidNumberColumnNamesInTable(t);
			fail();
		} catch (NullPointerException e) {
			// Good, the table was null
			assertTrue(e.getMessage().contains("The table should not be null"));
		}

		try {
			TableUtilities.getValidDateColumnNamesInTable(t);
			fail();
		} catch (NullPointerException e) {
			// Good, the table was null
			assertTrue(e.getMessage().contains("The table should not be null"));
		}

		try {
			TableUtilities.getValidIntegerColumnNamesInTable(t);
			fail();
		} catch (NullPointerException e) {
			// Good, the table was null
			assertTrue(e.getMessage().contains("The table should not be null"));
		}
	}
	
	@Test
	public void testFilterSchemaColumnNamesByClass() {
		try {
			TableUtilities.filterSchemaColumnNamesByClass(null, Class.class);
			fail();
		} catch (NullPointerException e) {
			// Ok
			assertTrue(e.getMessage().contains("schema"));
		}

		try {
			TableUtilities.filterSchemaColumnNamesByClass(
					this.table.getSchema(), null);
			fail();
		} catch (NullPointerException e) {
			// Ok
			assertTrue(e.getMessage().contains("objectClass"));
		}
		
		List<String> stringNames = Arrays.asList(TableUtilities.filterSchemaColumnNamesByClass(this.table.getSchema(), String.class));
		assertTrue(stringNames.contains("String"));
		assertFalse(stringNames.contains("int"));
		
		List<String> intNames = Arrays.asList(TableUtilities.filterSchemaColumnNamesByClass(this.table.getSchema(), int.class));
		assertTrue(intNames.contains("int"));
		assertFalse(intNames.contains("String"));
	}
	
	@Test
	public void testFilterSchemaColumnNamesByClasses() {
		try {
			TableUtilities.filterSchemaColumnNamesByClasses(null, new Class[] {Class.class});
			fail();
		} catch (NullPointerException e) {
			// Ok
			assertTrue(e.getMessage().contains("schema"));
		}

		try {
			TableUtilities.filterSchemaColumnNamesByClasses(
					this.table.getSchema(), null);
			fail();
		} catch (NullPointerException e) {
			// Ok
			assertTrue(e.getMessage().contains("objectClasses"));
		}
		
		List<String> stringNames = Arrays.asList(TableUtilities.filterSchemaColumnNamesByClasses(this.table.getSchema(), new Class[] {String.class}));
		assertTrue(stringNames.contains("String"));
		assertFalse(stringNames.contains("int"));
		
		List<String> intNames = Arrays.asList(TableUtilities.filterSchemaColumnNamesByClasses(this.table.getSchema(), new Class[] {int.class}));
		assertTrue(intNames.contains("int"));
		assertFalse(intNames.contains("String"));
		
		List<String> intAndStringNames = Arrays.asList(TableUtilities.filterSchemaColumnNamesByClasses(this.table.getSchema(), new Class[] {int.class, String.class}));
		assertTrue(intAndStringNames.contains("int"));
		assertTrue(intAndStringNames.contains("String"));
	}
	
	@Test
	public void testFormNonConflictingNewColumnName() {
		
		/**
		 * Test the version with only one suggestion
		 */
		
		try {
			TableUtilities.formNonConflictingNewColumnName(this.table.getSchema(), (String) null);
			fail();
		} catch (NullPointerException e) {
			assertTrue(e.getMessage().contains("suggestedColumnName"));
		}
		
		try {
			TableUtilities.formNonConflictingNewColumnName(null, "suggestion");
			fail();
		} catch (NullPointerException e) {
			assertTrue(e.getMessage().contains("schema"));
		}
		
		try {
			TableUtilities.formNonConflictingNewColumnName(new Table().getSchema(), "suggestion");
			fail();
		} catch (ColumnNotFoundException e) {
			// Ok, the schema was empty and we got a normal message
		}
		
		assertTrue("I am a batman!".equals(TableUtilities.formNonConflictingNewColumnName(this.table.getSchema(), "I am a batman!")));
		assertFalse("int".equals(TableUtilities.formNonConflictingNewColumnName(this.table.getSchema(), "int")));
		
		
		/**
		 * Test the version with multiple suggestions
		 */
		
		try {
			TableUtilities.formNonConflictingNewColumnName(this.table.getSchema(), new String[] {null});			
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().contains("suggestedColumnNames"));
		}
		
		try {
			TableUtilities.formNonConflictingNewColumnName(null, new String[]{"suggestion"});
			fail();
		} catch (NullPointerException e) {
			assertTrue(e.getMessage().contains("schema"));
		}
		try {
			TableUtilities.formNonConflictingNewColumnName(new Table().getSchema(), new String[]{"suggestion"});
			fail();
		}catch (ColumnNotFoundException e) {
			// Ok, the schema was empty and we got a normal message
		}
		
		assertTrue("I am a batman!".equals(TableUtilities.formNonConflictingNewColumnName(this.table.getSchema(), new String[]{"I am a batman!", "he is the batman!"})));
		String nonConflict = TableUtilities.formNonConflictingNewColumnName(this.table.getSchema(), new String[]{"int", "batman"});
		assertTrue(nonConflict.equals("batman"));
		assertFalse(nonConflict.equals("int"));
		
		nonConflict = TableUtilities.formNonConflictingNewColumnName(this.table.getSchema(), new String[]{"int", "String"});
		assertFalse(nonConflict.equals("int"));
		assertFalse(nonConflict.equals("int"));
		assertTrue(nonConflict.contains("int"));
	}

}
