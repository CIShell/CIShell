package org.cishell.testing.convertertester.core.tester.graphcomparison;

public class ComparisonResult {

	private boolean succeeded;
	private RunningLog log;
	
	public ComparisonResult(boolean succeeded, RunningLog log) {
		this.succeeded = succeeded;
		this.log = log;
	}
	
	public boolean comparisonSucceeded() {
		return succeeded;
	}
	
	public String getLog() {
		return log.toString();
	}
	
	public String toString() {
		if (comparisonSucceeded()) {
			return "Success!";
		} else {
			return "Failure: " + "\r\n" +
			       "Log:"     + "\r\n" +
			        log;
		}
	}
}
