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
import prefuse.data.Table;
import prefuse.data.Tuple;
import prefuse.util.collections.IntIterator;

	public class IdsPreservedComparer extends ComplexGraphComparer {
		
		private RunningLog log;
		
		public ComparisonResult compare(Graph g1, Graph g2) {
			super.clearLog();
			this.log = super.getLog();
			
			ComparisonResult simpleCompareResult = super.compare(g1, g2);
			
			if (! simpleCompareResult.comparisonSucceeded()) {
				return simpleCompareResult;
			}
			
			log.append(simpleCompareResult.getLog());
		
			if (! areEqual(g1, g2,  true))  {
				log.prepend("Graphs do not have the same contents");
				return new ComparisonResult(false, log);	
			}
				
		//all tests passed
			log.prepend("All comparison tests succeeded.");
			return new ComparisonResult(true, log);
		}
		
		/*
		 * These methods do what .equals() should do for their respective objects:
		 * Actually compare the contents to see if they are .equals() to each
		 * other. The default methods instead appear to be doing a memory 
		 * location comparison.
		 */

		private boolean areEqual(Graph g1, Graph g2, boolean sort) {
			Table nodeTable1 = g1.getNodeTable();
			Table nodeTable2 = g2.getNodeTable();
			
			if (sort) {
				if (! areEqualWhenSorted(nodeTable1, nodeTable2))
					return false;
			} else {
				if (! areEqual(nodeTable1, nodeTable2))
					return false;
			}
			
			Table edgeTable1 = g1.getEdgeTable();
			Table edgeTable2 = g2.getEdgeTable();
			
			if (sort) {
				if (! areEqualWhenSorted(edgeTable1, edgeTable2)) 
					return false;
			} else {
				if (! areEqual(edgeTable1, edgeTable2)) 
					return false;
			}
			
			return true;
		}
}
