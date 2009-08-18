package org.cishell.utilities;

public class ArrayUtilities {
	public static int indexOf(Object[] array, Object target) {
		for (int ii = 0; ii < array.length; ii++) {
			if (target.equals(array[ii])) {
				return ii;
			}
		}
		
		return -1;
	}
}
