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

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeProvider;

public class ConverterImpl implements Converter, AlgorithmFactory, AlgorithmProperty {
    private ServiceReference[] serviceReferences;
    private BundleContext bContext;
    private Dictionary<String, Object> properties;
    private CIShellContext ciContext;
	
    
    public ConverterImpl(BundleContext bContext, CIShellContext ciContext, ServiceReference[] refs) {
        this.bContext = bContext;
        this.ciContext = ciContext;
        this.serviceReferences = refs;
        properties = new Hashtable<String, Object>();
        
        properties.put(IN_DATA, refs[0].getProperty(IN_DATA));
        properties.put(OUT_DATA, refs[refs.length-1].getProperty(OUT_DATA));
        properties.put(LABEL, properties.get(IN_DATA) + " -> " + properties.get(OUT_DATA));
        
        // TODO: Do the same thing for complexity
        String lossiness = calculateLossiness(refs);        
        properties.put(CONVERSION, lossiness);
    }

    /**
     * @see org.cishell.service.conversion.Converter#convert(org.cishell.framework.data.Data)
     */
    public Data convert(Data inData) throws ConversionException {
        Data[] data = new Data[]{inData};
        
        AlgorithmFactory factory = getAlgorithmFactory();
        Algorithm algorithm = factory.createAlgorithm(data, new Hashtable(), ciContext);

        try {
			data = algorithm.execute();
		} catch (AlgorithmExecutionException e) {
			throw new ConversionException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ConversionException(
					"Unexpected error: " + e.getMessage(), e);
		}
        
        Object outData = null;
        if (data != null && data.length > 0) {
            outData = data[0].getData();
        }
        
        if (outData != null) {
            Dictionary properties = inData.getMetadata();
            Dictionary newProperties = new Hashtable();
            
            for (Enumeration propertyKeys = properties.keys(); propertyKeys.hasMoreElements();) {
                Object key = propertyKeys.nextElement();
                newProperties.put(key, properties.get(key));
            }
               
            String outFormat =
            	(String) getProperties().get(AlgorithmProperty.OUT_DATA);
            
            return new BasicData(newProperties, outData, outFormat);
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
    public ServiceReference[] getConverterChain() {
        return this.serviceReferences;
    }

    /**
     * @see org.cishell.service.conversion.Converter#getProperties()
     */
    public Dictionary getProperties() {
        return properties;
    }

    public Algorithm createAlgorithm(Data[] dm,
    								 Dictionary parameters,
    								 CIShellContext context) {
        return new ConverterAlgorithm(dm, parameters, context);
    }

    public MetaTypeProvider createParameters(Data[] dm) {
        return null;
    }
    
    public int hashCode() {
    	return toString().hashCode();
    }
    
    public String toString() {
    	String str = "";

    	for (ServiceReference serviceReference : this.serviceReferences) {
    		str += serviceReference.getProperty(Constants.SERVICE_ID);
    		str += " ";
    		str += serviceReference.getProperty(Constants.SERVICE_PID);
    		str += "-> ";
    	}
    	
    	return str;
    }

    public boolean equals(Object compareTo) {
    	boolean equal = false;

    	if (compareTo instanceof Converter) {
	    	ServiceReference[] otherServiceReference = ((Converter) compareTo).getConverterChain();

	    	if (this.serviceReferences.length == otherServiceReference.length) {
		    	for (int i = 0; i < otherServiceReference.length; i++) {
		    		if (this.serviceReferences[i].getProperty(Constants.SERVICE_ID).equals(
		    				otherServiceReference[i].getProperty(
		    						Constants.SERVICE_ID))) {
		    			equal = true;
		    		} else {
		    			equal = false;
		    			break;
		    		}
		    	}
	    	}
    	}
	    	
	    return equal;
    }
    
    /* The conversion chain (serviceReferences) is lossless
	 * if and only if no conversion (ref) is lossy.
	 */
	private String calculateLossiness(ServiceReference[] refs) {
		String lossiness = LOSSLESS;
	    for (int i=0; i < refs.length; i++) {
	        if (LOSSY.equals(refs[i].getProperty(CONVERSION))) {
	            lossiness = LOSSY;
	        }
	    }
	    
		return lossiness;
	}

	private class ConverterAlgorithm implements Algorithm {
        public static final String FILE_EXTENSION_PREFIX = "file-ext:";
		public static final String MIME_TYPE_PREFIX = "file:";
		
		private Data[] inData;        
        private Dictionary<String, Object> parameters;
        private CIShellContext ciShellContext;
        private LogService logger;
        
        
        public ConverterAlgorithm(
        		Data[] inData, Dictionary<String, Object> parameters, CIShellContext ciShellContext) {
            this.inData = inData;
            this.parameters = parameters;
            this.ciShellContext = ciShellContext;
            this.logger =
            	(LogService) ciShellContext.getService(LogService.class.getName());
        }
        
        
        public Data[] execute() throws AlgorithmExecutionException {
            Data[] convertedData = this.inData;
            
            // For each converter in the converter chain (serviceReferences)
            for (int ii = 0; ii < serviceReferences.length; ii++) {
                AlgorithmFactory factory =
                	(AlgorithmFactory) bContext.getService(serviceReferences[ii]);
                
                if (factory != null) {
                    Algorithm algorithm = factory.createAlgorithm(
                    	convertedData, this.parameters, this.ciShellContext);
                    
                    try {
                    	convertedData = algorithm.execute();
                    } catch(AlgorithmExecutionException e) {
                    	boolean isLastStep = (ii == serviceReferences.length - 1);
                    	if (isLastStep && isHandler(serviceReferences[ii])) {
                    		/* If the last step of the converter chain is a
                    		 * handler and it is the first (and so only) step
                    		 * to fail, just logger a warning and return the
                    		 * un-handled data.
                    		 */
                    		String warningMessage =
                    			"Warning: Attempting to convert data without " 
                    			+ "validating the output since the validator failed " 
                    			+ "with this problem:\n    " 
                    			+ createErrorMessage(serviceReferences[ii], e);
                    		
            				this.logger.log(LogService.LOG_WARNING, warningMessage, e);
                    		
                    		return convertedData;
                    	} else {                    	
	                   		throw new AlgorithmExecutionException(
	                   			createErrorMessage(serviceReferences[ii], e), e);
                    	}
                    }
                } else {
                    throw new AlgorithmExecutionException(
                    		"Missing subconverter: "
                            + serviceReferences[ii].getProperty(Constants.SERVICE_PID));
                }
            }
            
            return convertedData;
        }
        
        private boolean isHandler(ServiceReference ref) {
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
        
        private String createErrorMessage(ServiceReference ref, Throwable e) {
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
        				+ " (See the logger file for more details).:\n        "
        				+ e.getMessage();
        	}
        	else {        	
	        	return "Problem converting data from "
			        	+ prettifyDataType(inType) + " to "
			        	+ prettifyDataType(outType)
			        	+ " during the necessary intermediate conversion from "
			        	+ prettifyDataType(preProblemType) + " to "
			        	+ prettifyDataType(postProblemType)
			        	+ " (See the logger file for more details):\n        "
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
