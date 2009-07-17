package edu.iu.scipolicy.utilities;

import static org.junit.Assert.fail;

import java.util.Date;

import org.cishell.reference.service.metatype.BasicAttributeDefinition;
import org.cishell.utilities.TableUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.service.metatype.AttributeDefinition;

import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.util.collections.IntIterator;

public class TableUtilitiesTest {
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFilterSchemaColumnNamesByClass() {
		Schema testSchema = formTestSchema();
		
		try {
			// Get all of the column names of all of the types of columns.
		
			String[] stringColumnNames =
				TableUtilities.filterSchemaColumnNamesByClass
					(testSchema, String.class);
			
			String[] dateColumnNames =
				TableUtilities.filterSchemaColumnNamesByClass
					(testSchema, Date.class);
			
			String[] integerColumnNames =
				TableUtilities.filterSchemaColumnNamesByClass
					(testSchema, Integer.class);
			
			// For each set of column names, make sure they are the correct size and
			// contain the correct contents.
			
			if ((stringColumnNames.length != 2) ||
				!stringColumnNames[0].equals("string1") ||
				!stringColumnNames[1].equals("string2"))
			{
				fail();
			}
			
			if ((dateColumnNames.length != 2) ||
				!dateColumnNames[0].equals("date1") ||
				!dateColumnNames[1].equals("date2"))
			{
				fail();
			}
			
			if ((integerColumnNames.length != 2) ||
				!integerColumnNames[0].equals("integer1") ||
				!integerColumnNames[1].equals("integer2"))
			{
				fail();
			}
		}
		catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testCreateTableUsingSchema() {
		Schema testSchema = formTestSchema();
		
		try {
			Table testTable = TableUtilities.createTableUsingSchema(testSchema);
			Schema testTableSchema = testTable.getSchema();
			
			if (!schemasAreEqual(testSchema, testTableSchema))
				fail();
		}
		catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testGetNonConflictingNewColumnName() {
		Schema testSchema = formTestSchema();
		
		try {
			Table testTable = TableUtilities.createTableUsingSchema(testSchema);
			Schema testTableSchema = testTable.getSchema();
			
			String newColumnName = TableUtilities.formNonConflictingNewColumnName(testTableSchema, "string1");
			
			if (!newColumnName.equalsIgnoreCase("string1_1")) {
				fail();
			}
		}
		catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testCopyTableRow() {
		Table table1 = formTestTableWithValidSchema();
		Table table2 = formTestTableWithValidSchema();
		
		// Put a row in table1, then copy it to table2 and verify that table1 and
		// table2 have the same contents.
		
		table1.addRow();
		table1.set(0, 0, "Test1");
		table1.set(0, 1, new Date(1984, 0, 1));
		table1.set(0, 2, new Integer(1));
		table1.set(0, 3, "Test2");
		table1.set(0, 4, new Date(1984, 0, 2));
		table1.set(0, 5, new Integer(2));
		
		// table2 has to have the empty row first.
		table2.addRow();
		
		try {
			TableUtilities.copyTableRow(0, 0, table2, table1);
			
			if (!tableRowsAreEqual(table1, table2, 0, 0))
				fail();
		}
		catch (Exception e) {
			fail();
		}
	}

	@Test
	public void testCopyNRowsFromTableUsingIntIteratorAscending() {
		Table table1 = formTestTableWithValidSchema();
		
		// Put some rows in table1, then copy them to table2 and verify that table1
		// and table2 have the same contents.
		
		table1.addRow();
		table1.set(0, 0, "Test1a");
		table1.set(0, 1, new Date(1984, 0, 1));
		table1.set(0, 2, new Integer(1));
		table1.set(0, 3, "Test1b");
		table1.set(0, 4, new Date(1984, 0, 2));
		table1.set(0, 5, new Integer(2));
		
		table1.addRow();
		table1.set(1, 0, "Test2a");
		table1.set(1, 1, new Date(1984, 1, 1));
		table1.set(1, 2, new Integer(11));
		table1.set(1, 3, "Test2b");
		table1.set(1, 4, new Date(1984, 1, 2));
		table1.set(1, 5, new Integer(12));
		
		try {
			final int numTableRows = table1.getRowCount();
			IntIterator iterator = table1.rowsSortedBy("integer1", true);
			
			Table table2 = TableUtilities.copyNRowsFromTableUsingIntIterator
				(table1, iterator, numTableRows, false);
			
			for (int ii = 0; ii < numTableRows; ii++) {
				if (!tableRowsAreEqual(table1, table2, ii, ii))
					fail();
			}
		}
		catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void testCopyNRowsFromTableUsingIntIteratorDescending() {
		Table table1 = formTestTableWithValidSchema();
		
		// Put some rows in table1, then copy them to table2 and verify that table1
		// and table2 have the same contents.
		
		table1.addRow();
		table1.set(0, 0, "Test1a");
		table1.set(0, 1, new Date(1984, 0, 1));
		table1.set(0, 2, new Integer(1));
		table1.set(0, 3, "Test1b");
		table1.set(0, 4, new Date(1984, 0, 2));
		table1.set(0, 5, new Integer(2));
		
		table1.addRow();
		table1.set(1, 0, "Test2a");
		table1.set(1, 1, new Date(1984, 1, 1));
		table1.set(1, 2, new Integer(11));
		table1.set(1, 3, "Test2b");
		table1.set(1, 4, new Date(1984, 1, 2));
		table1.set(1, 5, new Integer(12));
		
		try {
			final int numTableRows = table1.getRowCount();
			IntIterator iterator = table1.rowsSortedBy("integer1", true);
			
			Table table2 = TableUtilities.copyNRowsFromTableUsingIntIterator
				(table1, iterator, numTableRows, true);
			
			for (int ii = 0; ii < numTableRows; ii++) {
				if (!tableRowsAreEqual(table1, table2, ii, (numTableRows - ii - 1)))
					fail();
			}
		}
		catch (Exception e) {
			fail();
		}
	}
	
	private Schema formTestSchema() {
		String[] schemaColumnNames = new String[]
		{
			"string1", "date1", "integer1", "string2", "date2", "integer2"
		};
		
		Class[] schemaColumnTypes = new Class[]
		{
			String.class,
			Date.class,
			Integer.class,
			String.class,
			Date.class,
			Integer.class
		};
		
		return new Schema(schemaColumnNames, schemaColumnTypes);
	}
	
	private Table formTestTableWithValidSchema() {
		Table table = new Table();
		
		table.addColumn("string1", String.class);
		table.addColumn("date1", Date.class);
		table.addColumn("integer1", Integer.class);
		table.addColumn("string2", String.class);
		table.addColumn("date2", Date.class);
		table.addColumn("integer2", Integer.class);
		
		return table;
	}
	
	private AttributeDefinition formTestAttributeDefinition() {
		return new BasicAttributeDefinition("testID",
											"test_name",
											"test description",
											AttributeDefinition.STRING,
											null,
											null);
	}
	
	private boolean schemasAreEqual(Schema schema1, Schema schema2) {
		if (schema1.getColumnCount() != schema2.getColumnCount())
			return false;
		
		for (int ii = 0; ii < schema1.getColumnCount(); ii++) {
			if (!schema1.getColumnName(ii).equals(schema2.getColumnName(ii)) ||
				!schema1.getColumnType(ii).equals(schema2.getColumnType(ii)))
			{
				return false;
			}
			
			// Forget about defaults for now.
			/* if (schema1.getDefault(ii) != null) {
				if ((schema2.getDefault(ii) == null) ||
					!schema1.getDefault(ii).equals(schema2.getDefault(ii)))
				{
					return false;
				}
			}
			else if (schema2.getDefault(ii) != null)
				return false; */
		}
		
		return true;
	}
	
	private boolean tableRowsAreEqual
		(Table table1, Table table2, int table1Row, int table2Row)
	{
		Schema table1Schema = table1.getSchema();
		Schema table2Schema = table2.getSchema();
		
		if (!schemasAreEqual(table1Schema, table2Schema))
			fail();
		
		for (int ii = 0; ii < table1Schema.getColumnCount(); ii++) {
			if (!table1.get(table1Row, ii).equals(table2.get(table2Row, ii)))
				fail();
		}
	
		return true;
	}
}
