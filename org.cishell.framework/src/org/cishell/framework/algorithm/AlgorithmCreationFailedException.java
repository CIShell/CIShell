package org.cishell.framework.algorithm;

// TODO: Make this a regular Exception (not RuntimeException).
public class AlgorithmCreationFailedException extends RuntimeException {
	private static final long serialVersionUID = 9017277008277139930L;

	public AlgorithmCreationFailedException(String message, Throwable exception) {
		super(message, exception);
	}
	
	public AlgorithmCreationFailedException(Throwable exception) {
		super(exception);
	}
	
	public AlgorithmCreationFailedException(String message) {
		super(message);
	}

	public AlgorithmCreationFailedException() {
		this("Algorithm canceled by user.");
	}
}
