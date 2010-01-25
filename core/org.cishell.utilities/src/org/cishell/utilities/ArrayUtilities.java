package org.cishell.utilities;

import java.util.Iterator;
import java.util.List;

public class ArrayUtilities {
	public static int indexOf(Object[] array, Object target) {
		for (int ii = 0; ii < array.length; ii++) {
			if (array[ii].equals(target)) {
				return ii;
			}
		}
		
		return -1;
	}
	
	public static void swapFirstMatchToFront(Object[] array, List targets) {
		for (Iterator targetsIt = targets.iterator(); targetsIt.hasNext();) {
			Object target = targetsIt.next();
			int index = ArrayUtilities.indexOf(array, target);
			
			if ( index != -1 ) {
				swap(array, 0, index);
				return;
			}
			
		}
	}
	
	public static void swap(Object[] array, int i, int j) {
		Object temp = array[i];
		array[i] = array[j];
		array[j] = temp;
	}
	
	public static String[] clone(String[] array) {
		String[] clone = new String[array.length];
		
		for (int ii = 0; ii < array.length; ii++) {
			clone[ii] = array[ii];
		}
		
		return clone;
	}

	// TODO: Find a better place to put this?
	public static <T> boolean allAreNull(T... objects) {
		for (T object : objects) {
			if (object != null) {
				return false;
			}
		}

		return true;
	}

	// TODO: Find a better place to put this?
	public static <T> boolean allAreNotNull(T... objects) {
		for (T object : objects) {
			if (object == null) {
				return false;
			}
		}

		return true;
	}
}
