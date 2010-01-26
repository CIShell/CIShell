package org.cishell.utilities.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cishell.utilities.DatabaseUtilities;

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

	public void deleteRowsByColumns(List<Map<String, Object>> otherEntities, Connection connection) throws SQLException {
		if(otherEntities.size() == 0) {
			return;
		}
		List<String> columns = new ArrayList<String>(otherEntities.get(0).keySet());
		String deleteStatement = constructDeleteStatement(columns, otherEntities);
		connection.createStatement().executeUpdate(deleteStatement);
	}

	private String constructDeleteStatement(List<String> columns,
			List<Map<String, Object>> otherEntities) {
		return "DELETE FROM " + this.toString() + " WHERE " + DatabaseUtilities.createSQLInExpression(columns, otherEntities);
	}

	public void duplicateTable(Connection originalConnection,
			Connection newConnection) throws SQLException {
		this.duplicateTableStructure(originalConnection, newConnection);
		//TODO: finish
		
	}

	public void duplicateTableStructure(Connection originalConnection,
			Connection newConnection) throws SQLException {		
		Column[] columns = getColumns(originalConnection);
		String createStatement = createCreateStatement(columns);
		newConnection.createStatement().executeUpdate(createStatement);
	}

	private String createCreateStatement(Column[] columns) {
		List<String> definitions = new ArrayList<String>();
		for(int ii = 0; ii < columns.length; ii++) {
			definitions.add(columns[ii].getDefinition());
		}
		return "CREATE TABLE " + this.toString() + DatabaseUtilities.implodeAndWrap(definitions);
	}

	private Column[] getColumns(Connection connection) throws SQLException {
		ResultSet results = connection.getMetaData().getColumns(this.catalog, this.schema, this.name, null);
		List<Column> columns = new ArrayList<Column>();
		while(results.next()) {
			columns.add(new Column(results.getString(4), results.getInt(5), results.getInt(7)));
		}
		return columns.toArray(new Column[]{});
	}

	public void transferPrimaryKey(Connection originalConnection,
			Connection newConnection) {
		// TODO Auto-generated method stub
		
	}

	public void pointForeignKeys(Connection originalConnection,
			Connection newConnection) {
		// TODO Auto-generated method stub
		
	}
}
