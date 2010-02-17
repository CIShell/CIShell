package org.cishell.framework.algorithm;

public class AlgorithmCanceledException extends Exception {
	private static final long serialVersionUID = 9017277008277139930L;

	public AlgorithmCanceledException(String message, Throwable exception) {
		super(message, exception);
	}
	
	public AlgorithmCanceledException(Throwable exception) {
		super(exception);
	}
	
	public AlgorithmCanceledException(String message) {
		super(message);
	}

	public AlgorithmCanceledException() {
		this("Algorithm canceled by user.");
	}
}
