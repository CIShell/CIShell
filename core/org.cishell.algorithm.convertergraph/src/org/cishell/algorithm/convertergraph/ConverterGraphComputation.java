/**
 * 
 */
package org.cishell.algorithm.convertergraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.utils.UserDataContainer;

/**
 * The algorithm is used for getting information about current converters is,
 * 1. Get all the service references for converters in the system.
 * 2. Node Schema to be used is,
 * 		id*int, label*string, strength*int
 * 3. Edge Schema to be used is, 
 * 		source*int, target*int, converter_name*string, service_pid*string
 * 4. Iterate over all the converter service reference one at a time.
 * 5. For collecting information for Nodes do,
 * 		(a). Get node label using service properties in_data & out_data.
 * 		(b). Check to see if this node is already present in the nodeLabels set.
 * 		(c). If it is present then just update the strength of node by incrementing
 * 			that node's strength by 1.
 * 		(d). Else create a new node. Provide it a default strength of 1 and update 
 * 			the node count. Also create an entry in the nodeLabels for future 
 * 			reference. 
 * 6. For collecting information about Edges do,
 * 		(a). Get the respective source & target node references from the recently updated 
 * 			nodes.
 * 		(b). Get the service_pid by using service property service.pid.
 * 		(c). Get the converter_name by extracting the last block from service_pid.
 * 7. Since the graph is being created during the processing of each node & edge
 * 	it just needs to be referenced on the UI. This is done by outputting the metadata. 
 *  
 * @author cdtank
 *
 */
public class ConverterGraphComputation {

	private LogService logger;
	private ServiceReference[] allConverterServices;
	
	private Set nodeLabels = new HashSet();
	
	private Graph outputGraph;
	
	/**
	 * @return the outputGraph
	 */
	public Graph getOutputGraph() {
		return outputGraph;
	}

	private int nodeCount;

	public ConverterGraphComputation(ServiceReference[] allConverterServices,
			LogService logger) {
		
		this.nodeCount = 0;
		this.logger = logger;
		this.allConverterServices = allConverterServices;
		this.outputGraph = new DirectedSparseGraph();
		
		/*
		 * Side affects nodes & edges
		 * */
		processServiceReferences();
	}

	/*
	 * Iterate over all the converter service references and process the 
	 * information to get nodes & edges.
	 * */
	private void processServiceReferences() {
		
		for (int converterCount = 0; 
				converterCount < allConverterServices.length; 
				converterCount++) {

			Vertex sourceNode, targetNode;

			ServiceReference currentConverterServiceReference = 
				allConverterServices[converterCount];

			String sourceNodeKey = 
				(String) currentConverterServiceReference.getProperty("in_data");
			String targetNodeKey = 
				(String) currentConverterServiceReference.getProperty("out_data");
			
			if (nodeLabels.contains(sourceNodeKey)) {
				sourceNode = updateNode(sourceNodeKey); 
			} else {
				sourceNode = createNode(sourceNodeKey);
			}
			
			if (nodeLabels.contains(targetNodeKey)) {
				targetNode = updateNode(targetNodeKey);
			} else {
				targetNode = createNode(targetNodeKey);
			}
			
			createEdge(sourceNode, targetNode, currentConverterServiceReference);
		}
	}
	
	private Vertex updateNode(String currentNodeKey) {
		
		for (Iterator nodeIterator = outputGraph.getVertices().iterator(); 
				nodeIterator.hasNext();) {
			Vertex currentVertex = (Vertex) nodeIterator.next();
			if (currentVertex.getUserDatum("label").toString()
					.equalsIgnoreCase(currentNodeKey)) {
				int currentVertexStrength = 
					((Integer) currentVertex.getUserDatum("strength")).intValue();
				currentVertex.setUserDatum("strength", new Integer(++currentVertexStrength), 
						new UserDataContainer.CopyAction.Shared());
				return currentVertex;
			}
		}
		return new DirectedSparseVertex();
	}

	private Vertex createNode(String nodeKey) {
		nodeCount++;
		
		Vertex node = new DirectedSparseVertex();
		node.addUserDatum("strength", new Integer(1), new UserDataContainer.CopyAction.Shared());
		node.addUserDatum("label", nodeKey, new UserDataContainer.CopyAction.Shared());
		
		outputGraph.addVertex(node);
		nodeLabels.add(nodeKey);
		
		return node;
	}

	/**
	 * Create an edge based on source id, target id & other information.
	 * @param sourceNode
	 * @param targetNode
	 * @param currentConverterServiceReference
	 */
	private void createEdge(Vertex sourceNode, Vertex targetNode,
			ServiceReference currentConverterServiceReference) {
		String serviceCompletePID = 
			(String) currentConverterServiceReference.getProperty("service.pid");
		
		/*
		 * Converter name is placed in the last block of service.pid. This is used
		 * to extract it.
		 * */
		int startIndexForConverterName = serviceCompletePID.lastIndexOf(".") + 1;
		String serviceShortPID = serviceCompletePID.substring(startIndexForConverterName);
		
		/*
		 * Build the actual edge & attach it to the graph.
		 * */
		Edge edge = new DirectedSparseEdge(sourceNode, targetNode);
		
		edge.addUserDatum("converter_name", serviceShortPID, 
				new UserDataContainer.CopyAction.Shared());
		edge.addUserDatum("service_pid", serviceCompletePID, 
				new UserDataContainer.CopyAction.Shared());
		
		outputGraph.addEdge(edge);
	}

}
