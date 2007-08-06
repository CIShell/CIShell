package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import org.cishell.testing.convertertester.core.tester2.filepassresults.FilePassResult;

public class ConvFilePassFailure extends ConvFilePass {
	
	private float chanceAtFault;
	
	public ConvFilePassFailure(FilePassResult fpr, float chanceAtFault) {
		super(fpr);
		this.chanceAtFault = chanceAtFault;
	}
	
	public float getChanceAtFault() {
		return this.chanceAtFault;
	}
}
