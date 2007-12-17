package org.cishell.testing.convertertester.core.tester2.reportgen.results.converter;

import org.cishell.testing.convertertester.core.converter.graph.Converter;

public class ConvFailureInfo {
	
	private Converter failedConverter;
	private String explanation;
	
	public ConvFailureInfo(String explanation, Converter failedConverter) {
		this.explanation = explanation;
		this.failedConverter = failedConverter;
	}
	
	public String getExplanation() {
		return this.explanation;
	}
	
	public Converter getFailedConverter() {
		return this.failedConverter;
	}
}
