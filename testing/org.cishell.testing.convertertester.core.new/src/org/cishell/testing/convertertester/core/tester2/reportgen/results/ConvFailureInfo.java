package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import org.cishell.framework.algorithm.AlgorithmFactory;

public class ConvFailureInfo {
	
	private AlgorithmFactory failedConverter;
	private String explanation;
	
	public ConvFailureInfo(String explanation, AlgorithmFactory failedConverter) {
		this.explanation = explanation;
		this.failedConverter = failedConverter;
	}
	
	public String getExplanation() {
		return this.explanation;
	}
	
	public AlgorithmFactory getFailedConverter() {
		return this.failedConverter;
	}
}
