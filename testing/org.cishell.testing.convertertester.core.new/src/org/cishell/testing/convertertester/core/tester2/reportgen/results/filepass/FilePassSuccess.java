package org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass;

import org.cishell.framework.data.Data;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;

public class FilePassSuccess extends FilePassResult {

	public static final String NO_EXPLANATION = "";
	
	private String explanation;
	
	public FilePassSuccess(Data[] originalData) {
		this(originalData, NO_EXPLANATION);
	}
	
	public FilePassSuccess(Data[] originalData, String explanation) {
		super(originalData);
		this.explanation = explanation;
	}

	public String getType() {
		return FilePassResult.SUCCESS;
	}

	public String getExplanation() {
		return this.explanation;
	}

	public boolean succeeded() {
		return true;
	}

	public String getFullExplanation() {
		return "File: " + this.getOriginalFileLabel() + "\n" + 
			getExplanation();
	}
}
