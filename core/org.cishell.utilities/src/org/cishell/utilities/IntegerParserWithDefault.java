package org.cishell.utilities;

public final class IntegerParserWithDefault {
	public static final Integer DEFAULT = null;

	private IntegerParserWithDefault() {
		// Utility class don't instantiate
	}
	
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