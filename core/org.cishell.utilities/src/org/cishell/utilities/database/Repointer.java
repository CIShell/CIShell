package org.cishell.utilities.database;

import java.sql.SQLException;
import java.util.Map;

public interface Repointer {
	public void repoint(Map<String, Object> primary, Map<String, Object> secondary) throws SQLException;

	public void apply() throws SQLException;
}
