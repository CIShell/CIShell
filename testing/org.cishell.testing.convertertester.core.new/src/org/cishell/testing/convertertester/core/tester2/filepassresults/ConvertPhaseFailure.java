package org.cishell.testing.convertertester.core.tester2.filepassresults;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvFailureInfo;

public class ConvertPhaseFailure extends FilePassResult {

	public static final String TEST_PHASE = "test conversion phase";
	public static final String COMPARISON_PHASE = "comparison conversion phase";
	
	private String explanation;
	private AlgorithmFactory failedConverter;
	private String phase;
	
	public ConvertPhaseFailure(
			Data[] originalData,
			AlgorithmFactory[] testConverters,
			AlgorithmFactory[] comparisonConverters,
			ConvFailureInfo convertFailInfo,
			String phase) {
		super(originalData, testConverters, comparisonConverters);
		this.explanation = convertFailInfo.getExplanation();
		this.failedConverter = convertFailInfo.getFailedConverter();
		this.phase = phase;
	}
	
	public String getPhase() {
		return this.phase;
	}
	
	public String getExplanation() {
		return this.explanation;
	}
	
	public AlgorithmFactory getFailedConverter() {
		return this.failedConverter;
	}
	
	public String getType() {
		return FilePassResult.CONVERT_FAILURE;
	}

}
