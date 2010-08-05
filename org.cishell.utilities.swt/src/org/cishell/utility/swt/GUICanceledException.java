package org.cishell.utility.swt;

public class GUICanceledException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public GUICanceledException() {
		super();
	}

	public GUICanceledException(String arg0) {
		super(arg0);
	}

	public GUICanceledException(Throwable arg0) {
		super(arg0);
	}

	public GUICanceledException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}