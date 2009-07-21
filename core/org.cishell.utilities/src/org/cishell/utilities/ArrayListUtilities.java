package org.cishell.utilities;

import java.util.ArrayList;
import java.util.Arrays;

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
}