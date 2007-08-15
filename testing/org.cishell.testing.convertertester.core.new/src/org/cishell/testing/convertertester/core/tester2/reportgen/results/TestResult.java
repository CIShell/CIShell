package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.ArrayList;
import java.util.List;

import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;



public class TestResult {
	
	public static final String DEFAULT_NAME = "Default Test Result Name";
	
	private FilePassResult[] fprs;
	private List passedFPRs;
	private List failedFPRs;
	
	private String format;
	private String name = DEFAULT_NAME;
	
	private ConverterPath testConvs;
	private ConverterPath compareConvs;

	private boolean cachedSuccesses = false;
	private boolean[] successes;
	
	public TestResult(FilePassResult[] fprs, ConverterPath testConvs, ConverterPath compareConvs) {
		this.fprs = fprs;
		this.testConvs = testConvs;
		this.compareConvs = compareConvs;
		
		this.successes = new boolean[fprs.length];
		
		this.format = fprs[0].getFormat();
		
		for (int ii = 0; ii < fprs.length; ii++) {
			FilePassResult fpr = fprs[ii];
			fpr.setParentTest(this);
		}
		
		initializePassFailLists(fprs);
	}
	
	private void initializePassFailLists(FilePassResult[] fprs) {
		this.passedFPRs = new ArrayList();
		this.failedFPRs = new ArrayList();
		
		for (int ii = 0; ii < fprs.length; ii++) {
			FilePassResult fpr = fprs[ii];
			
			if (fpr.succeeded()) {
				this.passedFPRs.add(fpr);
			} else {
				this.failedFPRs.add(fpr);
			}
		}
	}
	
	public FilePassResult[] getFilePassResults() {
		return fprs;
	}
	
	public String getShortSummary() {
		if (allSucceeded()) {
			return "Success";
		} else if (someSucceeded()) {
			return "Partial Success";
		} else {
			return "Failure";
		}
	}
	
	public FilePassResult[] getFilePassSuccesses() {
		return (FilePassResult[]) this.passedFPRs.toArray(new FilePassResult[0]);
	}
	
	public FilePassResult[] getFilePassFailures() {
		return (FilePassResult[]) this.failedFPRs.toArray(new FilePassResult[0]);
	}
	
	public int getNumFilePassFailures() {
		return this.failedFPRs.size();
	}
	
	public int getNumFilePassSuccesses() {
		return this.passedFPRs.size();
	}
	
	public int getNumFilePasses() {
		return this.fprs.length;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getFormat() {
		return this.format;
	}
	
	public ConverterPath getTestConverters() {
		return this.testConvs;
	}
	
	public ConverterPath getComparisonConverters() {
		return this.compareConvs;
	}
	
	public ConverterPath getAllConverters() {
		return getTestConverters().
			appendNonMutating(getComparisonConverters());
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
