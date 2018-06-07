package org.cishell.utilities;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class FileCopyingException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FileCopyingException() {
		super();
	}

	public FileCopyingException(String arg0) {
		super(arg0);
	}

	public FileCopyingException(Throwable arg0) {
		super(arg0);
	}

	public FileCopyingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}