package org.cishell.algorithm.convertergraph;

import java.util.Dictionary;

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

import edu.uci.ics.jung.graph.Graph;

/**
 * This plugin collects information about all the active converters in the tool and 
 * outputs a JUNG Graph having this Directed network. Also nodes corresponding to each
 * MIME/TYPE are weighted depending upon how many times they participate in a converter
 * relationship.
 *  
 * @author Chintan Tank
 */

public class ConverterGraphAlgorithm implements Algorithm {

	private LogService logger;
	private BundleContext bundleContext;
	
	private Graph outputGraph;
	
	private int nodeCount = 0, edgeCount = 0;

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
			 * Used to generate the output reference for graph containing the network. 
			 * */
			return prepareOutputMetadata(new BasicData(converterGraphComputation.getOutputGraph(), 
										 Graph.class.getName())); 
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
				+ ((Graph) outNWBData.getData()).numVertices() + " nodes & " 
				+ ((Graph) outNWBData.getData()).numEdges() + " edges.");
		outNWBData.getMetadata().put(DataProperty.TYPE, DataProperty.NETWORK_TYPE);
		return new Data[]{outNWBData};
	}
}
