package org.cishell.utilities.database;

import java.sql.SQLException;
import java.util.Map;

public interface Remover {
	public void remove(Map<String, Object> values) throws SQLException;
	public int apply() throws SQLException;
}
