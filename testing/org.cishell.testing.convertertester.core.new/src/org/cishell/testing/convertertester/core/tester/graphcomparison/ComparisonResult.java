package org.cishell.testing.convertertester.core.tester.graphcomparison;

public class ComparisonResult {

	private boolean succeeded;
	private String explanation;
	private RunningLog log;
	
	public ComparisonResult(boolean succeeded, RunningLog log) {
		this(succeeded, "", log);
	}
	
	public ComparisonResult(boolean succeeded, String explanation,
			RunningLog log) {
		this.succeeded = succeeded;
		this.explanation = explanation;
		this.log = log;
	}
	
	public boolean comparisonSucceeded() {
		return succeeded;
	}
	
	public String explanation() {
		return explanation;
	}
	
	public String getLog() {
		return log.toString();
	}
	
	public String toString() {
		if (comparisonSucceeded()) {
			return "Success!";
		} else {
			return "Failure: " + explanation + "\n" +
			        "Log:" + "\n" +
			        log;
		}
	}
}
