/*
 * Created on May 21, 2004
 *
 */
package edu.iu.iv.core.persistence;

/**
 * An IncorrectFormatException is thrown by a persister whenever the data that
 * it expects is different from what it actually finds in the resource descriptor.
 * Currently this Exception has been created here with the intention of being thrown
 * whenever a file is found to be of a format different than expected, implying that this
 * is primarily applicable for files on disk. 
 * 
 * @author Team IVC
 * @version 0.1
 * 
 */
// Shashikant Penumarthy
public class IncorrectFormatException extends PersistenceException {
    private static final long serialVersionUID = 1L;

    /**
	 * Creates a new exception.
	 */
	public IncorrectFormatException() {
		super() ;
	}
	
	/**
	 * Creates a new exception with the specified detail message. 
	 * 
	 * @param message The detail message for this exception.
	 */
	public IncorrectFormatException(String message) {
		super(message) ;
	}
}
