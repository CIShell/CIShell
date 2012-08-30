package org.cishell.utilities;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class ZipIOException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ZipIOException() {
		super();
	}

	public ZipIOException(String arg0) {
		super(arg0);
	}

	public ZipIOException(Throwable arg0) {
		super(arg0);
	}

	public ZipIOException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}