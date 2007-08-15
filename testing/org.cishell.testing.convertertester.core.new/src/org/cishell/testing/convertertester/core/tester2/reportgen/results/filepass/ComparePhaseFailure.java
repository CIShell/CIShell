package org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass;

import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;

public class ComparePhaseFailure extends FilePassFailure {

	private String explanation;
	
	public ComparePhaseFailure(Data[] originalData,
			String explanation) {
		super(originalData);
		this.explanation = explanation;
	}
	
	public String getExplanation() {
		return this.explanation;
	}
	
	public String getType() {
		return FilePassResult.COMPARE_FAILURE;
	}

	public boolean succeeded() {
		return false;
	}

	public String getFullExplanation() {
		return "File: " + this.getOriginalFileLabel() + "\n" + 
			this.getExplanation();
	}

}
