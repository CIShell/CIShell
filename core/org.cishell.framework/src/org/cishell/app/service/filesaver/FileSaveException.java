package org.cishell.app.service.filesaver;

public class FileSaveException extends Exception {
	private static final long serialVersionUID = 1L;

	public FileSaveException(String message, Throwable exception) {
		super(message, exception);
	}
	
	public FileSaveException(Throwable exception) {
		super(exception);
	}
	
	public FileSaveException(String message) {
		super(message);
	}
}
