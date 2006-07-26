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
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.datamodel.BasicDataModel;
import org.cishell.framework.datamodel.DataModel;
import org.cishell.service.conversion.Converter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class ConverterImpl implements Converter, AlgorithmFactory, AlgorithmProperty {
    private ServiceReference[] refs;
    private BundleContext bContext;
    private Dictionary props;
    private CIShellContext ciContext;
    
    public ConverterImpl(BundleContext bContext, CIShellContext ciContext, ServiceReference[] refs) {
        this.bContext = bContext;
        this.ciContext = ciContext;
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
    
    /**
     * @see org.cishell.service.conversion.Converter#convert(org.cishell.framework.datamodel.DataModel)
     */
    public DataModel convert(DataModel inDM) {
        DataModel[] dm = new DataModel[]{inDM};
        
        AlgorithmFactory factory = getAlgorithmFactory();
        Algorithm alg = factory.createAlgorithm(dm, new Hashtable(), ciContext);

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
            return new BasicDataModel(newProps, outData, outFormat);
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
        return refs;
    }

    /**
     * @see org.cishell.service.conversion.Converter#getProperties()
     */
    public Dictionary getProperties() {
        return props;
    }

    public Algorithm createAlgorithm(DataModel[] dm, Dictionary parameters, CIShellContext context) {
        return new ConverterAlgorithm(dm, parameters, context);
    }

    public MetaTypeProvider createParameters(DataModel[] dm) {
        return null;
    }
    
    private class ConverterAlgorithm implements Algorithm {
        DataModel[] inDM;
        CIShellContext context;
        Dictionary parameters;
        
        public ConverterAlgorithm(DataModel[] dm, Dictionary parameters, CIShellContext context) {
            this.inDM = dm;
            this.parameters = parameters;
            this.context = context;
        }
        
        public DataModel[] execute() {
            DataModel[] dm = inDM;
            for (int i=0; i < refs.length; i++) {
                AlgorithmFactory factory = (AlgorithmFactory)bContext.getService(refs[i]);
                
                if (factory != null) {
                    Algorithm alg = factory.createAlgorithm(dm, parameters, context);
                    
                    dm = alg.execute();
                } else {
                    throw new RuntimeException("Missing subconverter: " 
                            + refs[i].getProperty(Constants.SERVICE_PID));
                }
            }
            
            return dm;
        }
    }
}
