package org.cishell.testing.convertertester.core.tester2.graphcomparison;



import org.cishell.testing.convertertester.core.tester.graphcomparison.ComparisonResult;
import org.cishell.testing.convertertester.core.tester.graphcomparison.RunningLog;

import prefuse.data.Graph;

public class SimpleGraphComparer implements NewGraphComparer {

	private RunningLog log;
	
	public ComparisonResult compare(Graph g1, Graph g2) {
		this.log = new RunningLog();
		
		if (g1 == null || g2 == null) {
			log.prepend("At least one of the provided graphs was null");
			return new ComparisonResult(false, log);
		}
		//basic tests	
		if (! isSameDirectedness(g1, g2)) {
			log.prepend("Directedness not of the same type");
			return new ComparisonResult(false, log);
		} else if (! isEqualNodeCount(g1, g2)) {
			log.prepend("Node counts not equal.");
			return new ComparisonResult(false, log);
		} else if (! isEqualEdgeCount(g1, g2)) {
			log.prepend("Edge counts not equal");
			return new ComparisonResult(false, log);
		}
		
		//all tests succeeded.
		return new ComparisonResult(true, log);
	}
	
	protected boolean isSameDirectedness(Graph g1, Graph g2) {
		boolean result = g1.isDirected() == g2.isDirected();
		return result;
	}
	
	protected boolean isEqualNodeCount(Graph g1, Graph g2) {
		boolean result =  g1.getNodeCount() == g2.getNodeCount();
		return result;
	}
	
	protected boolean isEqualEdgeCount(Graph g1, Graph g2) {
		boolean result =  g1.getEdgeCount() == g2.getEdgeCount();
		return result;
	}
}
