package org.cishell.testing.convertertester.core.tester2.reportgen.allerrors.analyzer;

import org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis.ChanceAtFault;

public class ErrorSource {

	private String comment;
	private ChanceAtFault[] cafs;
	
	public ErrorSource(String comment, ChanceAtFault[] cafs) {
		this.comment = comment;
		this.cafs = cafs;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public ChanceAtFault[] getCulprits() {
		return this.cafs;
	}
}
