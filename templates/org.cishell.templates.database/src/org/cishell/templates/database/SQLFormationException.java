package org.cishell.templates.database;

public class SQLFormationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	//Constructors from Exception superclass
	
	public SQLFormationException() {
		super();
	}

	public SQLFormationException(String arg0) {
		super(arg0);
	}

	public SQLFormationException(Throwable arg0) {
		super(arg0);
	}

	public SQLFormationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}