package org.cishell.utilities.logging;

import java.io.PrintStream;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import com.google.common.base.Preconditions;

/**
 * Wrapper for a printstream so that it will behave like OSGi's
 * {@link LogService}
 * 
 * @author David M. Coe - david.coe+CNS@gmail.com
 * 
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class PrintStreamLogger implements LogService {
	final private PrintStream out;

	/**
	 * Create a logger that will print to a default {@link PrintStream}.
	 */
	public PrintStreamLogger() {
		this.out = System.out;
	}

	/**
	 * Create a logger that will log to a specified {@link PrintStream}.
	 * 
	 * @param out
	 *            The PrintStream to log messages to; must not be
	 *            <code>null</code>
	 */
	public PrintStreamLogger(PrintStream out) {
		Preconditions.checkNotNull(out);
		this.out = out;
	}

	/**
	 * Log a message
	 * 
	 * @param osgiLevel
	 *            The {@link LogService} log level. See
	 *            {@link LogService#log(int, String)}
	 * @param message
	 *            The message to log
	 */
	@Override
	public void log(int osgiLevel, String message) {

		this.out.println(Utilities.osgiLevelToJavaLevel(osgiLevel)
				.getLocalizedName() + ": " + message);

	}

	@Override
	public void log(int osgiLevel, String message, Throwable throwable) {
		String throwableMessage = getThrowableMessage(throwable);
		this.log(osgiLevel, throwableMessage + message);
	}

	@Override
	public void log(@SuppressWarnings("rawtypes") ServiceReference sr,
			int osgiLevel, String message) {
		String serviceReferenceMessage = getServiceReferenceMessage(sr);
		this.log(osgiLevel, serviceReferenceMessage + message);

	}

	@Override
	public void log(@SuppressWarnings("rawtypes") ServiceReference sr,
			int osgiLevel, String message, Throwable throwable) {
		String serviceReferenceMessage = getServiceReferenceMessage(sr);
		String throwableMessage = getThrowableMessage(throwable);
		this.log(osgiLevel, serviceReferenceMessage + throwableMessage
				+ message);
	}

	private static String getThrowableMessage(Throwable throwable) {
		String throwableMessage = throwable + ": ";
		return throwableMessage;
	}

	private static String getServiceReferenceMessage(ServiceReference<?> sr) {
		String serviceReferenceMessage = sr + ": ";
		return serviceReferenceMessage;
	}
}