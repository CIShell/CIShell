package org.cishell.utilities;

/**
 * Customized exception handling for Null value support.  
 * @author kongch
 *
 */
/**
 * @deprecated see
 *             http://wiki.cns.iu.edu/display/CISHELL/2012/08/30/Future+Direction
 *             +for+CIShell+Utilities
 */
@Deprecated
public class NullValueSupportException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NullValueSupportException(String message, Exception e) {
		super(message, e);
	}
}
