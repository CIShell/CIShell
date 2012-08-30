package org.cishell.utilities;
/**
 * @deprecated see
 *             http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities
 */
@Deprecated
public class ColumnNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ColumnNotFoundException() {
		super();
	}

	public ColumnNotFoundException(String arg0) {
		super(arg0);
	}

	public ColumnNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public ColumnNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}