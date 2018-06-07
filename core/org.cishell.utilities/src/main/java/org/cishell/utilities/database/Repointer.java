package org.cishell.utilities.database;

import java.sql.SQLException;
import java.util.Map;
/**
* @deprecated see {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction+for+CIShell+Utilities}
*/
@Deprecated
public interface Repointer {
	public void repoint(Map<String, Object> primary, Map<String, Object> secondary) throws SQLException;

	public void apply() throws SQLException;
}
