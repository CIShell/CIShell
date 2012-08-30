package org.cishell.utilities.database;
/**
* @deprecated see {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction+for+CIShell+Utilities}
*/
@Deprecated
public final class ForeignKeyNameWithTable {
	public final String name;
	public final DatabaseTable table;
	
	public ForeignKeyNameWithTable(String name, DatabaseTable table) {
		this.name = name;
		this.table = table;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof ForeignKeyNameWithTable)) {
			return false;
		}
		ForeignKeyNameWithTable o = (ForeignKeyNameWithTable) other;
		return o.name.equals(this.name) && o.table.equals(this.table);
	}
	
	public int hashCode() {
		return name.hashCode() * 31 + table.hashCode();
	}
}
