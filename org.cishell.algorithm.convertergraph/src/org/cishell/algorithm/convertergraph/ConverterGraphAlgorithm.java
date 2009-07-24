package org.cishell.algorithm.convertergraph;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * This plugin collects information about all the active converters in the tool and 
 * outputs a NWB file having this Directed network. Also nodes corresponding to each
 * MIME/TYPE are weighted depending upon how many times they participate in a converter
 * relationship.
 *  
 * @author Chintan Tank
 */

public class ConverterGraphAlgorithm implements Algorithm {

	private LogService logger;
	private BundleContext bundleContext;
	
	private int nodeCount, edgeCount;

    /**
     * Construct with the appropriate parameters.
     * @param ciShellContext 
     * @param bundleContext  
     * @throws AlgorithmExecutionException 
     */
    public ConverterGraphAlgorithm(Data[] data, Dictionary parameters, 
    		CIShellContext ciShellContext, BundleContext bundleContext) {
        this.bundleContext = bundleContext; 
		this.logger = (LogService) ciShellContext.getService(LogService.class.getName());
    }

    public Data[] execute() throws AlgorithmExecutionException {
		
			try {
				
				/*
				 * Get all the converter service references currently in service.
				 * */
				ServiceReference[] allConverterServices = getAllConverters();
				
				/*
				 * Process the references to create a network of weighted nodes & directed edges.
				 * */
				ConverterGraphComputation converterGraphComputation = 
					new ConverterGraphComputation(allConverterServices, logger);
				
				/*
				 * Used to generate the output file containing the network. 
				 * */
				File outputNWBFile = createOutputGraphFile(converterGraphComputation);
				
				return prepareOutputMetadata(new BasicData(outputNWBFile, "file:text/nwb"));
			
			} catch (IOException e) {
				throw new AlgorithmExecutionException(e);
			} 
	}

	/**
	 * Call all the NWB File Handler processes in order to create a NWB file.
	 * @param converterGraphComputation
	 * @return
	 * @throws IOException
	 */
	private File createOutputGraphFile(
			ConverterGraphComputation converterGraphComputation)
			throws IOException {
		
		File outputNWBFile = File.createTempFile("nwb-", ".nwb");
		
		ConverterGraphOutputGenerator outputGenerator = new ConverterGraphOutputGenerator(
				converterGraphComputation, outputNWBFile);
		
		outputGenerator.addComment("Graph of all Converters in the Tool.");
		
		nodeCount = converterGraphComputation.nodes.size();
		outputGenerator.setNodeCount(nodeCount);
		
		outputGenerator.setNodeSchema(converterGraphComputation.nodeSchema);
		
		/*
		 * Print all the node rows to the output file.
		 * */
		setNodeTuples(converterGraphComputation, outputGenerator);
		
		edgeCount = converterGraphComputation.edges.size();
		outputGenerator.setDirectedEdgeCount(edgeCount);
		
		outputGenerator.setDirectedEdgeSchema(converterGraphComputation.edgeSchema);
		
		/*
		 * Print all the edge rows to the output file. 
		 * */
		setDirectedEdgeTuples(converterGraphComputation, outputGenerator);
		
		outputGenerator.finishedParsing();
		outputGenerator.haltParsingNow();
		return outputNWBFile;
	}

	/**
	 * Iterate through the nodes and print it into the output file. 
	 * @param converterGraphComputation
	 * @param outputGenerator
	 */
	private void setDirectedEdgeTuples(
			ConverterGraphComputation converterGraphComputation,
			ConverterGraphOutputGenerator outputGenerator) {
		
		for (Iterator edgeIterator = converterGraphComputation.edges.iterator(); 
				edgeIterator.hasNext();) {
			
			Edge edge = (Edge) edgeIterator.next();
			int sourceNode = edge.source;
			int targetNode = edge.target;
			final String converterName = edge.serviceShortPID;
			final String servicePID = edge.serviceCompletePID;
				
			outputGenerator.addDirectedEdge(sourceNode, targetNode, new HashMap() {{
				put("converter_name", converterName);
				put("service_pid", servicePID);
			}});
		}
	}

	/**
	 * @param converterGraphComputation
	 * @param outputGenerator
	 */
	private void setNodeTuples(
			ConverterGraphComputation converterGraphComputation,
			ConverterGraphOutputGenerator outputGenerator) {
		
		for (Iterator nodeIterator = converterGraphComputation.nodes.entrySet().iterator(); 
				nodeIterator.hasNext();) {
			
			Map.Entry node = (Entry) nodeIterator.next();
			
			int nodeID = ((Node) node.getValue()).id;
			final int strength = ((Node) node.getValue()).strength;
			String label = node.getKey().toString();
				
			outputGenerator.addNode(nodeID, label, new HashMap() {{
				put("strength", strength);
			}});
		}
	}
    
	
	/**
	 * Gets all the converter service references based on the LDAP query. 
	 * @return
	 */
	private ServiceReference[] getAllConverters() {
		try {
			ServiceReference[] allConverters = 
				bundleContext.getAllServiceReferences(
					AlgorithmFactory.class.getName(),
					"(&(type=converter))");
			
			if (allConverters == null) {
				/*
				 * better to return a list of length zero than null
				 * */
				
				allConverters = new ServiceReference[]{};
			}
			
			return allConverters;
			
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return new ServiceReference[]{};	
		}
	}

	/**
	 * Prepares output metadata to be displayed to the user.
	 * @param outNWBData
	 */
	private Data[] prepareOutputMetadata(Data outNWBData) {
		outNWBData.getMetadata().put(DataProperty.LABEL, "Converter Graph having " 
				+ nodeCount + " nodes & " + edgeCount + " edges.");
		outNWBData.getMetadata().put(DataProperty.TYPE, DataProperty.NETWORK_TYPE);
		return new Data[]{outNWBData};
	}
}
