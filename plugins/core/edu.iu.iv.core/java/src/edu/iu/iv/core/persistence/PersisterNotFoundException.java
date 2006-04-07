/*
 * Created on May 21, 2004
 * Shashikant Penumarthy
 */
package edu.iu.iv.core.persistence;

/**
 * 
 * Signifies the exception that a Persister was not found.
 *
 * @see edu.iu.iv.core.persistence.PersistenceRegistry#findPersister(Object, ResourceDescriptor)
 *
 * @author Shashikant
 * @version 0.1
 **/
public class PersisterNotFoundException extends PersistenceException {
    private static final long serialVersionUID = 1L;

    /**
	 * Constructs an exception. The detail message is set to <code>null</code>.
	 */
	public PersisterNotFoundException() {
		super();
	}

	/**
	 * Constructs a new exception with the specified detail message.
	 * 
	 * @param message The detail message.
	 */
	public PersisterNotFoundException(String message) {
		super(message);
	}
}
