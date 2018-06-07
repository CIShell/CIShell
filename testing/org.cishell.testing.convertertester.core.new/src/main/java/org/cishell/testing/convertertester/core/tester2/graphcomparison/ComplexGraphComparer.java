package org.cishell.testing.convertertester.core.tester2.graphcomparison;

import java.util.Iterator;

import org.cishell.testing.convertertester.core.tester.graphcomparison.TableUtil;
import org.cishell.testing.convertertester.core.tester.graphcomparison.RunningLog;

import prefuse.data.Table;
import prefuse.data.Tuple;

public abstract class ComplexGraphComparer extends SimpleGraphComparer {

	private RunningLog log;
	
	public ComplexGraphComparer () {
		
		this.log = new RunningLog();
	}
	
	protected RunningLog getLog() {
		return this.log;
	}
	
	protected void clearLog() {
		this.log = new RunningLog();
	}
	
	protected boolean areEqualWhenSorted(Table t1, Table t2) {	
		
		if (! haveSameColumns(t1, t2)) {
			return false;
		}
		
		String[] colNames = TableUtil.getColumnNames(t1);
		
 		boolean result = areEqual(TableUtil.getSortedByColumns(t1, colNames),
				TableUtil.getSortedByColumns(t2, colNames));
		return result;
	}
	
	/*
	 * Cares about the order of nodes and edges as well.
	 */
	protected boolean areEqual(Table t1, Table t2) {
		Iterator tuplesIterator1 = t1.tuples();
		Iterator tuplesIterator2 = t2.tuples();
		
		while (tuplesIterator1.hasNext()) {
			Tuple tuple1 = (Tuple) tuplesIterator1.next();
			Tuple tuple2 = (Tuple) tuplesIterator2.next();
			
			if (! areEqual(tuple1, tuple2)) {
				return false;
			}
		}
		
		return true;
	}
	
	protected boolean areEqual(Tuple tu1, Tuple tu2) {
		if (tu1.getColumnCount() != tu2.getColumnCount()) {
			log.append("Number of columns in tuples differ.");
			log.append("First tuple: " + tu1);
			log.append("Second tuple: " + tu2);
			return false;
		}
			
		for (int ii = 0; ii < tu1.getColumnCount(); ii++) {
			Object columnContents1 = tu1.get(ii);	
			
	        Object columnContents2 = null;
	        boolean foundMatchingColumn = false;
			for (int kk = 0; kk < tu2.getColumnCount(); kk++) {
				
				if (tu2.getColumnName(kk).equals(tu1.getColumnName(ii))) {
					columnContents2 = tu2.get(kk);
					foundMatchingColumn = true;
					break;
				}
			}
			
			//TODO: Possibly remove this, since it SHOULD be guaranteed 
			//not to happen by a check run before this algorithm
			if (! foundMatchingColumn) {
				log.append("Only one graph has the column " + 
						tu1.getColumnName(ii));
				
				log.append("example tuples: ");
				log.append(tu1.toString() + " : " + tu2.toString());
				return false;
			}
			
			String columnName = tu1.getColumnName(ii);
			
			if (columnContents1 == null && columnContents2 == null) {
				//nulls are equal to each other!
				continue;
			} else if (columnContents1 == null) {
				log.append("Column contents not equal!");
				log.append("For the column " + columnName + "," + 
						"field in first tuple is null while " +
						"the other is " + columnContents2.toString());
				log.append(tu1 + " : " + tu2);
				return false;
			} else if (columnContents2 == null) {
				log.append("Column contents not equal!");
				log.append("For the column " + columnName + "," + 
						"field in first tuple is " + 
						columnContents1.toString() + ", while  " +
						"the other is null");
				log.append(tu1 + " : " + tu2);
				return false;
			} else if (! columnContents1.equals(columnContents2)){
				log.append("Column contents not equal!");
				log.append("For the column " + columnName + "," + 
						"field in first tuple is " + 
						columnContents1.toString() + ", while  " +
						"the other is " + columnContents2.toString());
				String contents1Class = columnContents1.getClass().toString();
				String contents2Class = columnContents2.getClass().toString();
				log.append("Field 1 class: " + contents1Class);
				log.append("Field 2 class: " + contents2Class);
				log.append(tu1 + " : " + tu2);
				//neither are null, but they are still not equal.
				return false;
			}
		}
		
		//all column contents are equal.
		return true;
	}
	
	protected boolean haveSameColumns(Table t1, Table t2) {
		return firstHasColumnsOfSecond(t2, t1) &&
			firstHasColumnsOfSecond(t1, t2);
	}
	
	protected boolean firstHasColumnsOfSecond(Table t1, Table t2) {
		
		for (int ii = 0; ii < t2.getColumnCount(); ii++) {
			String t2Name = t2.getColumnName(ii);
			
			boolean foundMatch = false;
			for (int kk = 0; kk < t1.getColumnCount(); kk++) {
				String t1Name = t1.getColumnName(kk);
				
				if (t2Name.equals(t1Name)) {
					foundMatch = true;
					break;
				}
			}
			
			if (! foundMatch) {
				log.append("One table has the column '" + 
						t2.getColumnName(ii) + "', while the other does " +
						"not.");
				
				String[] t1ColNames = TableUtil.getColumnNames(t1);
				String[] t2ColNames = TableUtil.getColumnNames(t2);
				
			
				log.append("t1 columns: " + format(t1ColNames));
				log.append("t2 columns: " + format(t2ColNames));
				return false;
			}
		}
		
		return true;
	}
	
	protected String format(String[] strings) {
		String result = "[";
		for (int ii = 0; ii < strings.length; ii++) {
			if (ii < strings.length - 1) {
				result += strings[ii] + ", ";
			} else {
				result += strings[ii];
			}
		}
		result += "]";
		return result;
	}
}
