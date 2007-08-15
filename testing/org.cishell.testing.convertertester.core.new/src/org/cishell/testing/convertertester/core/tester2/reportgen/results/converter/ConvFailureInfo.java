package org.cishell.testing.convertertester.core.tester2.reportgen.results.converter;

import org.cishell.framework.algorithm.AlgorithmFactory;

public class ConvFailureInfo {
	
	private String failedConverter;
	private String explanation;
	
	public ConvFailureInfo(String explanation, String failedConverter) {
		this.explanation = explanation;
		this.failedConverter = failedConverter;
	}
	
	public String getExplanation() {
		return this.explanation;
	}
	
	public String getFailedConverter() {
		return this.failedConverter;
	}
}
