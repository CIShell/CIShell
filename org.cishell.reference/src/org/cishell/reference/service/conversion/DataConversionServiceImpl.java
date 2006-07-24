/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 15, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.service.conversion;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.datamodel.BasicDataModel;
import org.cishell.framework.datamodel.DataModel;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class DataConversionServiceImpl implements DataConversionService, AlgorithmProperty {
    private BundleContext bContext;
    private CIShellContext ciContext;
    
    public DataConversionServiceImpl(BundleContext bContext, CIShellContext ciContext) {
        this.bContext = bContext;
        this.ciContext = ciContext;
    }

    /**
     * TODO: Only provides a direct conversion, need to improve
     * 
     * @see org.cishell.service.conversion.DataConversionService#findConverters(java.lang.String, java.lang.String)
     */
    public Converter[] findConverters(String inFormat, String outFormat) {
        try {
            String filter = "(&("+IN_DATA+"="+inFormat+") " +
                              "("+OUT_DATA+"="+outFormat+"))";

            ServiceReference[] refs = bContext.getServiceReferences(
                    AlgorithmFactory.class.getName(), filter);
            
            if (refs != null && refs.length > 0) {
                Converter[] converters = new Converter[refs.length];
                for (int i=0; i < converters.length; i++) {
                    converters[i] = new ConverterImpl(bContext, new ServiceReference[]{refs[i]});
                }
                
                return converters;
            } else {
                return new Converter[0];
            }
        } catch (InvalidSyntaxException e) {
            getLog().log(LogService.LOG_ERROR, "Incorrect Syntax", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.cishell.service.conversion.DataConversionService#findConverters(java.lang.String, java.lang.String, int, java.lang.String)
     */
    public Converter[] findConverters(String inFormat, String outFormat,
            int maxHops, String maxComplexity) {
        return findConverters(inFormat, outFormat);
    }

    public DataModel convert(DataModel inDM, String outFormat) {
        String inFormat = inDM.getFormat();
        
        Converter[] converters = new Converter[0];
        if (inFormat != null) {
            if (inFormat.equals(outFormat)) return inDM;
            
            converters = findConverters(inFormat, outFormat);
        } else if (inDM.getData() != null) {
            //try to find a converter that will convert the java object to
            //the correct outFormat
            
            inFormat = inDM.getData().getClass().getName();
            if (inFormat.equals(outFormat)) return inDM;
            converters = findConverters(inFormat, outFormat);
            
            Class[] classes = inDM.getData().getClass().getClasses();
            
            for (int i=0; i < classes.length && converters.length == 0; i++) {
                inFormat = classes[i].getName();
                
                if (inFormat.equals(outFormat)) return inDM;
                
                converters = findConverters(inFormat, outFormat);
            }
        }
        
        Object outData = null;
        
        if (converters.length > 0) {
            DataModel[] dm = new DataModel[]{inDM};
            
            AlgorithmFactory factory = converters[0].getAlgorithmFactory();
            Algorithm alg = factory.createAlgorithm(dm, new Hashtable(), ciContext);

            dm = alg.execute();
            
            if (dm != null && dm.length > 0) {
                outData = dm[0].getData();
            }
        }
        
        if (outData != null) {
            Dictionary props = inDM.getMetaData();
            Dictionary newProps = new Hashtable();
            
            for (Enumeration e=props.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                newProps.put(key, props.get(key));
            }
                        
            return new BasicDataModel(newProps, outData, outFormat);
        } else {
            return null;
        }        
    }
    
    public Converter[] findConverters(DataModel dm, String outFormat) {
        String format = dm.getFormat();
        
        List list = new ArrayList();
        Converter[] converters = new Converter[0];
        if (format != null) {
            converters = findConverters(format, outFormat);
            list.addAll(Arrays.asList(converters));
        } 
        
        if (!(dm.getData() instanceof File) && dm.getData() != null) {
            converters = findConverters(
                    dm.getData().getClass().getName(), outFormat);
            list.addAll(Arrays.asList(converters));
            
            Class[] classes = dm.getData().getClass().getClasses();
            for (int i=0; i < classes.length; i++) {
                converters = findConverters(classes[i].getName(), outFormat);
                list.addAll(Arrays.asList(converters));
            }
        }
        
        return (Converter[]) list.toArray(new Converter[0]);
    }
    
    private LogService getLog() {
        return (LogService)ciContext.getService(LogService.class.getName());
    }
}
