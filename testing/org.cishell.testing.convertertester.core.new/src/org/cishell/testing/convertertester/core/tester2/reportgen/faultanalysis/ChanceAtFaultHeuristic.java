package org.cishell.testing.convertertester.core.tester2.reportgen.faultanalysis;

import org.cishell.testing.convertertester.core.converter.graph.Converter;
import org.cishell.testing.convertertester.core.tester2.reportgen.results.FilePassResult;

//TODO: make partial trust heuristic

public interface ChanceAtFaultHeuristic {
	
	/**
	 * Determines how likely each involved converter is to be the cause 
	 * of the provided file passes failure.
	 * @param fp The file pass the failed
	 * @param involvedCs all the converters in the file pass up through the 
	 * converter that failed (can be null if it failed during graph comparison)
	 * @param trustedCs converters known to be trusted (for what it's worth)
	 * @return an array of objects, where each object is the pair of a
	 * converter involved in the failure, and how likely it is to have been
	 * at fault.
	 */
	public ChanceAtFault[] determine(FilePassResult fp, Converter[] involvedCs,
			Converter[] trustedCs);
}
