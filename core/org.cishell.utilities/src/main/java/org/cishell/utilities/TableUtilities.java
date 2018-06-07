package org.cishell.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.util.collections.IntIterator;

import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.osgi.service.log.LogService;
import org.apache.commons.io.FilenameUtils;

/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class TableUtilities {
	public static final Class<?>[] POSSIBLE_NUMBER_CLASSES = {
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

	public static final Class<?>[] POSSIBLE_INTEGER_CLASSES = {
		int.class,
		Integer.class,
		int[].class,
		Integer[].class
	};

	public static final Class<?>[] POSSIBLE_DATE_CLASSES = {
		Date.class,
		int.class,
		Integer.class,
		String.class,
		int[].class,
		Integer[].class,
		String[].class,
	};

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
		return filterSchemaColumnNamesByClasses(table.getSchema(), POSSIBLE_NUMBER_CLASSES);
	}

	public static String[] getValidIntegerColumnNamesInTable(Table table)
			throws ColumnNotFoundException {
    	return filterSchemaColumnNamesByClasses(table.getSchema(), POSSIBLE_INTEGER_CLASSES);
    }

	public static String[] getValidDateColumnNamesInTable(Table table)
			throws ColumnNotFoundException {
    	return filterSchemaColumnNamesByClasses(table.getSchema(), POSSIBLE_DATE_CLASSES);
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
	
	/**
	 * Replaces certain headers in a table with replacements specified in a given header map.
	 * 
	 * @param propertiesFilePath	Path to a file-format-specific properties file, which provides a 
	 * 								mapping from the original headers to the replacement ones.
	 * @param inputTable	A table whose headers need to be standardized
	 * @return	A new table with the correct headers in place
	 * @throws IOException If an error occurs while reading the properties file
	 * @throws IllegalArgumentException	If any given parameters are null
	 */
	public static Table standardizeTable(URL propertiesFilePath, Table inputTable) throws IOException {
		if (propertiesFilePath == null || inputTable == null)
			throw new IllegalArgumentException();
		
		Table returnTable = new Table();
		
		// grabs info about inputData
		Schema oldSchema = inputTable.getSchema();		
		final int numTableColumns = oldSchema.getColumnCount();
		final int numTableRows = inputTable.getRowCount();
		
		Map<String, String> headerMap = readHeaderProperties(propertiesFilePath);
		
		// add columns to return table, correcting them as iteration proceeds
		for (int i = 0; i < numTableColumns; i++) {
			String colHead = oldSchema.getColumnName(i);
			
			// check for columns that need updating here
			String newHeader = headerMap.get(colHead);
			if (newHeader != null)
				colHead = newHeader;
			
			returnTable.addColumn(colHead, oldSchema.getColumnType(i));
		}
		
		// add existing rows to return table
		returnTable.addRows(numTableRows);
		for (int i = 0; i < numTableRows; i++) {
			copyTableRow(i, i, returnTable, inputTable);
		}
			
		return returnTable;
	}
	
	/**
	 * Reads a properties file mapping data type specific headers to the desired headers.
	 * 
	 * @param propLocation	A URL representing the location of a .hmap format properties file
	 * @return	A String-to-String Map, with old header values as keys, and their replacements as values
	 * @throws IOException	If there is an IO error reading the properties file
	 */
	private static Map<String, String> readHeaderProperties(URL propLocation) throws IOException {
		
		// throws exception if properties file does not have .hmap (header map) extension
		String extension = FilenameUtils.getExtension(propLocation.getPath());
		if (!extension.equals("hmap"))
			throw new IOException("The given file extension is incompatible with this plugin. Please use a .hmap extension file instead.");
		
		InputStream in = propLocation.openStream();
		Reader reader = new InputStreamReader(in, "UTF-8");

		Properties prop = new Properties();
		try {
		    prop.load(reader);
		} finally {
		    reader.close();
		}
		
		HashMap<String, String> headerMap = new HashMap<String,String>();
		for (final String name : prop.stringPropertyNames()) {
		    headerMap.put(name, prop.getProperty(name));
		}
		
		return headerMap;
	}
}