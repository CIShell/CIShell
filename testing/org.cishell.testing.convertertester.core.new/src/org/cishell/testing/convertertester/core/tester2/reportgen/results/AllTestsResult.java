package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.ArrayList;
import java.util.List;

public class AllTestsResult {

	private TestResult[] trs;
	
	private List passedTRs;
	private List partialPassedTRs;
	private List failedTRs;
	
	public AllTestsResult(TestResult[] trs) {
		this.trs = trs;
		
		initializePassFailLists(trs);
		
	}
	
	private void initializePassFailLists(TestResult[] trs) {
		this.passedTRs        = new ArrayList();
		this.partialPassedTRs = new ArrayList();
		this.failedTRs        = new ArrayList();
		
		for (int ii = 0; ii < trs.length; ii++) {
			TestResult tr = trs[ii];
			
			if (tr.allSucceeded()) {
				passedTRs.add(tr);
			} else if (tr.someSucceeded()) {
				partialPassedTRs.add(tr);
			} else {
				failedTRs.add(tr);
			}
		}
	}
	
	public TestResult[] getTestResults() {
		return this.trs;
	}
	
	public int getNumTestsPassed() {
		return passedTRs.size();
	}
	
	public int getNumTestsPartialPassed() {
		return partialPassedTRs.size();
	}
	
	public int getNumTestsCompletelyFailed() {
		return failedTRs.size();
	}
	
	public int getNumTests() {
		return trs.length;
	}
	
	public TestResult[] getPassedTestResults() {
		return (TestResult[]) this.passedTRs.toArray(new TestResult[0]);
	}
	
	public TestResult[] getPartialPassedTestResults() {
		return (TestResult[]) this.partialPassedTRs.toArray(new TestResult[0]);
	}

	public TestResult[] getFailedTestResults() {
		return (TestResult[]) this.failedTRs.toArray(new TestResult[0]);
	}
	
	public TestResult[] getAllTestResults() {
		List allTestResults = new ArrayList();
		
		allTestResults.addAll(this.passedTRs);
		allTestResults.addAll(this.partialPassedTRs);
		allTestResults.addAll(this.failedTRs);
		
		return (TestResult[]) allTestResults.toArray(new TestResult[0]);
	}
}
