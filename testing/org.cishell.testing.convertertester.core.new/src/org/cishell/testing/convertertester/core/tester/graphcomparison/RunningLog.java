package org.cishell.testing.convertertester.core.tester.graphcomparison;

public class RunningLog {

	private String log = "";
	
	/**
	 * Adds the provided text as a new line to the end of the log.
	 * @param s the text to be added to the log.
	 */
	public void append(String s) {
		log += s +"\r\n";
	}
	
	public void prepend(String s) {
		log = s + "\r\n" + log;
	}
	
	public String getLog() {
		return log;
	}
	public String toString() {
		return getLog();
	}
}
