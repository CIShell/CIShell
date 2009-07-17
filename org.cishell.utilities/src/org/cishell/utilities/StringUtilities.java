package org.cishell.utilities;

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
}
