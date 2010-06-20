package org.cishell.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArrayListUtilities {
	public static ArrayList unionArrayLists(ArrayList arrayList1,
											ArrayList arrayList2,
											String[] keysToSkip,
											String[] keysToAdd) {
		ArrayList union = new ArrayList();
		
		for (int ii = 0; ii < arrayList1.size(); ii++) {
			Object element = arrayList1.get(ii);
			
			if (!union.contains(element) &&
					Arrays.binarySearch(keysToSkip, element) < 0) {
				union.add(element);
			}
		}
		
		for (int ii = 0; ii < arrayList2.size(); ii++) {
			Object element = arrayList2.get(ii);
			
			if (!union.contains(element)) {
				union.add(element);
			}
		}
		
		for (int ii = 0; ii < keysToAdd.length; ii++) {
			String keyToAdd = keysToAdd[ii];
			if (!union.contains(keyToAdd)) {
				union.add(keyToAdd);
			}
		}
		
		return union;
	}
	
	/* Implodes list to a String with the String.valueOf the elements separated
	 * by separator and where all elements except the first prefixSize and
	 * final suffixSize are represented only by ellipsis.
	 * 
	 * Examples:
	 * - ({1, 2, 3, 4, 5, 6}, 2, 1, ", ", "...") returns "1, 2, ..., 5"
	 * - ({1, 2, 3, 4, 5, 6}, 7, 7, ", ", "...") returns "1, 2, 3"
	 * - ({1, 2, 3, 4, 5, 6}, 0, 2, ", ", "...") returns "1, 2, ..., 4, 5"
	 * - ({1, 2, 3, 4, 5, 6}, 2, 0, ", ", "...") returns "1, 2, ..."
	 * - ({1, 2, 3, 4, 5, 6}, -1, -1, ", ", "...") returns "1, 2, ..."
	 * - The empty list always returns "No elements"
	 * 
	 * If requestedPrefixSize (resp. requestedSuffixSize) is less than
	 * prefixSizeMinimum (resp. suffixSizeMinimum), we instead use the minimum.
	 */	
	public static String makePreview(
			List list,
			int requestedPrefixSize,
			int requestedSuffixSize,
			String separator,
			String ellipsis) {
		if (list.isEmpty()) {
			return "No elements";
		} else {
			// Adjust the requested sizes to reasonable numbers.
			final int prefixSizeMinimum = 2;			
			requestedPrefixSize =
				Math.max(prefixSizeMinimum, requestedPrefixSize);
			final int suffixSizeMinimum = 0;
			requestedSuffixSize =
				Math.max(suffixSizeMinimum, requestedSuffixSize);
			
			// Check whether an ellipsis is necessary.
			boolean ellipsisNecessary =
				(list.size() > requestedPrefixSize + requestedSuffixSize);
			if (ellipsisNecessary) {
				// Implode the prefix, ellipsis, and suffix.
				List affixes = new ArrayList();
				
				List prefixList = list.subList(0, requestedPrefixSize);
				if (!prefixList.isEmpty()) {
					affixes.add(StringUtilities.implodeItems(prefixList, separator));
				}
				
				affixes.add(ellipsis);
				
				List suffixList =
					list.subList(
							list.size() - requestedSuffixSize,
							list.size());
				if (!suffixList.isEmpty()) {
					affixes.add(
							StringUtilities.implodeItems(suffixList, separator));
				}
	
				return StringUtilities.implodeItems(affixes, separator);
			} else {
				// Just implode the list.
				return StringUtilities.implodeItems(list, separator);
			}
		}
	}
}