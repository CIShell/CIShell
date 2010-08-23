package org.cishell.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;

public class CollectionUtilities {
	/* Return only elements of the Collection which are mapped to true in the
	 * Dictionary
	 */
	public static<K, V> Collection<K> grabSelectedValues(
			Collection<K> elements, Dictionary<K, V> selectionDictionary) {
		Collection<K> selectedElements = new ArrayList<K>();
	
		for (Iterator<K> elementsIt = elements.iterator(); elementsIt.hasNext();) {
			K element = elementsIt.next();
			V isSelected = selectionDictionary.get(element);
	
			if ((isSelected != null) && (isSelected instanceof Boolean)) {
				if (((Boolean) isSelected).booleanValue()) {
					selectedElements.add(element);
				}
			}
		}
	
		return selectedElements;
	}

	@SuppressWarnings("unchecked")
	public static<T> T get(Collection<T> values, int index) {
		return (T) values.toArray()[index];
	}
}
