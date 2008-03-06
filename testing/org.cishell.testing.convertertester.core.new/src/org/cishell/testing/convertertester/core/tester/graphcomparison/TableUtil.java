package org.cishell.testing.convertertester.core.tester.graphcomparison;

import java.util.Iterator;

import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.data.expression.parser.ExpressionParser;
import prefuse.data.util.Sort;

public class TableUtil {
	
	public static String[] getColumnNames(Table t) {
		String[] columnNames = new String[t.getColumnCount()];
		for (int ii = 0; ii < t.getColumnCount(); ii++) {
			columnNames[ii] = t.getColumnName(ii);
		}
		return columnNames;
	}
		
	public static Table copyTable(Table t) {
		Table tCopy = new Table();
		tCopy.addColumns(t.getSchema());
		
		for (Iterator ii = t.tuples(); ii.hasNext();) {
			Tuple tuple = (Tuple) ii.next();
			tCopy.addTuple(tuple);
		}
		return tCopy;
	}
	
	public static void printTable(Table t) {
		Iterator ii = t.tuples();
		while (ii.hasNext()) {
			System.out.println((Tuple) ii.next());
		}
	}
	
	public static Table getSorted(Table t) {
		return getSortedByColumns(t, getColumnNames(t));
	}
	
	public static Table getSortedByColumns(Table t, String[] columnNames) {
		for (int ii = 0; ii < columnNames.length; ii++) {
			String columnName = columnNames[ii];
			if (t.getColumn(columnName) == null) {
				System.out.println("WTF, cannot find column " + columnName);
			}
		}
		Sort tSort = new Sort(columnNames);
		Table sortedTable = t.select(ExpressionParser.predicate("TRUE"),
				tSort);
		return sortedTable;
	}
	
	
}
