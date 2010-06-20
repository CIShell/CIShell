package org.cishell.utilities.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.cishell.utilities.StringUtilities;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public final class ForeignKey {

	final public DatabaseTable localTable;
	final public DatabaseTable otherTable;
	final public Set<ColumnPair> pairs;

	public ForeignKey(DatabaseTable localTable, DatabaseTable otherTable, Set<ColumnPair> pairs) {
		this.localTable = localTable;
		this.otherTable = otherTable;
		this.pairs = ImmutableSet.copyOf(pairs);
	}

	

	private Map<String, Object> translateToForeignNames(
			Map<String, Object> from) {
		//sorting is necessary to ensure keys and values match up
		SortedMap<String, Object> to = Maps.newTreeMap();
		for(ColumnPair pair : pairs) {
			to.put(pair.foreign, from.get(pair.local));
		}		
		
		return to;
	}

	private List<String> getForeignColumnNames() {
		List<String> foreignColumns = new ArrayList<String>();
		for(ColumnPair pair : pairs) {
			foreignColumns.add(pair.foreign);
		}
		return foreignColumns;
	}

	

	private String formatUpdateEquals(String separator) {
		//sorting is necessary to ensure keys and values match up
		SortedSet<String> updateStatements = Sets.newTreeSet();
		for(ColumnPair pair : pairs) {
			String foreignColumn = pair.foreign;
			updateStatements.add(foreignColumn + " = ?");
		}
		return StringUtilities.implodeItems(Lists.newArrayList(updateStatements), separator);
	}

	public Repointer constructRepointer(Connection connection) throws SQLException {
		final PreparedStatement statement = connection.prepareStatement("UPDATE " + otherTable.toString() + " SET " + formatUpdateEquals(", ") + " WHERE " + formatUpdateEquals(" AND "));
		return new Repointer() {
			
			public void repoint(Map<String, Object> primary, Map<String, Object> secondary) throws SQLException {
				int index = 1;
				for(Object primaryValue : primary.values()) {
					statement.setObject(index, primaryValue);
					index++;
				}
				for(Object secondaryValue : secondary.values()) {
					statement.setObject(index, secondaryValue);
					index++;
				}
				statement.addBatch();
			}

			public void apply() throws SQLException {
				statement.executeBatch();
			}
		};
	}
	
	

}
