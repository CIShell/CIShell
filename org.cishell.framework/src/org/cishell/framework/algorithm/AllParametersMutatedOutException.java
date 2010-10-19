package org.cishell.framework.algorithm;

public class AllParametersMutatedOutException extends RuntimeException {
	private static final long serialVersionUID = 9017277008277139930L;

	public AllParametersMutatedOutException(String message, Throwable exception) {
		super(message, exception);
	}
	
	public AllParametersMutatedOutException(Throwable exception) {
		super(exception);
	}
	
	public AllParametersMutatedOutException(String message) {
		super(message);
	}

	public AllParametersMutatedOutException() {
		this("Algorithm canceled by user.");
	}
}
