package org.cishell.reference.gui.persistence.view.core.exceptiontypes;

public class UserCanceledDataViewSelectionException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public UserCanceledDataViewSelectionException() {
		super();
	}

	public UserCanceledDataViewSelectionException(String arg0) {
		super(arg0);
	}

	public UserCanceledDataViewSelectionException(Throwable arg0) {
		super(arg0);
	}

	public UserCanceledDataViewSelectionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}