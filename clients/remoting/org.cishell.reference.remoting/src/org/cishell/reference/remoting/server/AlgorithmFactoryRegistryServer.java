/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 4, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.server;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.reference.service.metatype.BasicMetaTypeProvider;
import org.cishell.reference.service.metatype.BasicObjectClassDefinition;
import org.cishell.remoting.service.framework.AlgorithmFactoryRegistry;
import org.cishell.remoting.service.framework.AlgorithmRegistry;
import org.cishell.remoting.service.framework.DataModelRegistry;
import org.cishell.remoting.service.framework.MetaTypeProviderRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.util.tracker.ServiceTracker;


public class AlgorithmFactoryRegistryServer implements AlgorithmFactoryRegistry {
    private ServiceTracker algRegistry;
    private ServiceTracker mtpRegistry;
    private ServiceTracker dmRegistry;
    private BundleContext bContext;
    private Map sidToContextMap;
    
    public AlgorithmFactoryRegistryServer(BundleContext bContext, CIShellContext ciContext) {
        this.bContext = bContext;
        
        sidToContextMap = new HashMap();
        
        algRegistry = new ServiceTracker(bContext, AlgorithmRegistry.class.getName(), null);
        mtpRegistry = new ServiceTracker(bContext, MetaTypeProviderRegistry.class.getName(), null);
        dmRegistry = new ServiceTracker(bContext, DataModelRegistry.class.getName(), null); 
        
        algRegistry.open();
        mtpRegistry.open();
        dmRegistry.open();
    }
    
    /**
     * @see org.cishell.remoting.service.framework.AlgorithmFactoryRegistry#createAlgorithm(String, long, Vector, Hashtable)
     */
    public String createAlgorithm(String sessionID, String servicePID,
            Vector dataModelIDs, Hashtable dictionary) {
        AlgorithmFactory factory = getAlgorithmFactory(servicePID);
        
        AlgorithmRegistry algReg = (AlgorithmRegistry) algRegistry.getService();
        MetaTypeProviderRegistry mtpReg = (MetaTypeProviderRegistry) mtpRegistry.getService();
        DataModelRegistry dmReg = (DataModelRegistry) dmRegistry.getService();
        
        String algID = "-1";
        if (factory != null && algReg != null && mtpReg != null && dmReg != null) {
            Data[] dm = dmReg.getDataModels(dataModelIDs);
            
            CIShellContext ciContext = null;
            synchronized (sidToContextMap) {
                ciContext = (CIShellContext) sidToContextMap.get(sessionID); 
                
                if (ciContext == null) {
                    ciContext = new RemoteCIShellContext(bContext, sessionID);
                    sidToContextMap.put(sessionID, ciContext);
                }
            }
            
            //TODO: should parse the given hashtable since values may need to 
            //be changed to different types (Vector->String[], etc...)
            Algorithm alg = factory.createAlgorithm(dm, dictionary, ciContext);
            if (alg != null) {
                algID = algReg.registerAlgorithm(alg);
            }
        }
        
        return algID;
    }
    
    /**
     * @see org.cishell.remoting.service.framework.AlgorithmFactoryRegistry#createParameters(long, Vector)
     */
    public String createParameters(String servicePID, Vector dataModelIDs) {
        AlgorithmFactory factory = getAlgorithmFactory(servicePID);
        
        AlgorithmRegistry algReg = (AlgorithmRegistry) algRegistry.getService();
        MetaTypeProviderRegistry mtpReg = (MetaTypeProviderRegistry) mtpRegistry.getService();
        DataModelRegistry dmReg = (DataModelRegistry) dmRegistry.getService();
        
        String mtpID = "-1";
        if (factory != null && algReg != null && mtpReg != null && dmReg != null) {
            Data[] dm = dmReg.getDataModels(dataModelIDs);
            
            MetaTypeProvider mtp = getMetaTypeProvider(servicePID, dm);
            mtpID = mtpReg.registerMetaTypeProvider(mtp);
        }
        
        return mtpID;
    }
    
    //FIXME
    private MetaTypeProvider getMetaTypeProvider(String servicePID, Data[] dm) {
		try {
			String filter = "(" + Constants.SERVICE_PID + "=" + servicePID + ")";
			ServiceReference[] refs = bContext.getServiceReferences(AlgorithmFactory.class.getName(), filter);
			
			if (refs != null && refs.length == 1) {
	            
	        }
		} catch (InvalidSyntaxException e) {

		}
		
    	return new BasicMetaTypeProvider(new BasicObjectClassDefinition(servicePID,"","",null));
    }
    
    public Hashtable getProperties(String servicePID) {
        Hashtable ht = new Hashtable();
        
        String filter = "(" + Constants.SERVICE_PID + "=" + servicePID + ")";
        
        ServiceReference[] refs = null;
        try {
            refs = bContext.getServiceReferences(AlgorithmFactory.class.getName(), filter);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        
        if (refs != null && refs.length == 1) {
            String[] keys = refs[0].getPropertyKeys();
            
            for (int i=0; i < keys.length; i++) {
                Object value = refs[0].getProperty(keys[i]);
                
                if (value instanceof Vector) {
                    
                } else if (value instanceof String[]) {
                    value = new Vector(Arrays.asList((String[])value));
                } else {
                    value = "" + value;
                }
                
                ht.put(keys[i], value);
            }
            
            return ht;
        } else {
            return null;
        }
    }
    
    public AlgorithmFactory getAlgorithmFactory(String serviceID) {
        AlgorithmFactory factory = null;
        
        try {
            String filter = "(" + Constants.SERVICE_PID + "=" + serviceID + ")";
            
            ServiceReference[] refs = 
                bContext.getServiceReferences(AlgorithmFactory.class.getName(), filter);
            
            if (refs != null && refs.length == 1) {
                factory = (AlgorithmFactory)bContext.getService(refs[0]);
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        
        return factory;
    }
}
