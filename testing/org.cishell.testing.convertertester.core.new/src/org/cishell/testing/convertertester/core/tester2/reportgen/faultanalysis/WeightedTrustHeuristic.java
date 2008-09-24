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
public class WeightedTrustHeuristic implements ChanceAtFaultHeuristic{

	public static final Float DEFAULT_FAULT_SCORE = new Float(1);

	public static final float TRUSTED_CONV_PENALTY_REDUCTION = .1f;
	
	public static final float FAILED_CONV_PENALTY = 3.0f;
	public static final float REPEAT_PENALTY = .5f;
	
	public ChanceAtFault[] determine(FilePassResult failFP,
			Converter[] involvedCs,
			Converter[] trustedCs) {
		
		List trustedCList = Arrays.asList(trustedCs);
		
		//assign fault scores to each converter
		
		Map convToFaultScore = new HashMap();
		
		for (int ii = 0; ii < involvedCs.length; ii++) {
			Converter involvedC = (Converter) involvedCs[ii];
			
			Float oldFaultScore = (Float) convToFaultScore.get(involvedC);
			Float newFaultScore;
			if (oldFaultScore == null) {
				//first occurrence of this converter
				
				newFaultScore = DEFAULT_FAULT_SCORE;
				if (isConvThatFailed(involvedC, failFP)) {
					newFaultScore = new Float(newFaultScore.floatValue() *
							FAILED_CONV_PENALTY);
				}
				
			} else {
				//converter has occurred before
				
				newFaultScore = new Float(oldFaultScore.floatValue() + 
						DEFAULT_FAULT_SCORE.floatValue() * REPEAT_PENALTY);
			}
			
			convToFaultScore.put(involvedC, newFaultScore);
	
		}
		

		
		//reduce fault scores of trusted converters
		
		float faultScoresTotal = 0.0f;
		
		Set convs = convToFaultScore.keySet();
		Iterator convIter = convs.iterator();
		while (convIter.hasNext()) {
			Converter convInvolved = (Converter) convIter.next();
			Float convFaultScore = 
				(Float) convToFaultScore.get(convInvolved);
			
			if (trustedCList.contains(convInvolved)) {
				convFaultScore = new Float(convFaultScore.floatValue() *
						TRUSTED_CONV_PENALTY_REDUCTION);
			}
			
			convToFaultScore.put(convInvolved, convFaultScore);
			faultScoresTotal += convFaultScore.floatValue();
		}
		
		List resultCAFList = new ArrayList();
		
		
		//return chance each converter is at fault, based on fault scores.
		
		for (int ii = 0; ii < involvedCs.length; ii++) {
			Converter involvedC = involvedCs[ii];
			
			Float faultScore = (Float) convToFaultScore.get(involvedC);
			
			float normalizedFaultScore;
			if (faultScoresTotal != 0.0f) {
				normalizedFaultScore =
					faultScore.floatValue() / faultScoresTotal;
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
