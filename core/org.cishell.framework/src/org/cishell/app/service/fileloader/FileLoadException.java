package org.cishell.app.service.fileloader;

public class FileLoadException extends Exception {
	private static final long serialVersionUID = 1L;

	public FileLoadException(String message, Throwable exception) {
		super(message, exception);
	}
	
	public FileLoadException(Throwable exception) {
		super(exception);
	}
	
	public FileLoadException(String message) {
		super(message);
	}
}
