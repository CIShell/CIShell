package org.cishell.testing.convertertester.core.tester.graphcomparison;

import java.io.File;
import java.io.FileInputStream;

import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Table;
import prefuse.data.io.GraphMLReader;

/**
 * 
 * @author mwlinnem
 *
 */
public class GraphComparerTester {
	public static final String DEFAULT_GRAPHML_FILE_DIRECTORY = "/home/" +
			"mwlinnem/workspace/NWB Converter Tester/test_files/" +
			"GraphML Files/";
	
	public static void main(String[] args) {
		GraphComparer comparer = new DefaultGraphComparer();
		
		System.out.println("---Basic Tests Assuming Ids are not preserved-");
		runBasicTests(comparer, false);
		System.out.println("----------------------------------------------");
		System.out.println("---Basic Tests Assuming Ids are preserved-----");
		runBasicTests(comparer, true);
		System.out.println("----------------------------------------------");
		System.out.println("---Real Tests Assuming Ids are not preserved--");
		runRealTests(comparer, false);
		System.out.println("----------------------------------------------");
		System.out.println("---Real Tests Assuming Ids are preserved------");
		runRealTests(comparer, true);
		System.out.println("----------------------------------------------");
		System.out.println("----------*-All Tests Completed-*-------------");
	}
	
	private static void runRealTests(GraphComparer comparer,
			boolean idsPreserved) {
		//directedness test
		Graph directedness1 = loadGraph("directedness1.xml");
		Graph directedness2 = loadGraph("directedness2.xml");
		
		ComparisonResult result1 = comparer.compare(directedness1,
				directedness2, idsPreserved);
		
		System.out.println("Directedness Test (should fail) ... " + result1);
		
		//nodecount test
		Graph nodeCount1 = loadGraph("nodecount1.xml");
		Graph nodeCount2 = loadGraph("nodecount2.xml");
		
		ComparisonResult result2 = comparer.compare(nodeCount1, nodeCount2,
				idsPreserved);
		
		System.out.println("Node Count Test (should fail) ... " + result2);
		
		//edgecount test
		Graph edgeCount1 = loadGraph("edgecount1.xml");
		Graph edgeCount2 = loadGraph("edgecount2.xml");
		
		ComparisonResult result3 = comparer.compare(edgeCount1, edgeCount2,
				idsPreserved);
		
		System.out.println("Edge Count Test (should fail) ... " + result3);	

	
		//neighbor test 1
		Graph neighbor1 = loadGraph("neighbor1.xml");
		Graph neighbor2 = loadGraph("neighbor2.xml");
		
		ComparisonResult result4 = comparer.compare(neighbor1, neighbor2,
				idsPreserved);
		
		System.out.println("Neighbor Test 1 (should fail, but will be missed if idsNotPreserved) ..." + result4);
		
		//neighbor test 2
		Graph neighbor3 = loadGraph("neighbor3.xml");
		Graph neighbor4 = loadGraph("neighbor4.xml");
		
		ComparisonResult result5 = comparer.compare(neighbor3, neighbor4,
				idsPreserved);
		
		System.out.println("Neighbor Test 2 (should pass) ..." + result5);
		
		//neighbor test 3
		Graph neighbor5 = loadGraph("neighbor5.xml");
		Graph neighbor6 = loadGraph("neighbor6.xml");
		
		ComparisonResult result6 = comparer.compare(neighbor5, neighbor6,
				idsPreserved);
		
		System.out.println("Neighbor Test 3 (should pass) ..." + result6);
		
		//edgeFrequency test 1
		Graph frequency1 = loadGraph("edgefrequency1.xml");
		Graph frequency2 = loadGraph("edgefrequency2.xml");
		
		ComparisonResult result7 = comparer.compare(frequency1, frequency2,
				idsPreserved);
		
		System.out.println("Degree frequency test 1 (should fail, but won't " +
				"on idsNotPreserved)" + result7);
		
		//edgeFrequency test2
		
		Graph frequency3 = loadGraph("edgefrequency3.xml");
		Graph frequency4 = loadGraph("edgefrequency4.xml");
		
		ComparisonResult result8 = comparer.compare(frequency3, frequency4,
				idsPreserved);
		
		System.out.println("Degree frequency test 2 (Fail if IdsPreserved) " +
				result8);
		
		//nodeAttributes test 1
		Graph nodeAttr1 = loadGraph("nodeattributes1.xml");
		Graph nodeAttr2 = loadGraph("nodeattributes2.xml");
		
		ComparisonResult result9 = comparer.compare(nodeAttr1, nodeAttr2,
				idsPreserved);
		
		System.out.println("Node Attributes Test 1 (should fail) ... " + 
				result9);
		
		//nodeAttributes test 2
		Graph nodeAttr3 = loadGraph("nodeattributes3.xml");
		Graph nodeAttr4 = loadGraph("nodeattributes4.xml");
		
		ComparisonResult result10 = comparer.compare(nodeAttr3, nodeAttr4, 
				idsPreserved);
		
		System.out.println("Node Attributes Test 2 (should fail but may " +
				"pass on ids not preserved) " + result10);
		
		//edgeAttributes
		Graph edgeAttr1 = loadGraph("edgeattributes1.xml");
		Graph edgeAttr2 = loadGraph("edgeattributes2.xml");
		
		ComparisonResult result11 = comparer.compare(edgeAttr1, edgeAttr2,
				idsPreserved);
		
		System.out.println("Edge Attributes Test (should fail) ... " + 
				result11);
	}	
	
	private static void runBasicTests(GraphComparer comparer,
			boolean idsPreserved) {
//		setup
		Schema edgeTableSchema = new Schema();
		edgeTableSchema.addColumn(Graph.DEFAULT_SOURCE_KEY, Integer.class);
		edgeTableSchema.addColumn(Graph.DEFAULT_TARGET_KEY, Integer.class);
		
		//test1
		Graph emptyGraph1 = new Graph();
		Graph emptyGraph2 = new Graph();
		
		ComparisonResult result1 = comparer.compare(emptyGraph1, 
				emptyGraph2, false);	
		System.out.println("Empty undirected graph test ... " + result1);
		
		//test2
		Graph directedGraph1 = new Graph(idsPreserved);
		Graph directedGraph2 = new Graph(idsPreserved);
		
		ComparisonResult result2 = comparer.compare(directedGraph1,
				directedGraph2, true);		
		System.out.println("Empty directed graph test ... " + result2);
		
		//test3
		Table nodeTable1 = new Table();
		nodeTable1.addRows(10);
		
		Table nodeTable2 = new Table();
		nodeTable2.addRows(10);
		
		Graph noEdgeGraph1 = new Graph(nodeTable1, idsPreserved);
		Graph noEdgeGraph2 = new Graph(nodeTable2, idsPreserved);
		
		ComparisonResult result3 = comparer.compare(noEdgeGraph1,
				noEdgeGraph2, false);
		System.out.println("No edge graph test ... " + result3);
		
		//test4 (should fail)
		Table nodeTable3 = new Table();
		nodeTable3.addRows(11);
		
		Graph noEdgeGraph3 = new Graph(nodeTable3, idsPreserved);
		
		ComparisonResult result4 = comparer.compare(noEdgeGraph1,
				noEdgeGraph3, false);
		System.out.println("No edge graph test 2 (should fail) ... " + result4);
	}
	

	private static Graph loadGraph(String fileName) {
		return loadGraph(DEFAULT_GRAPHML_FILE_DIRECTORY, fileName);
	}
	
	private static Graph loadGraph(String directoryPath, String fileName) {
		File fileHandler = new File(directoryPath + fileName);
		try {
		Graph graph= (new GraphMLReader()).readGraph(new FileInputStream(fileHandler));
		return graph;
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
			return null; //makes Eclipse happy
		}

	}
}
