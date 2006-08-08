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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

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
                              "("+OUT_DATA+"="+outFormat+")" +
                              "(!("+REMOTE+"=*)))";

            ServiceReference[] refs = bContext.getServiceReferences(
                    AlgorithmFactory.class.getName(), filter);
            
            if (refs != null && refs.length > 0) {
                Converter[] converters = new Converter[refs.length];
                for (int i=0; i < converters.length; i++) {
                    converters[i] = new ConverterImpl(bContext, ciContext, new ServiceReference[]{refs[i]});
                }
                
                return converters;
            } else {
                return new Converter[0];
            }
        } catch (InvalidSyntaxException e) {
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

    /**
     * @see org.cishell.service.conversion.DataConversionService#findConverters(org.cishell.framework.data.Data, java.lang.String)
     */
    public Converter[] findConverters(Data data, String outFormat) {
        String format = data.getFormat();
        
        List list = new ArrayList();
        Converter[] converters = new Converter[0];
        if (format != null) {
            converters = findConverters(format, outFormat);
            list.addAll(Arrays.asList(converters));
        } 
        
        if (!(data.getData() instanceof File) && data.getData() != null) {            
            Iterator iter = getClassesFor(data.getData().getClass()).iterator();
            while (iter.hasNext()) {
                Class c = (Class) iter.next();
                converters = findConverters(c.getName(), outFormat);
                list.addAll(Arrays.asList(converters));
            }
        }
        
        return (Converter[]) list.toArray(new Converter[0]);
    }
    
    protected Collection getClassesFor(Class clazz) {
        Set classes = new HashSet();
        
        Class[] c = clazz.getInterfaces();
        for (int i=0; i < c.length; i++) {
            classes.addAll(getClassesFor(c[i]));
        }
        
        Class superC = clazz.getSuperclass();
        
        if (superC != Object.class) {
            classes.addAll(getClassesFor(superC));
        } else {
            classes.add(superC);
        }
        
        classes.add(clazz);
        
        return classes;
    }
    
    /**
     * @see org.cishell.service.conversion.DataConversionService#convert(org.cishell.framework.data.Data, java.lang.String)
     */
    public Data convert(Data inDM, String outFormat) {
        String inFormat = inDM.getFormat();
        
        if (inFormat != null && inFormat.equals(outFormat)) {
            return inDM;
        }

        Converter[] converters = findConverters(inDM, outFormat);
        if (converters.length > 0) {
            inDM = converters[0].convert(inDM);
        }
        
        return inDM;
    }
}
