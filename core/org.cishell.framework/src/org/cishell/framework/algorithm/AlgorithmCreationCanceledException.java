package org.cishell.framework.algorithm;

// TODO: Make this a regular Exception (not RuntimeException).
public class AlgorithmCreationCanceledException extends RuntimeException {
	private static final long serialVersionUID = 9017277008277139930L;

	public AlgorithmCreationCanceledException(String message, Throwable exception) {
		super(message, exception);
	}
	
	public AlgorithmCreationCanceledException(Throwable exception) {
		super(exception);
	}
	
	public AlgorithmCreationCanceledException(String message) {
		super(message);
	}

	public AlgorithmCreationCanceledException() {
		this("Algorithm canceled by user.");
	}
}
