package org.cishell.reference.gui.log;

public class Utilities {
	/**
	 * If a message begins with one of these prefixes, it should not be logged.
	 */
	public static final String[] DEFAULT_IGNORED_PREFIXES = new String[] {
			"ServiceEvent ", "BundleEvent ", "FrameworkEvent " };

	/**
	 * Determine if the {@code message} should be logged.
	 * 
	 * @param message
	 * @param ignoredPrefixes ignore any message that starts with these prefixes.
	 * @return {@code true} if the message should be logged, {@code false} if it
	 *         should not be logged.
	 */
	public static boolean logMessage(String message, String[] ignoredPrefixes) {
		if (message == null) {
			return false;
		}
		
		if (ignoredPrefixes == null) {
			return true;
		}
		
		for (String messagePrefix : ignoredPrefixes) {
			if (message.startsWith(messagePrefix)) {
				return false;
			}
		}

		return true;
	}
}
