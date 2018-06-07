package org.cishell.testing.convertertester.core.tester.graphcomparison;

import prefuse.data.Graph;

public interface GraphComparer {
	public ComparisonResult compare(Graph originalGraph,
			Graph convertedGraph, boolean idsPreserved);
}
