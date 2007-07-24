package org.cishell.testing.convertertester.core.converter;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.MetaTypeProvider;



public class ConverterTesterImpl implements Converter, AlgorithmFactory, AlgorithmProperty{
	private ServiceReference[] refs;
    private BundleContext bContext;
    private Dictionary props;
    private CIShellContext cContext;
    
    public ConverterTesterImpl(BundleContext bContext, CIShellContext cContext, ServiceReference[] refs) {
        this.bContext = bContext;
        this.cContext = cContext;
        this.refs = refs;
        
        props = new Hashtable();
        
        props.put(IN_DATA, refs[0].getProperty(IN_DATA));
        props.put(OUT_DATA, refs[refs.length-1].getProperty(OUT_DATA));
        props.put(LABEL, props.get(IN_DATA) + " -> " + props.get(OUT_DATA));
        
        String lossiness = LOSSLESS;
        for (int i=0; i < refs.length; i++) {
            if (LOSSY.equals(refs[i].getProperty(CONVERSION))) {
                lossiness = LOSSY;
            }
        }
        //TODO: Do the same thing for complexity
        props.put(CONVERSION, lossiness);
    }
	
	public Data convert(Data inDM) {
		Data[] dm = new Data[]{inDM};
        
        AlgorithmFactory factory = getAlgorithmFactory();
        Algorithm alg = factory.createAlgorithm(dm, new Hashtable(), cContext);

        dm = alg.execute();
        
        Object outData = null;
        if (dm != null && dm.length > 0) {
            outData = dm[0].getData();
        }
        
        if (outData != null) {
            Dictionary props = inDM.getMetaData();
            Dictionary newProps = new Hashtable();
            
            for (Enumeration e=props.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                newProps.put(key, props.get(key));
            }
               
            String outFormat = (String)getProperties().get(AlgorithmProperty.OUT_DATA);
            return new BasicData(newProps, outData, outFormat);
        } else {
            return null;
        }
	}

	public AlgorithmFactory getAlgorithmFactory() {
		// TODO Auto-generated method stub
		return this;
	}

	public ServiceReference[] getConverterChain() {
		// TODO Auto-generated method stub
		return this.refs;
	}

	public Dictionary getProperties() {
		// TODO Auto-generated method stub
		return this.props;
	}

	public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
		// TODO Auto-generated method stub
		return new ConverterAlgorithm(data,parameters,context);
	}

	public MetaTypeProvider createParameters(Data[] data) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private class ConverterAlgorithm implements Algorithm {
        Data[] inDM;
        CIShellContext context;
        Dictionary parameters;
        
        public ConverterAlgorithm(Data[] dm, Dictionary parameters, CIShellContext context) {
            this.inDM = dm;
            this.parameters = parameters;
            this.context = context;
        }
        
        public Data[] execute() {
            Data[] dm = inDM;
            for (int i=0; i < refs.length; i++) {
                AlgorithmFactory factory = (AlgorithmFactory)bContext.getService(refs[i]);
                
                if (factory != null) {
                    Algorithm alg = factory.createAlgorithm(dm, parameters, context);
                    System.out.println("Entering: " + refs[i].getProperty(Constants.SERVICE_PID)+ "-->");
                    dm = alg.execute();
                    if(dm == null){
                    	throw new RuntimeException("Error after " + refs[i].getProperty(Constants.SERVICE_PID));
                    
                    }
                } else {
                    throw new RuntimeException("Missing subconverter: " 
                            + refs[i].getProperty(Constants.SERVICE_PID));
                }
            }
            
            return dm;
        }
    }
	
	public int hashCode() {
    	return toString().hashCode();
    }
    
    public String toString() {
    	String str ="";
    	ServiceReference[] refs = this.refs;
    	for (int ii = 0; ii < refs.length; ii++) {
    		ServiceReference ref = refs[ii];
    		str += ref.getProperty(Constants.SERVICE_ID) + " " + ref.getProperty(Constants.SERVICE_PID) + "-> ";
    	}
    	return str;
    }
	
    public boolean equals(Object o) {
    	boolean equals = false;
    	if (o instanceof Converter) {
	    	ServiceReference[] otherServiceReference = ((Converter)o).getConverterChain();
	    	if (refs.length == otherServiceReference.length) {
		    	for (int i = 0; i < otherServiceReference.length; i++) {
		    		if (refs[i].getProperty(Constants.SERVICE_ID).equals(
                            otherServiceReference[i].getProperty(Constants.SERVICE_ID))) {
		    			equals = true;
		    		} else {
		    			equals = false;
		    			break;
		    		}
		    	}
	    	}
    	}
    	return equals;
    }

}
