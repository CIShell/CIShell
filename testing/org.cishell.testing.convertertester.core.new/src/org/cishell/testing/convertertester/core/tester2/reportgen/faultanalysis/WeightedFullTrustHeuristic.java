package org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;

/**
 * 
 * @author mwlinnem
 *
 * A less naive approach to assessing likelihood of fault,
 * that gives higher weight to the converter that caused the error for
 * failures in conversion, and increases chance at fault for converters
 * involved more than once in the conversion path
 */
public class WeightedFullTrustHeuristic implements ChanceAtFaultHeuristic{

	public static final Float TRUSTED_FAULT_SCORE = new Float(0);
	public static final Float DEFAULT_FAULT_SCORE = new Float(1);
	public static final Float FAILED_CONV_FAULT_SCORE = new Float(2);
	
	public ChanceAtFault[] determine(FilePassResult failFP,
			Converter[] involvedCs,
			Converter[] trustedCs) {
		
		List trustedCList = Arrays.asList(trustedCs);
		
		//eliminate converters that are trusted
		
		List unTrustedCs = new ArrayList();
		for (int ii = 0; ii < involvedCs.length; ii++) {
			Converter c = involvedCs[ii];
			
			if (! trustedCList.contains(c)) {
				unTrustedCs.add(c);
			}
		}
		
		//assign fault scores to each untrusted converter
		
		Map convToFaultScore = new HashMap();
		
		float totalFaultScore = 0.0f;
		for (int ii = 0; ii < unTrustedCs.size(); ii++) {
			Converter untrustedC = (Converter) unTrustedCs.get(ii);
			
			Float oldFaultScore = (Float) convToFaultScore.get(untrustedC);
			Float newFaultScore;
			if (oldFaultScore == null) {
				//first occurrence of this converter
				
				if (! isConvThatFailed(untrustedC, failFP)) {
					newFaultScore = DEFAULT_FAULT_SCORE;
				} else {
					newFaultScore = FAILED_CONV_FAULT_SCORE;
				}
				
			} else {
				//converter has occurred before
				
				newFaultScore = new Float(oldFaultScore.floatValue() + 
						DEFAULT_FAULT_SCORE.floatValue());
			}
			
			convToFaultScore.put(untrustedC, newFaultScore);
			totalFaultScore += newFaultScore.floatValue();
		}
		
		//return chance each converter is at fault, based on fault scores.
		
		
		List resultCAFList = new ArrayList();
		
		
		
		
		for (int ii = 0; ii < involvedCs.length; ii++) {
			Converter involvedC = involvedCs[ii];
			
			Float faultScore = (Float) convToFaultScore.get(involvedC);
			//if there is no associated score...
			if (faultScore == null) {
				//this converter must have been removed because it was
				//trusted, so give it the fault score for trusted converters.
				faultScore = TRUSTED_FAULT_SCORE;
			}
			
			float normalizedFaultScore;
			if (totalFaultScore != 0.0f) {
				normalizedFaultScore =
					faultScore.floatValue() / totalFaultScore;
			} else {
				normalizedFaultScore = 0.0f;
			}
			
			ChanceAtFault resultCAF = new ChanceAtFault(failFP, involvedC,
					normalizedFaultScore);
			
			resultCAFList.add(resultCAF);
		}
		
		return (ChanceAtFault[]) resultCAFList.toArray(new ChanceAtFault[0]);
		
		
		

//		
//		//easier to deal with as a list
//		List trustedCList = Arrays.asList(trustedCs);
//		
//		//eliminate converters involved twice in the file pass
//		
//		Set uniqueInvolvedCs = new HashSet();
//		for (int ii = 0; ii < involvedCs.length; ii++) {
//			uniqueInvolvedCs.add(involvedCs[ii]);
//		}
//		//eliminate converters that are trusted
//		
//		List uniqueUntrustedCs = new ArrayList();
//		Iterator iter = uniqueInvolvedCs.iterator();
//		while (iter.hasNext()) {
//			Converter c  = (Converter) iter.next();
//			if (! trustedCList.contains(c)) {
//				//converter isn't trusted
//				//add it to the list
//				uniqueUntrustedCs.add(c);
//			} else {
//				//converter is trusted
//				//do nothing
//			}
//		}
//		
//		float chanceEachAtFault = 1.0f / uniqueUntrustedCs.size();
//		
//		List chanceAtFaultList = new ArrayList();
//		for (int ii = 0; ii < involvedCs.length; ii++) {
//			Converter c = involvedCs[ii];
//			
//			ChanceAtFault chanceAtFault = null;
//			if (uniqueUntrustedCs.contains(c)) {
//				chanceAtFault = 
//					new ChanceAtFault((FilePassFailure) failFP, c, chanceEachAtFault);
//			} else {
//				chanceAtFault = 
//					new ChanceAtFault((FilePassFailure) failFP, c, 0.0f);
//			}
//			
//			chanceAtFaultList.add(chanceAtFault);
//		}
//		
//		return (ChanceAtFault[]) 
//			chanceAtFaultList.toArray(new ChanceAtFault[0]);
	}
	
	private boolean isConvThatFailed(Converter c, FilePassResult failFP) {
		return failFP.failedWhileConverting() && 
		failFP.getFailedConverter() == c;
	}
}
