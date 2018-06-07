package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.ArrayList;
import java.util.List;

public class AllTestsResult {

	private TestResult[] trs;
	
	private List passedTRs;
	private List partialPassedTRs;
	private List failedTRs;
	
	private List allFilePasses;
	private List passedFilePasses;
	private List failedFilePasses;
	
	public AllTestsResult(TestResult[] trs) {
		this.trs = trs;
		
		initializeTestLists();
		initializeFilePassLists();
		
	}
	
	private void initializeTestLists() {
		this.passedTRs        = new ArrayList();
		this.partialPassedTRs = new ArrayList();
		this.failedTRs        = new ArrayList();
		
		for (int ii = 0; ii < this.trs.length; ii++) {
			TestResult tr = this.trs[ii];
			
			if (tr.allSucceeded()) {
				passedTRs.add(tr);
			} else if (tr.someSucceeded()) {
				partialPassedTRs.add(tr);
			} else {
				failedTRs.add(tr);
			}
		}
	}
	
	private void initializeFilePassLists() {
		this.allFilePasses    = new ArrayList();
		this.passedFilePasses = new ArrayList();
		this.failedFilePasses = new ArrayList();
		
		for (int ii = 0; ii < this.trs.length; ii++) {
			TestResult tr = this.trs[ii];
			
			FilePassResult[] fprs = tr.getFilePassResults();
			for (int jj = 0; jj < fprs.length; jj++) {
				FilePassResult fpr = fprs[jj];
				
				this.allFilePasses.add(fpr);
				
				if (fpr.succeeded()) {
					this.passedFilePasses.add(fpr);
				} else {
					this.failedFilePasses.add(fpr);
				}
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
	
	public int getNumFilePasses() {
		return this.allFilePasses.size();
	}
	
	public FilePassResult[] getAllFilePassResults() {
		return (FilePassResult[]) 
			this.allFilePasses.toArray(new FilePassResult[0]);
	}
	
	public FilePassResult[] getPassesFilePassResults() {
		return (FilePassResult[]) 
			this.passedFilePasses.toArray(new FilePassResult[0]);
	}
	
	public FilePassResult[] getFailedFilePassResults() {
		return (FilePassResult[]) 
			this.failedFilePasses.toArray(new FilePassResult[0]);
	}
}
