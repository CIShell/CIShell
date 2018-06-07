/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Feb 8, 2008 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.service.conversion;


/**
 * An exception which is thrown when an error occurs in the process of data 
 * conversion
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class ConversionException extends Exception {
	private static final long serialVersionUID = 1749134893481511313L;

	public ConversionException(String message, Throwable exception) {
		super(message, exception);
	}
	
	public ConversionException(Throwable exception) {
		super(exception);
	}
	
	public ConversionException(String message) {
		super(message);
	}
}
