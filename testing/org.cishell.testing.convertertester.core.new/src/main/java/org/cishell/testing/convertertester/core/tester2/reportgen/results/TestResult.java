package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.converter.graph.ConverterPath;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.PassPhase;



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
		
		if (fprs.length > 0) {
			this.format = fprs[0].getFileFormat();
		} else {
			this.format = "Unable to determine format for a test that has " +
					"no file passes";
		}
		
		for (int ii = 0; ii < fprs.length; ii++) {
			FilePassResult fpr = fprs[ii];
			fpr.setParentTest(this);
			fpr.setIndex(ii);
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
	
	/**
	 * Returns all the converters in order of conversion.
	 * (Some converters may appear twice if they appear twice in
	 * the conversion path).
	 * @return
	 */
	public Converter[] getAllConverters() {
		List testConvsPath = this.testConvs.getPath();
		List compareConvsPath = this.compareConvs.getPath();
		
		int testConvsPathSize = testConvsPath.size();
		int compareConvsPathSize = compareConvsPath.size();
		
		Converter[] allConvsArray = 
			new Converter[testConvsPathSize + compareConvsPathSize];
		
		Converter[] testConvsArray = 
			(Converter[]) testConvs.getPath().toArray(new Converter[0]);
		
        //copy all of testConvsArray into the beginning of allConvsArray	
		System.arraycopy(testConvsArray, 0, allConvsArray, 0,
				testConvsArray.length);
		
		Converter[] compareConvsArray = 
			(Converter[]) compareConvs.getPath().toArray(new Converter[0]);
		
		//copy all of compareConvsArray into the rest of allConvsArray
		System.arraycopy(compareConvsArray, 0, allConvsArray,
				testConvsArray.length, compareConvsArray.length);
		
		return allConvsArray;
		
	}
	
	
	/**
	 * Gets an array of converters up until, and including, the
	 * provided converter.
	 * @param c the converter we get all other converters up through.
	 * @param phase the phase the converter is found in. This
	 * is important since a converter may be found in both
	 * the test conversion and comparison conversion phase, and
	 * it is ambiguous which we want to go up through.
	 * @return
	 */
	public Converter[] getConvertersThrough(Converter c,
			PassPhase phase) {
		
		if (phase.equals(PassPhase.TEST_CONV_PHASE)) {
			/*
			 * include some of the test converters
			 */
			int cIndex = this.testConvs.getPath().indexOf(c);
			
			List someTestConvs = 
				this.testConvs.getPath().subList(0, cIndex + 1);
			
			return (Converter[]) 
				someTestConvs.toArray(new Converter[0]);
			
		} else if (phase.equals(PassPhase.COMPARE_CONV_ORIG_PHASE) ||
				phase.equals(PassPhase.COMPARE_CONV_RESULT_PHASE)) {
			/*
			 * include all of the test converters and some
			 * of the comparison converters
			 */
			
			List allTestConvs = this.testConvs.getPath();
			
			int cIndex = this.compareConvs.getPath().indexOf(c);
			
			List someCompareConvs =
				this.compareConvs.getPath().subList(0, cIndex + 1);
			
			
			List allTestAndSomeCompareConvs = new ArrayList();
			allTestAndSomeCompareConvs.addAll(allTestConvs);
			allTestAndSomeCompareConvs.addAll(someCompareConvs);
			
			return (Converter[]) 
				allTestAndSomeCompareConvs.toArray(new Converter[0]);			
			
		} else if (phase.equals(PassPhase.GRAPH_COMPARE_PHASE) ||
			phase.equals(PassPhase.SUCCEEDED_PHASE)) {
				/*
				 * include all of the test converters and all
				 * of the comparison converters
				 */
			
			return getAllConverters();
		} else {
			throw new IllegalArgumentException("Invalid PassPhase: " + phase);
		}
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
			
			if (fpr.succeeded()) {
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
		return FilePassResult.COMPARE_BY_SUCCESS;
	}	
}
