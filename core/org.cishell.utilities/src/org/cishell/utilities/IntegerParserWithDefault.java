package org.cishell.utilities;

public class IntegerParserWithDefault {
	public static final Integer DEFAULT = null;

	/**
	 * Please see {@link NumberUtilities} for alternatives, specifically
	 * {@link NumberUtilities#interpretObjectAsInteger(Object)}.
	 * 
	 * @param target
	 * @return
	 */
	@Deprecated
	public static Integer parse(String target) {
		try {
			return Integer.parseInt(target);
		} catch (Exception e) {
			return DEFAULT;
		}
	}
}