package org.cishell.utilities;

public class AlgorithmNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public AlgorithmNotFoundException() {
		super();
	}

	public AlgorithmNotFoundException(String arg0) {
		super(arg0);
	}

	public AlgorithmNotFoundException(Throwable arg0) {
		super(arg0);
	}

	public AlgorithmNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}