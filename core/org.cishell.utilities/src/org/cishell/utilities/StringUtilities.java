package org.cishell.utilities;

import java.util.ArrayList;
import java.util.List;

public class StringUtilities {
	public static String implodeStringArray(String[] stringArray,
											String separator) {
		final int stringArrayLength = stringArray.length;
		StringBuffer workingResultString = new StringBuffer();

		for (int ii = 0; ii < stringArrayLength; ii++) {
			workingResultString.append(stringArray[ii]);
			if (ii != stringArrayLength - 1) {
				workingResultString.append(separator);
			}
		}
		
		return workingResultString.toString();
	}
	
	public static String implodeList(List list, String separator) {
		StringBuffer workingResultString = new StringBuffer();
		
		final int listLength = list.size();
		
		for (int ii = 0; ii < listLength; ii++) {
			workingResultString.append(list.get(ii));
			
			boolean isLastElement = (ii == listLength - 1);
			if (!isLastElement) {
				workingResultString.append(separator);
			}
		}
		
		return workingResultString.toString();
	}
	
	public static String[] filterStringsByPattern(String[] stringsToFilter,
												  String pattern) {
		ArrayList filteredStrings = new ArrayList();
		
		for (int ii = 0; ii < stringsToFilter.length; ii++) {
			if (!stringsToFilter[ii].matches(pattern)) {
				filteredStrings.add(stringsToFilter[ii]);
			}
		}
		
		return (String[])filteredStrings.toArray(new String[0]);
	}
	
	public static String[] filterEmptyStrings(String[] stringsToFilter) {
		// TODO: This maybe should use filterStringsByPattern?
		ArrayList filteredStrings = new ArrayList();
		
		for (int ii = 0; ii < stringsToFilter.length; ii++) {
			if (!"".equals(stringsToFilter[ii])) {
				filteredStrings.add(stringsToFilter[ii]);
			}
		}
		
		return (String[])filteredStrings.toArray(new String[0]);
	}
	
	/*
	 * This method is really meant to simplify working with Prefuse tables.
	 * Prefuse table columns are typed.  If a column contains a null cell,
	 *  Prefuse types that column as an array type, and it then represents
	 *  null values with arrays of length 0.
	 * To handle this, this method returns:
	 *  null if the object is actually null or array of length 0;
	 *  just the first element of the array; or
	 *  the result of the object's toString method.
	 */
	// TODO: Rename to interpretAsString.
	// TODO: Move these things to TableUtilities.
	// TODO: Handle all cases, including all primitive array types and
	//  perhaps primitive box types (i.e. Integer).
	public static String interpretObjectAsString(Object object) {
		if (object == null) {
			return null;
		} else if (object instanceof String[]) {
			String[] objectAsStringArray = (String[]) object;
			
			if (objectAsStringArray.length == 0) {
				return null;
			} else {
				return objectAsStringArray[0];
			}
		} else {
			return object.toString();
		}
	}
	
	// TODO Think about instead using a Pattern, "\s*".  Don't have to though.
	public static boolean isEmptyOrWhiteSpace(String test) {
		String trimmed = test.trim();
		
		return (trimmed.length() == 0);
	}
	
	public static int countOccurrencesOfChar(
			CharSequence characters, char target) {
		int count = 0;
		
		for (int ii = 0; ii < characters.length(); ii++) {
			if (characters.charAt(ii) == target) {
				count++;
			}
		}
		
		return count;
	}
	
	public static String multiply(String target, int count) {
		if (count < 1) {
			return "";
		} else {
			StringBuffer stringInProgress = new StringBuffer();
			
			for (int ii = 0; ii < count; ii ++) {
				stringInProgress.append(target);
			}
			
			return stringInProgress.toString();
		}
	}
}
