package org.cishell.utilities;

/**
 * Customized exception handling for Null value support.  
 * @author kongch
 *
 */
public class NullValueSupportException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NullValueSupportException(String message, Exception e) {
		super(message, e);
	}
}
