package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.HashSet;
import java.util.Set;

import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassFailure;

public class ErrorResult {
	
	private String explanation;
	private FilePassFailure[] associatedFPs;
	
	public ErrorResult(String explanation,
			FilePassFailure[] associatedFPs) {
		this.explanation = explanation;
		this.associatedFPs = associatedFPs;
	}
	public String getExplanation() {
		return this.explanation;
	}
	
	public String[] getErrorSource() {
		Set errorSources = new HashSet();
		for (int ii = 0; ii < associatedFPs.length; ii++) {
			FilePassFailure fp = associatedFPs[ii];
			errorSources.add(fp.getFailedConverter());
		}
		
		return (String[]) errorSources.toArray(new String[0]);
	}
	
	public int getNumOccurrences() {
		return associatedFPs.length;
	}
	
	public FilePassResult[] getAssociatedFilePasses() {
		return this.associatedFPs;
	}
}
