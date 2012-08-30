package org.cishell.utilities.database;

import java.sql.SQLException;
import java.util.Map;
/**
* @deprecated see http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction+for+CIShell+Utilities
*/
@Deprecated
public interface Remover {
	public void remove(Map<String, Object> values) throws SQLException;
	public int apply() throws SQLException;
}
