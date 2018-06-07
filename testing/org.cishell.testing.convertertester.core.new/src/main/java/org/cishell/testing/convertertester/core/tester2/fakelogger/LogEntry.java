package org.cishell.testing.convertertester.core.tester2.fakelogger;

/**
 * Too much unnecessary stuff in real official OSGi LogEntry.
 * This should do.
 * @author mwlinnem
 *
 */
public class LogEntry {

	private String message;
	private Throwable exception;
	
	public LogEntry(String message) {
		this(message, null);
	}
	
	public LogEntry(String message, Throwable exception) {
		this.message = message;
		this.exception = exception;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public Throwable getThrowable() {
		return this.exception;
	}
}
