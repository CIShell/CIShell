package org.cishell.utilities;

public class IntegerParserWithDefault {
	public static final Integer DEFAULT = null;

	public static Integer parse(String target) {
		try {
			return Integer.parseInt(target);
		} catch (Exception e) {
			return DEFAULT;
		}
	}
}