package org.cishell.testing.convertertester.core.tester2.graphcomparison;

import org.cishell.testing.convertertester.core.tester.graphcomparison.ComparisonResult;
import org.cishell.testing.convertertester.core.tester.graphcomparison.RunningLog;

import prefuse.data.Graph;

public class LossyComparer extends SimpleGraphComparer {
	
	
	private RunningLog log;
	
	/**
	 * Assuming it isn't so lossy that it loses edges or nodes.
	 */
	public ComparisonResult compare(Graph g1, Graph g2) {
		this.log = new RunningLog();
		
		if (g1 == null || g2 == null) {
			return new ComparisonResult(false, "At least one of the provided" +
					" graphs was null.", log);
		}

		if (! isEqualNodeCount(g1, g2)) {
			return new ComparisonResult(false, "Node counts not equal.", log);
		} else if (! isEqualEdgeCount(g1, g2)) {
			return new ComparisonResult(false, "Edge counts not equal.", log);
		}
		
		//all tests succeeded.
		return new ComparisonResult(true, log);
	}
	
	
}
