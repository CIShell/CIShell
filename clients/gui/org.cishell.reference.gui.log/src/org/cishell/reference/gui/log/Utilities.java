package org.cishell.reference.gui.log;

import java.util.logging.Level;

import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogService;

public class Utilities {
	/**
	 * If a message begins with one of these prefixes, it should not be logged.
	 */
	public static final String[] DEFAULT_IGNORED_PREFIXES = new String[] {
			"ServiceEvent ", "BundleEvent ", "FrameworkEvent " };

	/**
	 * Determine if the {@code message} should be logged.
	 * 
	 * @param entry
	 * @param ignoredPrefixes ignore any message that starts with these prefixes.
	 * @return {@code true} if the message should be logged, {@code false} if it
	 *         should not be logged.
	 */
	public static boolean shouldLogMessage(LogEntry entry, String[] ignoredPrefixes) {
		if (entry == null) {
			return false;
		}
		
		if (entry.getMessage() == null) {
			return false;
		}
		
		if (ignoredPrefixes == null) {
			return true;
		}
		
		for (String messagePrefix : ignoredPrefixes) {
			if (entry.getMessage().startsWith(messagePrefix)) {
				return false;
			}
		}

		return true;
	}
	
	/**
	 * Convert from an OSGI LogService level to a Java one.
	 * 
	 * @param osgiLevel
	 * @return the corresponding {@link Level} or {@link Level.Severe} if no
	 *         match is found.
	 */
	public static Level osgiLevelToJavaLevel(int osgiLevel) {
		switch (osgiLevel) {
			case LogService.LOG_DEBUG:
				return Level.FINEST;
			case LogService.LOG_ERROR:
				return Level.SEVERE;
			case LogService.LOG_INFO:
				return Level.INFO;
			case LogService.LOG_WARNING:
				return Level.WARNING;
			default:
				return Level.SEVERE;
		}
	}
}
