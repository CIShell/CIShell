package org.cishell.utilities;

import java.util.Iterator;
import java.util.List;
/**
 * @deprecated see
 *             http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities
 */
@Deprecated
public final class ArrayUtilities {
	
	private ArrayUtilities() {
	}
	
	public static int indexOf(Object[] array, Object target) {
		for (int ii = 0; ii < array.length; ii++) {
			if (array[ii].equals(target)) {
				return ii;
			}
		}
		
		return -1;
	}
	
	public static void swapFirstMatchToFront(Object[] array, List<?> targets) {
		for (Iterator<?> targetsIt = targets.iterator(); targetsIt.hasNext();) {
			Object target = targetsIt.next();
			int index = ArrayUtilities.indexOf(array, target);
			
			if (index != -1) {
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
	
	/**
	 * This method will return a shadow copy of the given array with the desired size.
	 * If the given disiredSize exceeds the original array size, the remaining items
	 * will be filled with empty string.
	 * @param array - The original array to be copied
	 * @param desiredSize - The desired size of the new array
	 * @return The new copy of the original array with the desired size
	 */
	public static String[] copyOf(String[] array, int desiredSize) {
		String[] newArray = new String[desiredSize];
		int copiedSize = Math.min(array.length, desiredSize);
		
		/* Copy items from the original array to new array */
		for (int i = 0; i < copiedSize; i++) {
			newArray[i] = array[i];
		}
		
		/* Fill the extra items will empty string if there are */
		for (int i = copiedSize; i < desiredSize; i++) {
			newArray[i] = "";
		}
		
		return newArray;
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
