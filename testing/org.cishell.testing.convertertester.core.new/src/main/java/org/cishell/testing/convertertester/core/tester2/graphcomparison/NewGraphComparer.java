package org.cishell.testing.convertertester.core.tester2.graphcomparison;

import org.cishell.testing.convertertester.core.tester.graphcomparison.ComparisonResult;

import prefuse.data.Graph;

public interface NewGraphComparer {

	public ComparisonResult compare(Graph g1, Graph g2);
}
