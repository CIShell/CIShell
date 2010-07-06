package org.cishell.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class MapUtilities {
	public static<K, V> Collection<K> getValidKeysOfTypesInMap(
			Map<K, V> map, Collection<V> types, Collection<K> keysToSkip)
			throws ColumnNotFoundException {
		Collection<K> workingKeys = new ArrayList<K>();
		Collection<Map.Entry<K, V>> entrySet = map.entrySet();

		for (V type : types) {
			Collection<K> keysForType =
				SetUtilities.getKeysOfMapEntrySetWithValue(entrySet, type);
			workingKeys =
				ArrayListUtilities.unionCollections(workingKeys, keysForType, keysToSkip);
		}
		
		return workingKeys;
	}
}