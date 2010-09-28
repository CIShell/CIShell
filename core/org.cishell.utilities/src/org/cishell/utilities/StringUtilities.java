package org.cishell.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringUtilities {
	public static Pattern NON_ALPHA_NUMERIC_CHARACTER_ESCAPE = Pattern.compile("([^a-zA-z0-9])");

	// TODO: Make this wrap implodeItems.
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

	/* TODO: This is a wrapper for implodeItems.  All new/updated code should refer to implodeItems
	 *  from now on.
	 */
	@SuppressWarnings("unchecked")	// Raw List.
	public static String implodeList(List list, String separator) {
		return implodeItems(list, separator);
	}

	public static<T> String implodeItems(Collection<T> items, String separator) {
		StringBuffer workingResultString = new StringBuffer();

		for (Iterator<T> it = items.iterator(); it.hasNext(); ) {
//		for (int ii = 0; ii < listLength; ii++) {
//			workingResultString.append(list.get(ii));
			workingResultString.append(it.next());
			
//			boolean isLastElement = (ii == listLength - 1);
			boolean isLastElement = !it.hasNext();
			if (!isLastElement) {
				workingResultString.append(separator);
			}
		}
		
		return workingResultString.toString();
	}
	
	public static String[] filterStringsByPattern(String[] stringsToFilter, String pattern) {
		ArrayList<String> filteredStrings = new ArrayList<String>();
		
		for (int ii = 0; ii < stringsToFilter.length; ii++) {
			if (!stringsToFilter[ii].matches(pattern)) {
				filteredStrings.add(stringsToFilter[ii]);
			}
		}
		
		return (String[])filteredStrings.toArray(new String[0]);
	}
	
	public static String[] filterEmptyStrings(String[] stringsToFilter) {
		// TODO: This maybe should use filterStringsByPattern?
		ArrayList<String> filteredStrings = new ArrayList<String>();
		
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
	public static boolean isEmptyOrWhitespace(String string) {
		String trimmed = string.trim();
		
		return (trimmed.length() == 0);
	}

	public static boolean allAreEmptyOrWhitespace(String... strings) {
		for (String string : strings) {
			if (!isEmptyOrWhitespace(string)) {
				return false;
			}
		}

		return true;
	}

	public static boolean allAreNeitherEmptyNorWhitespace(String... strings) {
		for (String string : strings) {
			if (isEmptyOrWhitespace(string)) {
				return false;
			}
		}

		return true;
	}

	public static boolean isNull_Empty_OrWhitespace(String string) {
		if (string == null) {
			return true;
		}

		return isEmptyOrWhitespace(string);
	}

	public static boolean allAreNull_Empty_OrWhitespace(String... strings) {
		for (String string : strings) {
			if (!isNull_Empty_OrWhitespace(string)) {
				return false;
			}
		}

		return true;
	}

	public static boolean allAreNeitherNullNorEmptyNorWhitespace(String... strings) {
		for (String string : strings) {
			if (isNull_Empty_OrWhitespace(string)) {
				return false;
			}
		}

		return true;
	}
	
	public static int countOccurrencesOfChar(CharSequence characters, char target) {
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

	public static String multiplyWithSeparator(String target, String separator, int count) {
		String multipliedWithExtraSeparator = multiply(target + separator, count);

		return multipliedWithExtraSeparator.substring(
			0, multipliedWithExtraSeparator.length() - separator.length());
	}

	public static String emptyStringIfNull(Object object) {
		if (object == null) {
			return "";
		} else {
			return object.toString();
		}
	}

	public static String simpleClean(String string) {
		String guaranteedToNotBeNull = emptyStringIfNull(string);

		return guaranteedToNotBeNull.trim();
	}

	public static final String[] simpleCleanStrings(String[] strings) {
		List<String> cleanedStrings = new ArrayList<String>();

		for (int ii = 0; ii < strings.length; ii ++) {
			cleanedStrings.add(StringUtilities.simpleClean(strings[ii]));
		}

		return (String[])cleanedStrings.toArray(new String[0]);
	}

	public static String trimIfNotNull(String string) {
		if (string == null) {
			return null;
		}

		return string.trim();
	}

	public static String toSentenceCase(String word) {
		String cleanedWord = simpleClean(word);

		if (cleanedWord.length() == 0) {
			return "";
		} else {
			return
				Character.toUpperCase(cleanedWord.charAt(0)) +
				cleanedWord.substring(1).toLowerCase();
		}
	}

	public static int prefixIndex(String target, String[] prefixes) {
		/*
		 * Look for the prefixes in reverse order (so a longer one will win out over a shorter one
		 *  if they both have a beginning in common).
		 */
		for (int ii = (prefixes.length - 1); ii >= 0; ii--) {
			if (target.startsWith(prefixes[ii])) {
				return ii;
			}
		}

		return -1;
	}

	/* TODO Perhaps make a "hasContent" method in here and apply that terminology throughout. */
	public static boolean areValidAndEqual(String string1, String string2) {
		return (
			!isNull_Empty_OrWhitespace(string1) &&
			!isNull_Empty_OrWhitespace(string2) &&
			(string1.equals(string2)));
	}

	public static boolean bothAreEqualOrNull(String string1, String string2) {
		if (string1 != null) {
			return string1.equals(string2);
		} else {
			return (string2 == null);
		}
	}

	public static boolean areValidAndEqualIgnoreCase(String string1, String string2) {
		return (
			!isNull_Empty_OrWhitespace(string1) &&
			!isNull_Empty_OrWhitespace(string2) &&
			string1.equalsIgnoreCase(string2));
	}

	// TODO: New Name.
	public static String simpleMerge(String string1, String string2) {
		if (!isNull_Empty_OrWhitespace(string1)) {
			if (!isNull_Empty_OrWhitespace(string2)) {
				if (string1.length() >= string2.length()) {
					return string1;
				} else {
					return string2;
				}
			} else {
				return string1;
			}
		}
		else if (!isNull_Empty_OrWhitespace(string2)) {
			return string2;
		}

		return string1;
	}
	
	//TODO: Make this not exist (a check for 'Null Empty or Whitespace' can stay. Use ! for negated cases)
	public static Object alternativeIfNotNull_Empty_OrWhitespace(
			String string, Object alternative) {
		if (!isNull_Empty_OrWhitespace(string)) {
			return string;
		} else {
			return alternative;
		}
	}

	public static Object alternativeIfNotNull_Empty_OrWhitespace_IgnoreCase(
			String string, Object alternative) {
		if (!isNull_Empty_OrWhitespace(string)) {
			return string.toLowerCase();
		} else {
			return alternative;
		}
	}

	public static String getNthToken(
			String originalString,
			String separator,
			int index,
			boolean trim,
			boolean escapeForRegularExpression) {
		return getAllTokens(originalString, separator, trim, escapeForRegularExpression)[index];
	}

	public static String[] getAllTokens(
			String originalString,
			String separator,
			boolean trim,
			boolean escapeForRegularExpression) {
		if (escapeForRegularExpression) {
			separator = escapeForRegularExpression(separator);
		}

		String[] tokens = originalString.split(separator);

		if (trim) {
			String[] trimmedTokens = new String[tokens.length];

			for (int ii = 0; ii < tokens.length; ii++) {
				trimmedTokens[ii] = tokens[ii].trim();
			}

			return trimmedTokens;
		} else {
			return tokens;
		}
	}

	// TODO: Use StreamTokenizer?
	public static String[] tokenizeByWhitespace(String originalString) {
		StringTokenizer tokenizer = new StringTokenizer(originalString);
		int tokenCount = tokenizer.countTokens();
		String[] tokens = new String[tokenCount];

		for (int ii = 0; ii < tokenCount; ii++) {
			tokens[ii] = tokenizer.nextToken();
		}

		return tokens;
	}

	public static String stripSurroundingQuotes(String quoted) {
		if (quoted.startsWith("\"")) {
			if (quoted.endsWith("\"")) {
				return quoted.substring(1, quoted.length() - 1);
			} else {
				return quoted.substring(1);
			}
		} else {
			return quoted;
		}
	}

	public static String escapeForRegularExpression(String original) {
		return NON_ALPHA_NUMERIC_CHARACTER_ESCAPE.matcher(original).replaceAll("\\\\$1"); 
	}

	// TODO
//	public static String escape(String unescaped) {
//		return unescaped.replaceAll("\"", "\\\""
//	}

	public static String getStackTraceAsString(Throwable e) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		e.printStackTrace(printWriter);

		return writer.toString();
	}
}
