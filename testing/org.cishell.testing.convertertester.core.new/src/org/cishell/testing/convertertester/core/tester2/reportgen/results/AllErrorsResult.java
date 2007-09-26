package org.cishell.testing.convertertester.core.tester2.reportgen.results;

public class AllErrorsResult {
	
	private ErrorResult[] ers;
	
	public AllErrorsResult(ErrorResult[] ers) {
		this.ers = ers;
	}
	
	public ErrorResult[] getErrorResults() {
		return this.ers;
	}
}
