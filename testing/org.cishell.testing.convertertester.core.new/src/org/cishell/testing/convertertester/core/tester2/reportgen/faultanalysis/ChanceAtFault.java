package org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis;

import java.util.Comparator;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassFailure;

public class ChanceAtFault {
	
	public static final Comparator COMPARE_BY_LIKELIHOOD = 
		new CompareByLikelihood();
	
	private FilePassResult failedFP;
	private Converter c;
	private float chanceAtFault;
	
	public ChanceAtFault(FilePassResult failedFP, Converter c, float chanceAtFault) {
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
	
	public FilePassResult getFailedFilePass() {
		return this.failedFP;
	}
	
	public String getExplanation() {
		return this.failedFP.getExplanation();
	}
	
	public String toString() {
		return String.valueOf(chanceAtFault);
	}
	
	private static class CompareByLikelihood implements Comparator {

		public int compare(Object arg0, Object arg1) {
			if (arg0 instanceof ChanceAtFault &&
				arg1 instanceof ChanceAtFault) {
				ChanceAtFault caf0 = (ChanceAtFault) arg0;
				ChanceAtFault caf1 = (ChanceAtFault) arg1;
				
				float difference = caf1.getChanceAtFault() - 
					caf0.getChanceAtFault();
				
				if (difference > 0.0f) {
					return 1;
				} else if (difference == 0.0f) {
					return 0;
				} else if (difference < 0.0f) {
					return -1;
				}
				
			} else {
				throw new IllegalArgumentException("Must compare two " +
				"ChanceAtFaults");
			}
					
			return 0;
		}
		
	}
}
