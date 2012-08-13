package org.cishell.utilities.logging;

import java.util.logging.Level;

import org.osgi.service.log.LogService;


/**
 * A collection of log utilities. TODO has an exact copy of
 * {@link #osgiLevelToJavaLevel(int)} from
 * org.cishell.reference.gui.log.Utilities. see if these could be reintegrated.
 * 
 * @author David M. Coe (david.coe@gmail.com)
 * 
 */
public class Utilities {

	// Suppress default constructor for noninstantiability
	private Utilities() {
		throw new AssertionError();
	}

	/**
	 * Convert from an OSGI LogService level to a Java one.
	 * 
	 * @param osgiLevel
	 *            The level from OSGi's {@link LogService}.
	 * @return the corresponding {@link Level} or {@link Level#SEVERE} if no
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
