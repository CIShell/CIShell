package org.cishell.testing.convertertester.core.tester2.util;

public class ConvUtil {

	/*
	 * Returns everything after the last period in the OSGi service pid.
	 */
	public static String removePackagePrefix(String pid) {
		int startIndex = pid.lastIndexOf(".") + 1;
		return pid.substring(startIndex);
	}
}
