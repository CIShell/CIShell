/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 21, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.server.service.conversion;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.remoting.service.conversion.RemoteDataConversionService;
import org.cishell.remoting.service.framework.DataModelRegistry;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.Converter;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class RemoteDataConversionServiceServer implements
        RemoteDataConversionService {
    private CIShellContext ciContext;
    private ServiceTracker dmReg;

    public RemoteDataConversionServiceServer(BundleContext bContext, CIShellContext ciContext) {
        this.ciContext = ciContext;
        
        dmReg = new ServiceTracker(bContext, DataModelRegistry.class.getName(), null);
        dmReg.open();
    }
    
    /**
     * @see org.cishell.remoting.service.conversion.RemoteDataConversionService#convert(java.lang.String, java.lang.String)
     */
    public String convert(String dataModelID, String outFormat) {
        DataConversionService converter = getConverter();
        String id = "-1";
        
        DataModelRegistry dmRegistry = (DataModelRegistry) dmReg.getService();
        
        Data dm = dmRegistry.getDataModel(dataModelID);
        if (dm != null) {
            try {
				dm = converter.convert(dm, outFormat);
			} catch (ConversionException e) {
				dm = null;
			}
            
            if (dm != null) {
                id = dmRegistry.registerDataModel(dm);
            }
        }
        
        return id;
    }

    /**
     * @see org.cishell.remoting.service.conversion.RemoteDataConversionService#findConverter(java.util.Vector, java.util.Vector)
     */
    public Vector findConverter(Vector inFormats, Vector outFormats) {
        DataConversionService converter = getConverter();
        
        for (Iterator i=inFormats.iterator(); i.hasNext(); ) {
            String inFormat = (String) i.next();
            
            for (Iterator j=outFormats.iterator(); j.hasNext(); ) {
                String outFormat = (String) j.next();
                
                Converter[] c = converter.findConverters(inFormat, outFormat);
                if (c.length > 0) {
                    Vector v = new Vector();
                    v.add(inFormat);
                    v.add(outFormat);
                    
                    return v;
                }
            }
        }
        
        return null;
    }

    /**
     * @see org.cishell.remoting.service.conversion.RemoteDataConversionService#getConversions(java.lang.String, java.lang.String)
     */
    public Vector getConversions(String dataModelID, String outFormat) {
        DataModelRegistry dmRegistry = (DataModelRegistry) dmReg.getService();
        Data dm = dmRegistry.getDataModel(dataModelID);
        
        Set conversions = new HashSet();
        if (dm != null) {
            Converter[] converters = getConverter().findConverters(dm, outFormat);
            
            for (int j=0; j < converters.length; j++) {
                conversions.add(converters[j].getProperties().get(AlgorithmProperty.OUT_DATA));
            }
        }
        
        return new Vector(conversions);
    }
    
    private DataConversionService getConverter() {
        return (DataConversionService) ciContext.getService(
                DataConversionService.class.getName());
    }
}
