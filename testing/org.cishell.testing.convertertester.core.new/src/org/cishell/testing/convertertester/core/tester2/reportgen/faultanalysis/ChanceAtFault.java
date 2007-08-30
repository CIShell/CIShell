package org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassFailure;

public class ChanceAtFault {
	
	private FilePassFailure failedFP;
	private Converter c;
	private float chanceAtFault;
	
	public ChanceAtFault(FilePassFailure failedFP, Converter c, float chanceAtFault) {
		this.failedFP = failedFP;
		this.c = c;
		this.chanceAtFault = chanceAtFault;
	}
	
	public float getChanceAtFault() {
		return this.chanceAtFault;
	}
	
	public float getChanceNotAtFault() {
		return 1.0f - getChanceAtFault();
	}
	
	public Converter getConverter() {
		return this.c;
	}
	
	public FilePassFailure getFailedFilePass() {
		return this.failedFP;
	}
	
	public String getExplanation() {
		return this.failedFP.getExplanation();
	}
	
	public String toString() {
		return String.valueOf(chanceAtFault);
	}
}
