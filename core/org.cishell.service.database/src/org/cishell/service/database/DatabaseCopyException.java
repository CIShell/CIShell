/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jan 13, 2009 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.service.database;

public class DatabaseCopyException extends Exception {
	
	private static final long serialVersionUID = 1L;

	//Constructors from Exception superclass
	
	public DatabaseCopyException() {
		super();
	}

	public DatabaseCopyException(String arg0) {
		super(arg0);
	}

	public DatabaseCopyException(Throwable arg0) {
		super(arg0);
	}

	public DatabaseCopyException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
