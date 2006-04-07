/*
 * Created on Jun 2, 2004
 *
 * Shashikant Penumarthy
 */
package edu.iu.iv.core.persistence;

/**
 * All exceptions that are not java Exceptions must be PersistenceException. An example
 * for the usage of this class is the IncorrectFormatException, which extends PersistenceException.
 * This way all PersistenceExceptions raised from the persistence side can be handled by the plugins
 * that use the persistence layer, eg: LoadPlugin that does file load.
 * 
 * @see edu.iu.iv.core.persistence.IncorrectFormatException
 * @see edu.iu.iv.gui.LoadPlugin
 * 
 * @author Team IVC
 * @version 0.1
 * 
 */
public class PersistenceException extends Exception {
    private static final long serialVersionUID = 1L;

    public PersistenceException() {
		super() ;
	}
	
	public PersistenceException(String message) {
		super(message) ;
	}
}
