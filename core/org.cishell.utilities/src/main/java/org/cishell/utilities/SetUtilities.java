package org.cishell.utilities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class SetUtilities {
	public static<K, V> Collection<K> getKeysOfMapEntrySetWithValue(
			Collection<Map.Entry<K, V>> mapEntries, V value) {
		Collection<K> keysOfMapEntrySetWithValue = new HashSet<K>();
		Iterator<Map.Entry<K, V>> mapEntrySetIterator = mapEntries.iterator();

		while (mapEntrySetIterator.hasNext()) {
			Map.Entry<K, V> entry = mapEntrySetIterator.next();
			
			if (entry.getValue().equals(value)) {
				keysOfMapEntrySetWithValue.add(entry.getKey());
			}
		}
		
		return keysOfMapEntrySetWithValue;
	}
}