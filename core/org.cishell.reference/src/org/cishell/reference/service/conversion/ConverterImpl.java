/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 20, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.service.conversion;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.reference.service.conversion.util.ImmutableDictionary;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeProvider;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;


public class ConverterImpl implements Converter, AlgorithmFactory, AlgorithmProperty {
    private final ImmutableList<ServiceReference<AlgorithmFactory>> serviceReferences;
    private final BundleContext bContext;
    private final ImmutableDictionary<String, Object> properties;
    private final CIShellContext ciContext;
	
	private ConverterImpl(BundleContext bContext, CIShellContext ciContext, List<ServiceReference<AlgorithmFactory>> refs,
			Dictionary<String, Object> properties) {
        this.bContext = bContext;
        this.ciContext = ciContext;
        this.serviceReferences = ImmutableList.copyOf(refs);
        this.properties = ImmutableDictionary.fromDictionary(properties);
    }
	
	/**
	 * Create a converter that doesn't do anything.
	 * <p>
	 * This static factory creates a placeholder Converter that won't do anything to the data you pass it.
	 * Its input and output data format are both the supplied {@code dataFormat}.
	 * <p>
	 * Only {@link DataConversionServiceImpl} should create or deal directly with {@code ConverterImpl} 
	 * objects; other code should use the {@link Converter} interface.
	 * 
	 * @param bContext
	 * @param ciContext
	 * @param dataFormat the input and output data format
	 * @return a Converter which returns its input {@code Data} unmodified
	 */
	static ConverterImpl createNoOpConverter(BundleContext bContext, CIShellContext ciContext, String dataFormat) {
    	Dictionary<String, Object> properties = new Hashtable<String, Object>();
        properties.put(IN_DATA, dataFormat);
        properties.put(OUT_DATA, dataFormat);
        properties.put(LABEL, properties.get(IN_DATA) + " --(no-op)-> " + properties.get(OUT_DATA));
        
        properties.put(CONVERSION, LOSSLESS);
        
        ConverterImpl toReturn = new ConverterImpl(bContext, ciContext,
        		ImmutableList.<ServiceReference<AlgorithmFactory>>of(), properties);
        
        return toReturn;
    }
	
	/**
	 * Create a converter using a list of Algorithms.
	 * <p>
	 * Given a List of {@code ServiceReference<AlgorithmFactory>}, creates a {@code Converter} that will
	 * call each of the referenced Algorithms in sequence to transform its input data.
	 * <p>
	 * This factory requires that there be at least one algorithm in the chain, because it gets its
	 * input and output data formats from the inputs and outputs of the component algorithms.  If you
	 * need to create a Converter that has zero algorithms, i.e. does nothing, use the {@code createNoOpConverter}
	 * factory.
	 * 
	 * @param bContext
	 * @param ciContext
	 * @param refs the algorithms which will be called, in order, to transform the data
	 * @return a Converter for transforming data
	 * @throws IllegalArgumentException if {@code refs} is empty
	 */
    static ConverterImpl createConverter(BundleContext bContext,
			CIShellContext ciContext, List<ServiceReference<AlgorithmFactory>> refs) {
    	if (refs.size() == 0) {
    		throw new IllegalArgumentException("This static factory requires 1 or more algorithms in the chain; try .createNoOpConverter");
    	}
    	Dictionary<String, Object> properties = new Hashtable<String, Object>();
        
        properties.put(IN_DATA, refs.get(0).getProperty(IN_DATA));
        properties.put(OUT_DATA, refs.get(refs.size()-1).getProperty(OUT_DATA));
        properties.put(LABEL, properties.get(IN_DATA) + " -> " + properties.get(OUT_DATA));
        
        // TODO: Do the same thing for complexity
        String lossiness = calculateLossiness(refs);    
        properties.put(CONVERSION, lossiness);
		return new ConverterImpl(bContext, ciContext, refs, properties);
	}

	/**
     * @see org.cishell.service.conversion.Converter#convert(org.cishell.framework.data.Data)
     */
    public Data convert(Data inData) throws ConversionException {
        AlgorithmFactory factory = getAlgorithmFactory();
        Algorithm algorithm = factory.createAlgorithm(new Data[]{inData}, new Hashtable<String, Object>(), ciContext);

        Data[] resultDataArray;
        try {
        	resultDataArray = algorithm.execute();
		} catch (AlgorithmExecutionException e) {
			e.printStackTrace();
			throw new ConversionException(e.getMessage(), e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConversionException(
					"Unexpected error: " + e.getMessage(), e);
		}
        
        Object result = null;
        if (resultDataArray != null && resultDataArray.length > 0) {
        	result = resultDataArray[0].getData();
        }
        
        if (result != null) {
            Dictionary<String, Object> properties = inData.getMetadata();
            Dictionary<String, Object> newProperties = new Hashtable<String, Object>();
            
            for (Enumeration<String> propertyKeys = properties.keys(); propertyKeys.hasMoreElements();) {
                String key = propertyKeys.nextElement();
                newProperties.put(key, properties.get(key));
            }
               
            String outFormat =
            	(String) getProperties().get(AlgorithmProperty.OUT_DATA);
            
            return new BasicData(newProperties, result, outFormat);
        } else {
            return null;
        }
    }
    
    
    /**
     * @see org.cishell.service.conversion.Converter#getAlgorithmFactory()
     */
    public AlgorithmFactory getAlgorithmFactory() {
        return this;
    }

    /**
     * @see org.cishell.service.conversion.Converter#getConverterChain()
     */
    // Can't make generic arrays, so oh well...
    @SuppressWarnings("rawtypes")
	public ServiceReference[] getConverterChain() {
        return this.serviceReferences.toArray(new ServiceReference[0]);
    }
    
    public ImmutableList<ServiceReference<AlgorithmFactory>> getConverterList() {
    	return this.serviceReferences;
    }
    
    /**
     * @see org.cishell.service.conversion.Converter#getProperties()
     */
    public Dictionary<String,Object> getProperties() {
        return properties;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" }) // unfortunately, it's a raw type in the interface.
	public Algorithm createAlgorithm(Data[] dm,
    								 Dictionary parameters,
    								 CIShellContext context) {
        return new ConverterAlgorithm(dm, parameters, context, serviceReferences);
    }

    public MetaTypeProvider createParameters(Data[] dm) {
        return null;
    }
    
    public int hashCode() {
    	return Objects.hashCode(properties, serviceReferences);
    			
    }
    
    public String toString() {
    	return Objects.toStringHelper(Converter.class)
    			.add("properties", properties)
    			.add("chain", serviceReferences)
    			.toString();
    }
    
    // Rely partly on ServiceReference's .equals implementation:
    // http://www.osgi.org/javadoc/r4v43/org/osgi/framework/ServiceReference.html
    public boolean equals(Object compareTo) {
    	if (! (compareTo instanceof ConverterImpl)) {
    		return false;
    	}
    	ConverterImpl that = (ConverterImpl) compareTo;
    	
    	return (this.properties.equals(that.properties))
    			&& (this.serviceReferences.equals(that.serviceReferences));
    }

    public String calculateLossiness() {
    	return calculateLossiness(getConverterList());
    }

	private static String calculateLossiness(List<ServiceReference<AlgorithmFactory>> serviceReferences) {
		for (ServiceReference<AlgorithmFactory> serviceReference : serviceReferences) {
	        if (LOSSY.equals(serviceReference.getProperty(CONVERSION))) {
	            return LOSSY;
	        }
	    }
	    
		return LOSSLESS;
	}

	private class ConverterAlgorithm implements Algorithm {
        public static final String FILE_EXTENSION_PREFIX = "file-ext:";
		public static final String MIME_TYPE_PREFIX = "file:";
		
		private Data[] inData;        
        private Dictionary<String, Object> parameters;
        private CIShellContext ciShellContext;
        private LogService logger;
        private ImmutableList<ServiceReference<AlgorithmFactory>> serviceReferences;
        
        
        public ConverterAlgorithm(
        		Data[] inData, Dictionary<String, Object> parameters, CIShellContext ciShellContext,
        		ImmutableList<ServiceReference<AlgorithmFactory>> serviceReferences) {
            this.inData = inData;
            this.parameters = parameters;
            this.ciShellContext = ciShellContext;
            this.serviceReferences = serviceReferences;
            this.logger =
            	(LogService) ciShellContext.getService(LogService.class.getName());
        }
        
        
        public Data[] execute() throws AlgorithmExecutionException {
            Data[] convertedData = this.inData;
            
            // For each converter in the converter chain (serviceReferences)
            for (int ii = 0; ii < serviceReferences.size(); ii++) {
                AlgorithmFactory factory =
                	bContext.getService(serviceReferences.get(ii));
                
                if (factory != null) {
                    Algorithm algorithm = factory.createAlgorithm(
                    	convertedData, this.parameters, this.ciShellContext);
                    
                    try {
                    	convertedData = algorithm.execute();
                    } catch(AlgorithmExecutionException e) {
                    	boolean isLastStep = (ii == serviceReferences.size() - 1);
                    	if (isLastStep && isHandler(serviceReferences.get(ii))) {
                    		/* If the last step of the converter chain is a
                    		 * handler and it is the first (and so only) step
                    		 * to fail, just logger a warning and return the
                    		 * un-handled data.
                    		 */
                    		String warningMessage =
                    			"Warning: Attempting to convert data without " 
                    			+ "validating the output since the validator failed " 
                    			+ "with this problem:\n    " 
                    			+ createErrorMessage(serviceReferences.get(ii), e);
                    		
            				this.logger.log(LogService.LOG_WARNING, warningMessage, e);
                    		
                    		return convertedData;
                    	} else {                    	
	                   		throw new AlgorithmExecutionException(
	                   			createErrorMessage(serviceReferences.get(ii), e), e);
                    	}
                    }
                } else {
                    throw new AlgorithmExecutionException(
                    		"Missing subconverter: "
                            + serviceReferences.get(ii).getProperty(Constants.SERVICE_PID));
                }
            }
            
            return convertedData;
        }
        
        private boolean isHandler(ServiceReference<AlgorithmFactory> ref) {
        	/* For some reason, handlers are often referred to as validators,
             * though strictly speaking, validators are for reading in data and
             * handlers are for writing out data.
             */
        	String algorithmType =
        		(String) ref.getProperty(AlgorithmProperty.ALGORITHM_TYPE);
        	boolean algorithmTypeIsValidator =
        		AlgorithmProperty.TYPE_VALIDATOR.equals(algorithmType);
        	
        	String inDataType =
        		(String) ref.getProperty(AlgorithmProperty.IN_DATA);
        	boolean inDataTypeIsFile = inDataType.startsWith(MIME_TYPE_PREFIX);
        	
        	String outDataType =
        		(String) ref.getProperty(AlgorithmProperty.OUT_DATA);
        	boolean outDataTypeIsFileExt =
        		outDataType.startsWith(FILE_EXTENSION_PREFIX);
        	
        	return (algorithmTypeIsValidator
        			&& inDataTypeIsFile
        			&& outDataTypeIsFileExt);
    	}
        
        private String createErrorMessage(ServiceReference<AlgorithmFactory> ref, Throwable e) {
        	String inType = (String) properties.get(IN_DATA);
        	String preProblemType =	(String) ref.getProperty(IN_DATA);
        	String postProblemType = (String) ref.getProperty(OUT_DATA);
        	String outType = (String) properties.get(OUT_DATA);
        	
        	/* Only report the intermediate conversion if it is different
        	 * from the overall conversion.
        	 */        	
        	if (inType.equals(preProblemType)
        			&& outType.equals(postProblemType)) {
        		return "Problem converting data from "
        				+ prettifyDataType(inType)
        				+ " to " + prettifyDataType(outType)
        				+ " (See the log file for more details).:\n        "
        				+ e.getMessage();
        	}
        	else {        	
	        	return "Problem converting data from "
			        	+ prettifyDataType(inType) + " to "
			        	+ prettifyDataType(outType)
			        	+ " during the necessary intermediate conversion from "
			        	+ prettifyDataType(preProblemType) + " to "
			        	+ prettifyDataType(postProblemType)
			        	+ " (See the log file for more details):\n        "
			        	+ e.getMessage();
        	}
        }
        
        private String prettifyDataType(String dataType) {
        	if (dataType.startsWith(MIME_TYPE_PREFIX)) {
        		return withoutFirstCharacters(
        				dataType, MIME_TYPE_PREFIX.length());
        	}
        	else if(dataType.startsWith(FILE_EXTENSION_PREFIX)) {
        		return "." + withoutFirstCharacters(
        				dataType, FILE_EXTENSION_PREFIX.length());
        	}
        	else {
        		return dataType;
        	}
        }
        
        public String withoutFirstCharacters(String s, int n) {
        	return s.substring(n);
        }
    }
}
