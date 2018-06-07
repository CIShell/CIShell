package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.ArrayList;
import java.util.List;

public class AllConvsResult {
	
	private ConvResult[] allCRs;
	
	private List trustedCRs;
	private List untrustedCRs;
	
	public AllConvsResult(ConvResult[] crs) {
		this.allCRs = crs;
		
		initializeTrustedAndUntrusted(crs);
	}
	
	private void initializeTrustedAndUntrusted(ConvResult[] crs) {
		List trustedCRs = new ArrayList();
		List untrustedCRs = new ArrayList();
		for (int ii = 0; ii < crs.length; ii++) {
			ConvResult cr = crs[ii];
			
			if (cr.isTrusted()) {
				trustedCRs.add(cr);
			} else {
				untrustedCRs.add(cr);
			}
		}
	
		this.trustedCRs   = trustedCRs;
		this.untrustedCRs = untrustedCRs;
	}
	
	public ConvResult[] getConvResults() {
		return this.allCRs;
	}
	
	public ConvResult[] getTrustedConvResults() {
		return (ConvResult[]) this.trustedCRs.toArray(new ConvResult[0]);
	}
	
	public ConvResult[] getUntrustedConvResults() {
		return (ConvResult[]) this.untrustedCRs.toArray(new ConvResult[0]);
	}
}
