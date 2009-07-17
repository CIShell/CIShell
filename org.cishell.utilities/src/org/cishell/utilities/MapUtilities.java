package org.cishell.utilities;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MapUtilities {
	public static String[] getValidKeysOfTypesInMap(Map map,
													String[] types,
													String[] keysToSkip,
													String[] keysToAdd)
			throws ColumnNotFoundException {
		ArrayList workingKeys = new ArrayList();
		Set entrySet = map.entrySet();
		
		for (String type : types) {
			ArrayList keysForType =
				SetUtilities.getKeysOfMapEntrySetWithValue(entrySet, type);
			workingKeys = ArrayListUtilities.unionArrayLists(
				workingKeys, keysForType, keysToSkip, keysToAdd);
		}
		
		return (String[])workingKeys.toArray(new String[0]);
	}
}