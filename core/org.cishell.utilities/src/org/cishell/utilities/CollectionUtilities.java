package org.cishell.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Iterator;

public class CollectionUtilities {

	/* Return only elements of the Collection which are mapped to true in the
	 * Dictionary
	 */
	public static Collection grabSelectedValues(
			Collection elements, Dictionary selectionDictionary) {
		Collection selectedElements = new ArrayList();
	
		for (Iterator elementsIt = elements.iterator(); elementsIt.hasNext();) {
			String element = (String) elementsIt.next();
			Object isSelected = selectionDictionary.get(element);
	
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
