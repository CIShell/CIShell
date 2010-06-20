package org.cishell.utilities.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.cishell.utilities.DatabaseUtilities;
import org.cishell.utilities.StringUtilities;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public final class DatabaseTable {
	public final String catalog;
	public final String schema;
	public final String name;

	public DatabaseTable(String catalog, String schema, String name) {
		this.catalog = catalog == null ? "" : catalog.intern();
		this.schema = schema == null ? "" : schema.intern();
		this.name = name == null ? "" : name.intern();
	}
	
	public static DatabaseTable fromRepresentation(String representation) throws InvalidRepresentationException {
		String[] parts = representation.split("\\.");
		switch(parts.length) {
			case 1:
				return new DatabaseTable(null, null, parts[0]);
			case 2:
				return new DatabaseTable(null, parts[0], parts[1]);
			case 3:
				return new DatabaseTable(parts[0], parts[1], parts[2]);
			default:
				throw new InvalidRepresentationException("The representation '" + representation + "' has the wrong number of parts!");
		}
	}
	
	public static DatabaseTable[] availableTables(Connection connection) throws SQLException {
		ResultSet results = connection.getMetaData().getTables(null, null, null, new String[]{"TABLE"});
		
		List<DatabaseTable> tables = new ArrayList<DatabaseTable>();
		while(results.next()) {
			tables.add(new DatabaseTable(results.getString(1), results.getString(2), results.getString(3)));
		}
		
		results.close();
		
		return tables.toArray(new DatabaseTable[]{});
	}
	
	public String toString() {
		StringBuilder output = new StringBuilder();
		if(catalog != null && catalog != "") {
			output.append(catalog);
			output.append('.');
		}
		if(schema != null && schema != "") {
			output.append(schema);
			output.append('.');
		}
		
		output.append(name);
		
		return output.toString();
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof DatabaseTable)) {
			return false;
		}
		DatabaseTable o = (DatabaseTable) other;
		return o.catalog == this.catalog && o.schema == this.schema && o.name == this.name;
	}
	
	
	public int hashCode() {
		int hash = catalog.hashCode() * 7;
		hash += schema.hashCode() * 5;
		hash += name.hashCode() * 3;
		return hash;
		
	}
	
	public boolean presentInDatabase(Connection connection) throws SQLException {
		ResultSet results = connection.getMetaData().getTables(catalog, schema, name, new String[]{"TABLE"});
		boolean foundOne = results.next();
		results.close();
		return foundOne;
	}
	
	public boolean hasPrimaryKey(Connection connection) throws SQLException {
		return getPrimaryKeyColumns(connection).length != 0;
	}
	
	public ForeignKey[] getRelations(Connection connection) throws SQLException {
		ResultSet related = connection.getMetaData().getExportedKeys(catalog, schema, name);
		Map<ForeignKeyNameWithTable, Set<ColumnPair>> correspondences = new HashMap<ForeignKeyNameWithTable, Set<ColumnPair>>();
		while(related.next()) {
			String foreignKeyName = related.getString(12);
			DatabaseTable relatedTable = new DatabaseTable(related.getString(5), related.getString(6), related.getString(7));
			ForeignKeyNameWithTable key = new ForeignKeyNameWithTable(foreignKeyName, relatedTable);
			ColumnPair pair = new ColumnPair(related.getString(4), related.getString(8));
			if(!correspondences.containsKey(key)) {
				correspondences.put(key, new HashSet<ColumnPair>());
			}
			correspondences.get(key).add(pair);
		}
		
		related.close();
		
		return makeForeignKeys(correspondences);
	}

	private ForeignKey[] makeForeignKeys(
			Map<ForeignKeyNameWithTable, Set<ColumnPair>> correspondences) {
		ForeignKey[] foreignKeys = new ForeignKey[correspondences.size()];
		int index = 0;
		for(Map.Entry<ForeignKeyNameWithTable, Set<ColumnPair>> entry : correspondences.entrySet()) {
			foreignKeys[index] = new ForeignKey(this, entry.getKey().table, entry.getValue());
			index++;
		}
		return foreignKeys;
	}

	public String[] getPrimaryKeyColumns(Connection connection) throws SQLException {
		ResultSet columns = connection.getMetaData().getPrimaryKeys(catalog, schema, name);
		List<String> columnNames = new ArrayList<String>();
		while(columns.next()) {
			columnNames.add(columns.getString(4));
		}
		return columnNames.toArray(new String[]{});
	}

	public void deleteRowsByColumns(List<Map<String, Object>> otherEntities, Statement statement) throws SQLException {
		if(otherEntities.size() == 0) {
			return;
		}
		List<String> columns = new ArrayList<String>(otherEntities.get(0).keySet());
		String deleteStatement = constructDeleteStatement(columns, otherEntities);
		statement.addBatch(deleteStatement);
	}

	private String constructDeleteStatement(List<String> columns,
			List<Map<String, Object>> otherEntities) {
		return "DELETE FROM " + this.toString() + " WHERE " + DatabaseUtilities.createSQLInExpression(columns, otherEntities);
	}
	
	private String formatDeleteEquals(Connection connection, String separator) throws SQLException {
		//sorting is necessary to ensure keys and values match up
		SortedSet<String> keys = Sets.newTreeSet();
		String[] primaryKeys = getPrimaryKeyColumns(connection);
		for(String key : primaryKeys) {
			keys.add(key + " = ?");
		}
		return StringUtilities.implodeItems(Lists.newArrayList(keys), separator);
	}

	public Remover constructRemover(Connection connection) throws SQLException {
		String deleteSql = "DELETE FROM " + this.toString() + " WHERE " + formatDeleteEquals(connection, " AND ");
		final PreparedStatement statement = connection.prepareStatement(deleteSql);
		return new Remover() {
			public void remove(Map<String, Object> values) throws SQLException {
				int index = 1;
				for(Object value : ImmutableSortedMap.copyOf(values).values()) {
					statement.setObject(index, value);
					index++;
				}
				statement.addBatch();
			}
			public int apply() throws SQLException {
				int removed = 0;
				int[] updates = statement.executeBatch();
				for(int ii = 0; ii < updates.length; ii++) {
					removed += updates[ii];
				}
				return removed;	
			}
		};
	}
}
