package org.cishell.utility.datastructure.datamodel.exception;

public class ModelValidationException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public ModelValidationException() {
		super();
	}

	public ModelValidationException(String arg0) {
		super(arg0);
	}

	public ModelValidationException(Throwable arg0) {
		super(arg0);
	}

	public ModelValidationException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}