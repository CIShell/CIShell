/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 20, 2004 at Indiana University.
 */
package edu.iu.iv.core;

/**
 * An exception thrown when an unsupported model is given to the IVC system.
 * 
 * @author Team IVC 
 */
//Created by: Bruce Herr
public class UnsupportedModelException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnsupportedModelException() {
		super();
	}
	
	/**
	 * @param message a string giving more details about the cause of the exception
	 */
	public UnsupportedModelException(String message) {
		super(message);
	}
}
