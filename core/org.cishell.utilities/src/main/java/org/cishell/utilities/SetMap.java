package org.cishell.utilities;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * Don't use this! Instead use a SetMultimap from Google Guava!
 */
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class SetMap {	
	private Map map = new Hashtable();
	
	public void put(Object key, Object value) {
		Set valueSet = (Set) this.map.get(key);
		if (valueSet == null) {
			valueSet = new HashSet();
		}
		
		valueSet.add(value);
		
		this.map.put(key, valueSet);
	}
	
	public Set keySet() {
		return this.map.keySet();
	}
	
	public Set get(Object key) {
		return (Set) this.map.get(key);
	}
}

