package org.cishell.algorithm.convertergraph;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.iu.nwb.util.nwbfile.NWBFileParserHandler;
import edu.iu.nwb.util.nwbfile.NWBFileWriter;

public class ConverterGraphOutputGenerator implements NWBFileParserHandler {

	private NWBFileWriter output;
	
	public ConverterGraphOutputGenerator(ConverterGraphComputation converterGraphComputation,
			File outputNWBFile) throws IOException {
		output = new NWBFileWriter(outputNWBFile); 
	}

	public void setNodeCount(int numberOfNodes) {
		output.setNodeCount(numberOfNodes);
	}
	
	public void setNodeSchema(LinkedHashMap schema) {
		output.setNodeSchema(schema);
	}
	
	public void addNode(int id, String label, Map attributes) {
		output.addNode(id, label, attributes);
	}
	
	public void addDirectedEdge(int sourceNode, int targetNode, Map attributes) {
		output.addDirectedEdge(sourceNode, targetNode, attributes);
	}
	public void addUndirectedEdge(int node1, int node2, Map attributes) {
		output.addUndirectedEdge(node1, node2, attributes);
	}
	public void setDirectedEdgeCount(int numberOfEdges) {
		output.setDirectedEdgeCount(numberOfEdges);
	}
	public void setDirectedEdgeSchema(LinkedHashMap schema) {
		output.setDirectedEdgeSchema(schema);
	}
	public void setUndirectedEdgeCount(int numberOfEdges) {
		output.setUndirectedEdgeCount(numberOfEdges);
	}
	public void setUndirectedEdgeSchema(LinkedHashMap schema) {
		output.setUndirectedEdgeSchema(schema);
	}

	public void addComment(String comment) {
		output.addComment(comment);
	}
	
	public void finishedParsing() {
		output.finishedParsing();
	}

	public boolean haltParsingNow() {
		return false;
	}
}
