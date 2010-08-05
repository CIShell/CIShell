package org.cishell.utility.swt.model;

public class ModelFieldException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public ModelFieldException() {
		super();
	}

	public ModelFieldException(String arg0) {
		super(arg0);
	}

	public ModelFieldException(Throwable arg0) {
		super(arg0);
	}

	public ModelFieldException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}