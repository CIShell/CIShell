package org.cishell.utilities;

import java.util.List;

public class StringUtilities {
	public static String implodeStringArray(String[] stringArray, String separator) {
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
}
