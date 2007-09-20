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

public class ErrorProximityHeuristic implements ChanceAtFaultHeuristic {
	
	public static final float FAULT_REDUCTION_PER_DEGREE_REMOVED = .33333f;
	public static final float FAULT_REDUCTION_FOR_TRUST = .5f;
	
		public ChanceAtFault[] determine(FilePassResult failFP,
				Converter[] involvedCs,
				Converter[] trustedCs) {
			
			List trustedCList = Arrays.asList(trustedCs);
			
			//assign fault scores to each converter
		
			Map convToFaultScore = new HashMap();
			
		if (failFP.failedWhileConverting()) {
			
			//assign fault such that the closer a converter is to the failed
			//converter, the more likely it is to be at fault
			
			final float startingFaultScore = 1.0f;

			float currentFaultScore = startingFaultScore;


			for (int ii = 0; ii < involvedCs.length; ii++) {
				Converter involvedC = (Converter) involvedCs[ii];

				Float oldFaultScore = (Float) convToFaultScore.get(involvedC);

				Float newFaultScore;
				if (oldFaultScore == null) {
					// first occurrence of this converter
					newFaultScore = new Float(currentFaultScore);
				} else {
					// converter occurred once before (at least)
					// the same thing for now
					newFaultScore = new Float(currentFaultScore);
				}

				convToFaultScore.put(involvedC, newFaultScore);

				currentFaultScore /= FAULT_REDUCTION_PER_DEGREE_REMOVED;
			}
		} else if (failFP.failedWhileComparingGraphs()) {
			//assign fault evenly across all converters
			
			Float faultScore = new Float(1);
			
			for (int ii = 0; ii < involvedCs.length; ii++) {
				Converter involvedC = (Converter) involvedCs[ii];

				Float oldFaultScore = (Float) convToFaultScore.get(involvedC);

				Float newFaultScore;
				if (oldFaultScore == null) {
					newFaultScore = faultScore;
				} else {
					// converter occurred once before (at least)
					// the same thing for now
					newFaultScore = faultScore;
				}

				convToFaultScore.put(involvedC, newFaultScore);
			}
		}
			

			Set convsInvolved = convToFaultScore.keySet();
			
			//reduce fault score of trusted converters
			
			Iterator convIter1 = convsInvolved.iterator();
			while (convIter1.hasNext()) {
				Converter convInvolved = (Converter) convIter1.next();
				Float convFaultScore = 
					(Float) convToFaultScore.get(convInvolved);
			
				if (trustedCList.contains(convInvolved)) {
					Float newConvFaultScore = 
						new Float(convFaultScore.floatValue() *
								FAULT_REDUCTION_FOR_TRUST);
					convToFaultScore.put(convInvolved, newConvFaultScore);
				}
			}
			
			//determine total fault score
			
			float faultScoresTotal = 0.0f;
			
			Iterator convIter2 = convsInvolved.iterator();
			while (convIter2.hasNext()) {
				Converter convInvolved = (Converter) convIter2.next();
				Float convFaultScore = 
					(Float) convToFaultScore.get(convInvolved);
			
				faultScoresTotal += convFaultScore.floatValue();
			}
			
			//normalize fault scores to total 1.
	
			Iterator convIter3 = convsInvolved.iterator();
			while (convIter3.hasNext()) {
				Converter convInvolved = (Converter) convIter3.next();
				Float convFaultScore = 
					(Float) convToFaultScore.get(convInvolved);
			
				float normalizedFaultScore;
				if (faultScoresTotal != 0.0f) {
					normalizedFaultScore =
						convFaultScore.floatValue() / faultScoresTotal;
				} else {
					normalizedFaultScore = 0.0f;
				}
				
				Float newConvFaultScore = new Float(normalizedFaultScore);
				
				convToFaultScore.put(convInvolved, newConvFaultScore);
			}

			//return chance each converter is at fault, based on fault scores.
			
			List resultCAFList = new ArrayList();
			for (int ii = 0; ii < involvedCs.length; ii++) {
				Converter involvedC = involvedCs[ii];
				
				Float convFaultScore = (Float) convToFaultScore.get(involvedC);
				
				
				ChanceAtFault resultCAF = new ChanceAtFault(failFP, involvedC,
						convFaultScore.floatValue());
				
				resultCAFList.add(resultCAF);
			}
			
			return (ChanceAtFault[]) resultCAFList.toArray(new ChanceAtFault[0]);
	}
}
