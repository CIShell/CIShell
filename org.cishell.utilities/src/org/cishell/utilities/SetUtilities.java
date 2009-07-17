package org.cishell.utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SetUtilities {
	public static ArrayList getKeysOfMapEntrySetWithValue(Set mapEntrySet,
														  Object value) {
		ArrayList keysOfMapEntrySetWithValue = new ArrayList();
		Iterator mapEntrySetIterator = mapEntrySet.iterator();
		
		while (mapEntrySetIterator.hasNext()) {
			Map.Entry entry = (Map.Entry)mapEntrySetIterator.next();
			
			if (entry.getValue().equals(value)) {
				keysOfMapEntrySetWithValue.add(entry.getKey());
			}
		}
		
		return keysOfMapEntrySetWithValue;
	}
}