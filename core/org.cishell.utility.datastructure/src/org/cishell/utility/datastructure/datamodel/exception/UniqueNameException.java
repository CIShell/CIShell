package org.cishell.utility.datastructure.datamodel.exception;

public class UniqueNameException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UniqueNameException() {
		super();
	}

	public UniqueNameException(String arg0) {
		super(arg0);
	}

	public UniqueNameException(Throwable arg0) {
		super(arg0);
	}

	public UniqueNameException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}