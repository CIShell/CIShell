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
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class NullConverter implements Converter, AlgorithmFactory, AlgorithmProperty, Comparable {
    private Dictionary props;
    
    
    public NullConverter(String inData) {
        props = new Hashtable();
        
        props.put(IN_DATA,  inData);
        props.put(OUT_DATA, inData);
        props.put(LABEL, props.get(IN_DATA) + " -> " + props.get(OUT_DATA));
        
        //TODO: Do the same thing for complexity
        props.put(CONVERSION, LOSSLESS);
    }
    
    /**
     * @see org.cishell.service.conversion.Converter#convert(org.cishell.framework.data.Data)
     */
    public Data convert(Data inDM) {
    	return inDM;
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
        return new ServiceReference[0];
    }

    /**
     * @see org.cishell.service.conversion.Converter#getProperties()
     */
    public Dictionary getProperties() {
        return props;
    }

    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters, CIShellContext context) {
        return new ConverterAlgorithm(dm, parameters, context);
    }

    public MetaTypeProvider createParameters(Data[] dm) {
        return null;
    }

    public boolean equals(Object o) {
    	boolean equals = false;
    	if (o instanceof Converter) {
	    	ServiceReference[] otherServiceReference = ((Converter)o).getConverterChain();
	    	if (otherServiceReference.length == 0) {
	    		Dictionary otherDictionary = ((Converter)o).getProperties();
	    		if (otherDictionary.get(IN_DATA).equals(props.get(IN_DATA)) &&
	    			otherDictionary.get(OUT_DATA).equals(props.get(OUT_DATA))) {
	    			equals = true;
		    	}
	    	}
    	}
	    	
	    return equals;
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
            return inDM;
        }
    }

	public int compareTo(Object o) {
		return equals(o) ? 0 : 1;
	}   
}
