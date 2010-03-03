package org.cishell.utilities;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.util.collections.IntIterator;

public class TableUtilities {
	public static Table copyTable(Table oldTable) {
		Schema oldSchema = oldTable.getSchema();
		Table newTable = oldSchema.instantiate();
		
		for (Iterator rowIt = oldTable.tuples(); rowIt.hasNext();) {
			Tuple row = (Tuple) rowIt.next();			
			newTable.addTuple(row);
		}		
		
		return newTable;
	}

	public static Table copyNRowsFromTableUsingIntIterator(
			Table originalTable, IntIterator iterator, int topN, boolean isDescending)
	{
		// TODO: Add a couple comments in this method
		
		Schema tableSchema = originalTable.getSchema();
		final int numTableRows = originalTable.getRowCount();
		Table newTable = createTableUsingSchema(tableSchema);
		final int numRowsToCopy = Math.min(numTableRows, topN);
		int[] originalTableRowsToCopy = new int [numTableRows];
		
		newTable.addRows(numRowsToCopy);
		
		for (int ii = 0; ii < numTableRows; ii++) {
			originalTableRowsToCopy[ii] = iterator.nextInt();
		}

		// TODO: Comment the side-effects here
		
		if (!isDescending) {
			for (int ii = 0; ii < numRowsToCopy; ii++) {
				copyTableRow(ii, originalTableRowsToCopy[ii], newTable, originalTable);
			}
		}
		else {
			for (int ii = 0; ii < numRowsToCopy; ii++) {
				copyTableRow(
					ii, originalTableRowsToCopy[numTableRows - ii - 1], newTable, originalTable);
			}
		}

		return newTable;
	}

	public static void copyTableRow(
			int newTableRow, int originalTableRow, Table newTable, Table originalTable)
	{
		final int numTableColumns = originalTable.getColumnCount();
		
		for (int ii = 0; ii < numTableColumns; ii++)
			newTable.set(newTableRow, ii, originalTable.get(originalTableRow, ii));
	}

	public static List<String> getAllColumnNames(Schema schema) throws ColumnNotFoundException {
		List<String> workingColumnNames = new ArrayList<String>();

		for (int ii = 0; ii < schema.getColumnCount(); ii++) {
			workingColumnNames.add(schema.getColumnName(ii));
		}
		
		if (workingColumnNames.size() == 0) {
			throw new ColumnNotFoundException("No columns found in the schema.");
		}
	
		return workingColumnNames;
	}

	public static String[] getValidStringColumnNamesInTable(Table table)
			throws ColumnNotFoundException {
    	return filterSchemaColumnNamesByClass(table.getSchema(), String.class);
    }

	public static String[] getValidNumberColumnNamesInTable(Table table)
			throws ColumnNotFoundException {
		Class<?>[] possibleNumberClasses = {
			byte.class,
			byte[].class,
			Byte.class,
			Byte[].class,
			short.class,
			short[].class,
			Short.class,
			Short[].class,
			int.class,
			int[].class,
			Integer.class,
			Integer[].class,
			long.class,
			long[].class,
			Long.class,
			Long[].class,
			float.class,
			float[].class,
			Float.class,
			Float[].class,
			double.class,
			double[].class,
			Double.class,
			Double[].class
		};
		
		return filterSchemaColumnNamesByClasses(table.getSchema(), possibleNumberClasses);
	}

	public static String[] getValidIntegerColumnNamesInTable(Table table)
			throws ColumnNotFoundException {
		Class<?>[] possibleIntegerClasses = {
			int.class,
			Integer.class,
			int[].class,
			Integer[].class
		};
		
    	return filterSchemaColumnNamesByClasses(table.getSchema(), possibleIntegerClasses);
    }

	public static String[] getValidDateColumnNamesInTable(Table table)
			throws ColumnNotFoundException {
		Class<?>[] possibleDateClasses = {
			Date.class,
			int.class,
			Integer.class,
			String.class,
			int[].class,
			Integer[].class,
			String[].class,
		};
		
    	return filterSchemaColumnNamesByClasses(table.getSchema(), possibleDateClasses);
    }

	public static String[] filterSchemaColumnNamesByClasses(
			Schema schema, Class<?>[] objectClasses) throws ColumnNotFoundException {
		ArrayList<String> workingColumnNames = new ArrayList<String>();
		
		for (int ii = 0; ii < schema.getColumnCount(); ii++) {
			for (Class<?> objectClass : objectClasses) {
				if (objectClass.isAssignableFrom(schema.getColumnType(ii))) {
					workingColumnNames.add(schema.getColumnName(ii));
					
					break;
				}
			}
		}
		
		if (workingColumnNames.size() > 0) {
			String[] finalColumnNames = new String [workingColumnNames.size()];

			return (String[])workingColumnNames.toArray(finalColumnNames);
		} else {
			// An exception is thrown if there is not at least 1 column name.
			StringBuffer objectClassesString = new StringBuffer();
			objectClassesString.append("[");
			
			for (int ii = 0; ii < objectClasses.length; ii++) {
				objectClassesString.append(objectClasses[ii].getName());
				
				if ((ii + 1) < objectClasses.length) {
					objectClassesString.append(", ");
				}
			}
			
			objectClassesString.append("]");
			
			throw new ColumnNotFoundException(
				"No column of types " + objectClassesString + " was found.");
		}
	}

	public static String[] filterSchemaColumnNamesByClass(Schema schema, Class<?> objectClass)
			throws ColumnNotFoundException {
		ArrayList<String> workingColumnNames = new ArrayList<String>();

		for (int ii = 0; ii < schema.getColumnCount(); ii++) {
			if (objectClass.isAssignableFrom(schema.getColumnType(ii)))
				workingColumnNames.add(schema.getColumnName(ii));
		}
		
		if (workingColumnNames.size() == 0) {
			throw new ColumnNotFoundException(
				"No column of type " + objectClass.getName() + " was found.");
		}

		String[] finalColumnNames = new String [workingColumnNames.size()];

		return (String[])workingColumnNames.toArray(finalColumnNames);
	}
	
	public static String formNonConflictingNewColumnName(Schema schema, String suggestedColumnName)
			throws ColumnNotFoundException {
		List<String> workingColumnNames = getAllColumnNames(schema);
	
		if (!workingColumnNames.contains(suggestedColumnName)) {
			return suggestedColumnName;
		} else {
			int columnNameSuffix = 1;

			while(true) {
				String newColumnName = suggestedColumnName.concat("_" + columnNameSuffix);

				if (!workingColumnNames.contains(newColumnName)) {
					return newColumnName;
				}

				columnNameSuffix++;
			}
		}
	}
	
	public static String formNonConflictingNewColumnName(
			Schema schema, String[] suggestedColumnNames) throws ColumnNotFoundException {
		List<String> workingColumnNames = getAllColumnNames(schema);
		boolean suggestedNameFound = false;

		for (String suggestedName : suggestedColumnNames) {
			for (String workingColumnName : workingColumnNames) {
				if (workingColumnName.equalsIgnoreCase(suggestedName)) {
					suggestedNameFound = true;

					break;
				}
			}

			/*
			 * To ensure that whenever a suggested name is found in the original column schema,
			 *  create a name.
			 */
			if (suggestedNameFound) {
				break;
			}
		}
		
		/*
		 * If none of the suggested names are conflicting then return the first suggested name.
		 */
		if(!suggestedNameFound) {
			return suggestedColumnNames[0];
		}

		/*
		 * This part of code will be executed only if the suggested names are already present in
		 *  the column schema.
		 */
		boolean newColumnNameFound = false;
		int columnNameSuffix = 2;

		while(true) {
			/*
			 * The pattern for new names will be taken from the first suggested column name.
			 */
			String newColumnName = suggestedColumnNames[0].concat("_" + columnNameSuffix);

			for (String workingColumnName : workingColumnNames) {
				if (workingColumnName.equalsIgnoreCase(newColumnName)) {
					newColumnNameFound = true;

					break;
				}
			}

			if (!newColumnNameFound) {
				return newColumnName;
			}

			columnNameSuffix++;
		}
	}
	
	/**
	 * @deprecated Replace calls with schema.instantiate().
	 */
	public static Table createTableUsingSchema(Schema tableSchema) {
		final int numTableColumns = tableSchema.getColumnCount();
		Table table = new Table();
		
		for (int ii = 0; ii < numTableColumns; ii++) {
			table.addColumn(tableSchema.getColumnName(ii), tableSchema.getColumnType(ii));
		}
		
		return table;
	}

	public static double extractDoubleFromCell(Tuple row, String columnName)
			throws NumberFormatException {
		double value = NumberUtilities.interpretObjectAsDouble(row.get(columnName)).doubleValue();

		return value;
	}
}