package org.cishell.testing.convertertester.core.tester2.graphcomparison;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cishell.testing.convertertester.core.tester.graphcomparison.ComparisonResult;
import org.cishell.testing.convertertester.core.tester.graphcomparison.TableUtil;
import org.cishell.testing.convertertester.core.tester.graphcomparison.RunningLog;

import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.util.collections.IntIterator;

public class IdsNotPreservedComparer extends ComplexGraphComparer {
	
	private RunningLog log;
	
	public ComparisonResult compare(Graph g1, Graph g2) {
		super.clearLog();
		this.log = super.getLog();
		ComparisonResult simpleCompareResult = super.compare(g1, g2);
		
		if (! simpleCompareResult.comparisonSucceeded()) {
			return simpleCompareResult;
		}
		
		log.append(simpleCompareResult.getLog());
		
		if (! nodeDegreeFrequenciesEqual(g1, g2)) {
			log.prepend("The number of nodes" +
					"with a certain number of edges is not the same in" +
					"both graphs.");
			return new ComparisonResult(false, log);
		}
		
		/*
		 * TODO: we could really use a graph isomorphism comparison right
		 * here. nodeDegreeFrequencies will catch some errors, but lets
		 * a lot through.
		 */	
		
		if (! haveSameNodeAttributes(g1, g2)) {
			log.prepend("Node attributes are not " +
					"the same in both graphs.");
			return new ComparisonResult(false, log);
		}
		
		if (! haveSameEdgeAttributes(g1, g2)) {
			log.prepend("Edge attributes are not " +
					"the same in both graphs.");
			return new ComparisonResult(false, log);
		}
	
	//all tests passed
		
		log.prepend("All comparison tests succeeded");
		return new ComparisonResult(true, log);
	}
	
	/*
	 * Tests whether there are an equal numbers of nodes with the same 
	 * number of edges in each graph, e.g. 5 nodes with 1 edge, 12 nodes
	 * with 2 edges etc.. .
	 * 
	 * Possibly useful when graph IDs are modified by the conversion.
	 */
	private boolean nodeDegreeFrequenciesEqual(Graph g1, Graph g2) {
		Set e1 = getNodeDegreeFrequencies(g1);
		Set e2 = getNodeDegreeFrequencies(g2);
		
		boolean result = e1.equals(e2);
		return result;
	}
	
	/*
	 * Helper method for nodeDegreeFrequenciesEqual
	 */
	private Set getNodeDegreeFrequencies(Graph g) {
		Map nodeDegreeFrequencies 
			= new HashMap();
		
		/*
		 * TODO: (might want to shortcut all of this by counting from 0 to 
		 * numberOfNodes)
		 */
		Table nodeTable = g.getNodeTable();
		for (IntIterator ii = nodeTable.rows(); ii.hasNext();) {
			int nodeID = ii.nextInt();
			Node node = g.getNode(nodeID);
			
			int numEdges = g.getInDegree(node) + g.getOutDegree(node);
			
			Integer currentFrequency = 
				(Integer) nodeDegreeFrequencies.get(new Integer(numEdges));
			if (currentFrequency == null) { 
				/*
				 * A node with this number of edges has not been recorded yet,
				 * so we set the number of occurrences to one.
				 */
				nodeDegreeFrequencies.put(new Integer(numEdges),
						new Integer(1));
			} else {
				/*
				 * A node with this number of edges has been recorded, so
				 * we increment the number of occurrences by one.
				 */
				nodeDegreeFrequencies.put(new Integer(numEdges),
						currentFrequency);
			}
		}

		//convert the result to a more usable format.
		Set nodeFrequencyPairs 
			= nodeDegreeFrequencies.entrySet();
		
		return nodeFrequencyPairs;
	}

	
	private boolean haveSameNodeAttributes(Graph g1, Graph g2) {
		Table t1 = getStrippedNodeTable(g1);
		Table t2 = getStrippedNodeTable(g2);
		boolean result = areEqualWhenSorted(t1, t2);
		return result;
	}

	/*
	 * Determines whether the two graphs have the same edge attributes.
	 * That is, for every edge in table A there is an edge in table B with
	 * the exactly the same attribute values, and vice versa. Has no regard 
	 * for source and target IDs, or the order the edgesappear in the edge 
	 * tables.
	 */
	private boolean haveSameEdgeAttributes(Graph g1, Graph g2) {
		//remove the IDs
		Table t1 = getStrippedEdgeTable(g1.getEdgeTable());
		Table t2 = getStrippedEdgeTable(g2.getEdgeTable());
				
		boolean result = areEqualWhenSorted(t1, t2);
		return result;
	}
	
	

	/**
	 * Removes source and target columns from a copied version of the table.
	 * 
	 * Helper method for haveSameEdgeAttributes
	 * 
	 * @param t the original table
	 * @return a stripped copy of the original table
	 */
	private Table getStrippedEdgeTable(Table t) {
		Table tCopy = TableUtil.copyTable(t);
		tCopy.removeColumn(Graph.DEFAULT_SOURCE_KEY);
		tCopy.removeColumn(Graph.DEFAULT_TARGET_KEY);
		return tCopy;
	}
	
	private Table getStrippedNodeTable(Graph g) {
		Table tCopy = TableUtil.copyTable(g.getNodeTable());
		String nodeKeyField = g.getNodeKeyField();
		if (nodeKeyField != null) {
			tCopy.removeColumn(nodeKeyField);
		}
		return tCopy;
	}	
	
	
}
