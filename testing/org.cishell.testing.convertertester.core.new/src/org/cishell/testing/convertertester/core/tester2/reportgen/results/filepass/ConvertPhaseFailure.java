package org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass;

import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.converter.ConvFailureInfo;

public class ConvertPhaseFailure extends FilePassFailure {

	public static final String TEST_PHASE = "test conversion phase";
	public static final String COMPARISON_PHASE = "comparison conversion phase";
	
	private String explanation;
	private String failedConverter;
	private String phase;
	
	public ConvertPhaseFailure(Data[] originalData,
			ConvFailureInfo convertFailInfo, String phase) {
		super(originalData);
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
	
	public String getFailedConverter() {
		return this.failedConverter;
	}
	
	public String getType() {
		return FilePassResult.CONVERT_FAILURE;
	}

	public boolean succeeded() {
		return false;
	}

	public String getFullExplanation() {
		return "File: " + this.getOriginalFileLabel() + "\n" + 
			"Failed converter: " + getFailedConverter() + "\n" +
			getExplanation();
	}

}
