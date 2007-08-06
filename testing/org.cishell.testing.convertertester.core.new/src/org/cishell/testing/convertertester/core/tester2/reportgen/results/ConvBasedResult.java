package org.cishell.testing.convertertester.core.tester2.reportgen.results;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cishell.framework.algorithm.AlgorithmFactory;


public class ConvBasedResult {
	
	private AlgorithmFactory conv;
	
	private List passes = new ArrayList();
	private boolean isTrusted;
	
	private boolean dataCached = false;
	private float chanceOfFlaw = 0.0f;
	private float percentPassed = 1.0f;
	
	public ConvBasedResult(AlgorithmFactory conv) {
		this.conv = conv;
		this.isTrusted = false;
	}
	
	public void setTrusted(boolean isTrusted) {
		this.isTrusted = isTrusted;
	}
	
	public boolean isTrusted() {
		return this.isTrusted;
	}
	
	public float getChanceOfFlaw() {
		if (! this.dataCached) cacheData();
		return this.chanceOfFlaw;
	}
	
	public float getPercentPassed() {
		if (! this.dataCached) cacheData();
		return this.percentPassed;
	}
	
	
	public void addPass(ConvFilePass cfp) {
		invalidateCache();
		this.passes.add(cfp);
	}
	
	public AlgorithmFactory getConverter() {
		return this.conv;
	}
	
	private void cacheData() {
		float chanceCorrect = 1.0f;
		
		float totalPasses = this.passes.size();
		float passedSoFar = 0f;
		float failedSoFar = 0f;
		
		Iterator iter = this.passes.iterator();
		while (iter.hasNext()) {
			ConvFilePass cfp = (ConvFilePass) iter.next();
			
			if (cfp instanceof ConvFilePassSuccess) {
				passedSoFar++;
			} else {
				ConvFilePassFailure cfpFailure = (ConvFilePassFailure) cfp;
				float chanceAtFault = cfpFailure.getChanceAtFault();
				
				failedSoFar++;
				
				if (chanceCorrect == 1.0f) {
					chanceCorrect = 1.0f - chanceAtFault;
				} else {
					chanceCorrect = chanceCorrect * (1.0f - chanceAtFault);
				}
			}
		}
		
		this.percentPassed = passedSoFar / totalPasses;
		this.chanceOfFlaw = 1.0f - chanceCorrect;
		this.dataCached = true;
	}
	
	private void invalidateCache() {
		this.dataCached = false;
	}
	
}
