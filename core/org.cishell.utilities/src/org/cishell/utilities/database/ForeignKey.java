package org.cishell.utilities.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cishell.utilities.DatabaseUtilities;
import org.cishell.utilities.StringUtilities;

public final class ForeignKey {

	final public DatabaseTable localTable;
	final public DatabaseTable otherTable;
	final public Set<ColumnPair> pairs;

	public ForeignKey(DatabaseTable localTable, DatabaseTable otherTable, Set<ColumnPair> pairs) {
		this.localTable = localTable;
		this.otherTable = otherTable;
		this.pairs = Collections.unmodifiableSet(pairs);
	}

	public void repoint(List<Map<String, Object>> from,
			Map<String, Object> to, Connection connection) throws SQLException {
		
		String updateQuery = constructUpdateQuery(from, to);
		//TODO: remove
		System.err.println("Issuing update: " + updateQuery);
		connection.createStatement().executeUpdate(updateQuery);
		
	}

	private String constructUpdateQuery(List<Map<String, Object>> from,
			Map<String, Object> to) {
		return "UPDATE " + otherTable.toString() + " SET "+ formatUpdates(to) + " WHERE "
				+ DatabaseUtilities.createSQLInExpression(getForeignColumnNames(), translateToForeignNames(from));
	}

	

	private List<Map<String, Object>> translateToForeignNames(
			List<Map<String, Object>> from) {
		List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> fromValues : from) {
			Map<String, Object> toValues = new HashMap<String, Object>();
			for(ColumnPair pair : pairs) {
				toValues.put(pair.foreign, fromValues.get(pair.local));
			}
			output.add(toValues);
		}
		
		
		return output;
	}

	private List<String> getForeignColumnNames() {
		List<String> foreignColumns = new ArrayList<String>();
		for(ColumnPair pair : pairs) {
			foreignColumns.add(pair.foreign);
		}
		return foreignColumns;
	}

	

	private String formatUpdates(Map<String, Object> to) {
		List<String> updateStatements = new ArrayList<String>();
		for(ColumnPair pair : pairs) {
			String foreignColumn = pair.foreign;
			Object newValue = to.get(pair.local);
			updateStatements.add(foreignColumn + " = " + DatabaseUtilities.formatValue(newValue));
		}
		return StringUtilities.implodeList(updateStatements, ", ");
	}
	
	

}
