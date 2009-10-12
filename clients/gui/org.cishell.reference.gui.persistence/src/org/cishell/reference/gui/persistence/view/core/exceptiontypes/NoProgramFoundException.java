package org.cishell.reference.gui.persistence.view.core.exceptiontypes;

public class NoProgramFoundException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NoProgramFoundException() {
		super();
	}

	public NoProgramFoundException(String arg0) {
		super(arg0);
	}

	public NoProgramFoundException(Throwable arg0) {
		super(arg0);
	}

	public NoProgramFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
