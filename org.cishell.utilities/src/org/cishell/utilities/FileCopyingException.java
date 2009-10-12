package org.cishell.utilities;

public class FileCopyingException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public FileCopyingException() {
		super();
	}

	public FileCopyingException(String arg0) {
		super(arg0);
	}

	public FileCopyingException(Throwable arg0) {
		super(arg0);
	}

	public FileCopyingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}