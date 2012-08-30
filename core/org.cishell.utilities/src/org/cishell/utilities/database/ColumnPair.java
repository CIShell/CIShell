package org.cishell.utilities.database;
/**
* @deprecated see http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction+for+CIShell+Utilities
*/
@Deprecated
public final class ColumnPair {
	public final String local;
	public final String foreign;
	
	public ColumnPair(String local, String foreign) {
		this.local = local;
		this.foreign = foreign;
	}
	
	public boolean equals(Object other) {
		if(!(other instanceof ColumnPair)) {
			return false;
		}
		ColumnPair o = (ColumnPair) other;
		return o.local.equals(this.local) && o.foreign.equals(this.foreign);
	}
	
	public int hashCode() {
		return local.hashCode() * 31 + foreign.hashCode();
	}

}
