package org.cishell.utilities;
/**
 * @deprecated see
 *            {@link url http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities}
 */
@Deprecated
public class AlgorithmNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public AlgorithmNotFoundException() {
		super();
	}

	public AlgorithmNotFoundException(String arg0) {
		super(arg0);
	}

	public AlgorithmNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public AlgorithmNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}