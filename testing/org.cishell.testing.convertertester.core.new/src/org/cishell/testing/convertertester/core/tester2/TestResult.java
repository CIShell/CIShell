package org.cishell.testing.convertertester.core.tester2;

import org.cishell.testing.convertertester.core.tester2.filepassresults.FilePassResult;

public class TestResult {
	
	private FilePassResult[] fprs;

	private boolean cachedSuccesses = false;
	private boolean[] successes;
	

	
	public TestResult(FilePassResult[] fprs) {
		this.fprs = fprs;
		this.successes = new boolean[fprs.length];
	}
	
	public FilePassResult[] getFilePassResults() {
		return fprs;
	}
	
	public boolean allSucceeded() {
		if (! cachedSuccesses) cacheSuccesses();
		
		boolean allSucceeded = true;
		for (int ii = 0; ii < successes.length; ii++) {
			if (successes[ii] == false) {
				allSucceeded = false;
				break;
			}
		}
		
		return allSucceeded;
	}
	
	public boolean allFailed() {
		if (! cachedSuccesses) cacheSuccesses();
		
		boolean allFailed = true;
		for (int ii = 0; ii < successes.length; ii++) {
			if (successes[ii] == true) {
				allFailed = false;
				break;
			}
		}
		
		return allFailed;
	}
	
	public boolean someSucceeded() {
		return ! allFailed();
	}
	
	public boolean someFailed() {
		return ! allSucceeded();
	}
	
	private void cacheSuccesses() {
		for (int ii = 0; ii < fprs.length; ii++) {
			FilePassResult fpr = fprs[ii];
			
			if (fpr.getType().equals(FilePassResult.SUCCESS)) {
				this.successes[ii] = true;
			} else {
				this.successes[ii] = false;
			}
		}
		
		this.cachedSuccesses = true;
	}
	
	

	
	
}
