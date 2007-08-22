package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;



public class TestResult implements Comparable {
	
	public static final String DEFAULT_NAME = "Default Test Result Name";
	
	private FilePassResult[] fprs;
	private List passedFPRs;
	private List failedFPRs;
	
	private int testNum;
	
	private String format;
	private String name = DEFAULT_NAME;
	
	private ConverterPath testConvs;
	private ConverterPath compareConvs;

	private boolean cachedSuccesses = false;
	private boolean[] successes;
	
	public TestResult(FilePassResult[] fprs, ConverterPath testConvs,
			ConverterPath compareConvs, int testNum)  {
		this.fprs = fprs;
		this.testConvs = testConvs;
		this.compareConvs = compareConvs;
		this.testNum = testNum;
		this.name = "Test " + testNum;
		
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
	
	public String getSummary() {
		if (allSucceeded()) {
			return "Successful";
		} else if (someSucceeded()) {
			return "Partially Successful";
		} else {
			return "Failed";
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
	
	public String getName() {
		return this.name;
	}
	
	public int getTestNum() {
		return this.testNum;
	}
	
	public String getNameWithSuccess() {
		return getName() + " - " + getSummary();
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

	public int compareTo(Object arg0) {
		if ((arg0 instanceof TestResult)) {
			TestResult otherTR = (TestResult) arg0;
			return getTestNum() - otherTR.getTestNum();
		} else {
			throw new IllegalArgumentException("Must compare to another " +
			"TestResult");
		}
	}
	
	public static Comparator getCompareBySuccess() {
		return new CompareBySuccess();
	}
	
	private static class CompareBySuccess implements Comparator {
		
		/**
		 * Compare first by success, where
		 * completely successful > partially successful > complet failure,
		 * and then alphabetize (the natural order of test results) 
		 * for cases where they both have the same success type.
		 */
		public int compare(Object o1, Object o2) {
			if (o1 instanceof TestResult && o2 instanceof TestResult) {
				TestResult tr1 = (TestResult) o1;
				TestResult tr2 = (TestResult) o2;
				
				int success1 = getSuccessRating(tr1);
				int success2 = getSuccessRating(tr2);
				
				if (success1 != success2) {
					return success2 - success1;
				} else {
					return tr1.compareTo(tr2);
				}
				
			} else {
				throw new IllegalArgumentException("Can only " +
						"compare test results");
			}
			
		}
		
		private int getSuccessRating(TestResult tr) {
			if (tr.allSucceeded()) {
				return 2;
			} else if (tr.someSucceeded()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	

	
	
}
