package org.cishell.testing.convertertester.core.tester2.filepassresults;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;

public abstract class FilePassResult {
	
	public static final String SUCCESS = "success";
	public static final String CONVERT_FAILURE = "convert failure";
	public static final String COMPARE_FAILURE = "compare failure";
	
	private AlgorithmFactory[] testConverters;
	private AlgorithmFactory[] comparisonConverters;
	private AlgorithmFactory[] allConverters;
	private Data[] originalData;
	
	public FilePassResult(
			Data[] originalData,
			AlgorithmFactory[] testConverters,
			AlgorithmFactory[] comparisonConverters) {
		this.originalData = originalData;
		this.testConverters = testConverters;
		this.comparisonConverters = comparisonConverters;
	}
	
	public AlgorithmFactory[] getTestConverters() {
		return this.testConverters;
	}
	
	public AlgorithmFactory[] getComparisonConverters() {
		return this.comparisonConverters;
	}
	
	public AlgorithmFactory[] getAllConverters() {
		if (this.allConverters == null) {
			initializeAllConverters();
		}
		
		return this.allConverters;
	}
	
	public Data[] getOriginalData() {
		return this.originalData;
	}
	
	public abstract String getType();
	
	private void initializeAllConverters() {
		this.allConverters = new AlgorithmFactory[testConverters.length + 
		                                     comparisonConverters.length];
		for (int ii = 0; ii < testConverters.length; ii++) {
			allConverters[ii] = testConverters[ii];
		}
		
		int startIndex = testConverters.length;
		for (int ii = startIndex; ii < startIndex + comparisonConverters.length; ii++) {
			allConverters[ii] = comparisonConverters[ii - startIndex];
		}
	}
}
