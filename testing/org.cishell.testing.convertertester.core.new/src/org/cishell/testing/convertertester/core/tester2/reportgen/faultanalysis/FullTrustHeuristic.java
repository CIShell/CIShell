package org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.ConvResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.filepass.FilePassFailure;

/**
 * @author mwlinnem
 *
 * Assumes all trusted converters are incapable of being at fault,
 * and all other involved converters are equally likely to be at fault.
 */
public class FullTrustHeuristic implements ChanceAtFaultHeuristic {

	public ChanceAtFault[] determine(FilePassResult failFP,
			Converter[] involvedCs,
			Converter[] trustedCs) {
		
		//easier to deal with as a list
		List trustedCList = Arrays.asList(trustedCs);
		
		//eliminate converters involved twice in the file pass
		
		Set uniqueInvolvedCs = new HashSet();
		for (int ii = 0; ii < involvedCs.length; ii++) {
			uniqueInvolvedCs.add(involvedCs[ii]);
		}
		//eliminate converters that are trusted
		
		List uniqueUntrustedCs = new ArrayList();
		Iterator iter = uniqueInvolvedCs.iterator();
		while (iter.hasNext()) {
			Converter c  = (Converter) iter.next();
			if (! trustedCList.contains(c)) {
				//converter isn't trusted
				//add it to the list
				uniqueUntrustedCs.add(c);
			} else {
				//converter is trusted
				//do nothing
			}
		}
		
		float chanceEachAtFault = 1.0f / uniqueUntrustedCs.size();
		
		List chanceAtFaultList = new ArrayList();
		for (int ii = 0; ii < involvedCs.length; ii++) {
			Converter c = involvedCs[ii];
			
			ChanceAtFault chanceAtFault = null;
			if (uniqueUntrustedCs.contains(c)) {
				chanceAtFault = 
					new ChanceAtFault(failFP, c, chanceEachAtFault);
			} else {
				chanceAtFault = 
					new ChanceAtFault(failFP, c, 0.0f);
			}
			
			chanceAtFaultList.add(chanceAtFault);
		}
		
		return (ChanceAtFault[]) 
			chanceAtFaultList.toArray(new ChanceAtFault[0]);
	}

}
