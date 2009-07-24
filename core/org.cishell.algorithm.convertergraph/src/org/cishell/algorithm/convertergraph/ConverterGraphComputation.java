/**
 * 
 */
package org.cishell.algorithm.convertergraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import edu.iu.nwb.util.nwbfile.NWBFileProperty;

/**
 * The algorithm is used for getting information about current converters is,
 * 1. Get all the service references for converters in the system.
 * 2. Create Node Schema to be used when constructing the NWB file. In this case
 * it will be,
 * 		id*int, label*string, strength*int
 * 3. Create Edge Schema to be used when constructing the NWB file. In this case
 * it will be,
 * 		source*int, target*int, converter_name*string, service_pid*string
 * 4. Iterate over all the converter service reference one at a time.
 * 5. For collecting information for Nodes do,
 * 		(a). Get node label using service properties in_data & out_data.
 * 		(b). Check to see if this node is already present in the nodes map.
 * 		(c). If it is present then just update the strength of node by incrementing
 * 			that node's strength by 1.
 * 		(d). Else create a new node. Provide it a default strength of 1 and update 
 * 			the node count.  
 * 6. For collecting information about Edges do,
 * 		(a). Get the respective source & target node ids from the recently updates 
 * 			respective nodes.
 * 		(b). Get the service_pid by using service property service.pid.
 * 		(c). Get the converter_name by extracting the last block from service_pid.
 * 7. These information is then passed on to {@link ConverterGraphOutputGenerator} 
 * 		for printing it into a NWB file.
 *  
 * @author cdtank
 *
 */
public class ConverterGraphComputation {

	private LogService logger;
	private ServiceReference[] allConverterServices;
	
	public Map nodes = new HashMap();
	public LinkedHashMap nodeSchema = new LinkedHashMap();
	
	public List edges = new ArrayList();
	public LinkedHashMap edgeSchema = new LinkedHashMap();
	
	private int nodeCount;

	public ConverterGraphComputation(ServiceReference[] allConverterServices,
			LogService logger) {
		
		this.nodeCount = 0;
		this.logger = logger;
		this.allConverterServices = allConverterServices;
		
		/*
		 * Side affects nodeSchema
		 * */
		createNodeSchema();
		
		/*
		 * Side affects edgeSchema
		 * */
		createEdgeSchema();
		
		/*
		 * Side affects nodes & edges
		 * */
		processServiceReferences();
	}

	private void createNodeSchema() {
		nodeSchema.put("id", NWBFileProperty.TYPE_INT);
		nodeSchema.put("label", NWBFileProperty.TYPE_STRING);
		nodeSchema.put("strength", NWBFileProperty.TYPE_INT);
	}
	
	private void createEdgeSchema() {
		edgeSchema.put("source", NWBFileProperty.TYPE_INT);
		edgeSchema.put("target", NWBFileProperty.TYPE_INT);
		edgeSchema.put("converter_name", NWBFileProperty.TYPE_STRING);
		edgeSchema.put("service_pid", NWBFileProperty.TYPE_STRING);
	}



	/*
	 * Iterate over all the converter service references and process the 
	 * information to get nodes & edges.
	 * */
	private void processServiceReferences() {
		
		for (int converterCount = 0; 
				converterCount < allConverterServices.length; 
				converterCount++) {

			int sourceNodeID, targetNodeID;

			ServiceReference currentConverterServiceReference = 
				allConverterServices[converterCount];

			String sourceNodeKey = 
				(String) currentConverterServiceReference.getProperty("in_data");
			String targetNodeKey = 
				(String) currentConverterServiceReference.getProperty("out_data");
			
			if (nodes.containsKey(sourceNodeKey)) {
				sourceNodeID = updateNode(sourceNodeKey); 
			} else {
				sourceNodeID = createNode(sourceNodeKey);
			}
			
			if (nodes.containsKey(targetNodeKey)) {
				targetNodeID = updateNode(targetNodeKey);
			} else {
				targetNodeID = createNode(targetNodeKey);
			}
			
			createEdge(sourceNodeID, targetNodeID, currentConverterServiceReference);
		}
	}
	
	private int updateNode(String currentNodeKey) {
		int sourceNodeID;
		Node sourceNodeValue = (Node) nodes.get(currentNodeKey);
		sourceNodeID = sourceNodeValue.id;
		sourceNodeValue.strength += 1;
		return sourceNodeID;
	}

	private int createNode(String nodeKey) {
		nodeCount++;
		Node nodeValue = new Node();
		nodeValue.id = nodeCount;
		nodeValue.strength = 1;
		
		nodes.put(nodeKey, nodeValue);
		return nodeCount;
	}

	/**
	 * Create an edge based on source id, target id & other information.
	 * @param sourceNodeID
	 * @param targetNodeID
	 * @param currentConverterServiceReference
	 */
	private void createEdge(int sourceNodeID, int targetNodeID,
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
		 * Build the actual edge tuple.
		 * */
		Edge edge = new Edge();
		edge.source = sourceNodeID;
		edge.target = targetNodeID;
		edge.serviceShortPID = serviceShortPID;
		edge.serviceCompletePID = serviceCompletePID;
		
		edges.add(edge);
	}

}
