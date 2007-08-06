package org.cishell.testing.convertertester.core.tester2.filepassresults;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;

public class ComparePhaseFailure extends FilePassResult {

	private String explanation;
	
	public ComparePhaseFailure(
			Data[] originalData,
			AlgorithmFactory[] testConverters,
			AlgorithmFactory[] comparisonConverters,
			String explanation) {
		super(originalData, testConverters, comparisonConverters);
		this.explanation = explanation;
	}
	
	public String getExplanation() {
		return this.explanation;
	}
	
	public String getType() {
		return FilePassResult.COMPARE_FAILURE;
	}

}
