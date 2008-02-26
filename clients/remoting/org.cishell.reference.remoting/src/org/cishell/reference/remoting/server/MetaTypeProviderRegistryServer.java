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
import java.util.Vector;

import org.cishell.framework.CIShellContext;
import org.cishell.reference.remoting.ObjectRegistry;
import org.cishell.remoting.service.framework.MetaTypeProviderRegistry;
import org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.ObjectClassDefinition;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class MetaTypeProviderRegistryServer implements MetaTypeProviderRegistry {
    private ObjectRegistry registry;
    private BundleContext bContext;
    private ServiceTracker ocdReg; 
    
    public MetaTypeProviderRegistryServer(BundleContext bContext, CIShellContext ciContext) {
        this.bContext = bContext;
        
        registry = new ObjectRegistry();
        ocdReg = new ServiceTracker(this.bContext, 
                        ObjectClassDefinitionRegistry.class.getName(), null);
        
        ocdReg.open();
    }

    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#getLocales(String)
     */
    public Vector getLocales(String providerID) {
        MetaTypeProvider provider = getMetaTypeProvider(providerID);
        
        if (provider != null) {
            return toVector(provider.getLocales());
        } else {
            return new Vector();
        }
    }

    private Vector toVector(String[] str) {
        Vector v = null;
        if (str != null) {
            v = new Vector(Arrays.asList(str));
        }
        return v;
    }
    
    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#getObjectClassDefinition(String, java.lang.String, java.lang.String)
     */
    public String getObjectClassDefinition(String providerID, String id,
            String locale) {
        String ocdID = "-1";
        MetaTypeProvider provider = getMetaTypeProvider(providerID);
        ObjectClassDefinitionRegistry ocdRegistry = 
            (ObjectClassDefinitionRegistry) ocdReg.getService();
        
        if (provider != null && ocdRegistry != null) {
            ObjectClassDefinition ocd = provider.getObjectClassDefinition(id, locale);
            
            if (ocd != null) {
                ocdID = ocdRegistry.registerObjectClassDefinition(ocd);
            }
        }
        
        return ocdID;
    }

    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#getMetaTypeProvider(String)
     */
    public MetaTypeProvider getMetaTypeProvider(String providerID) {
        return (MetaTypeProvider) registry.getObject(providerID);
    }
    
    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#registerMetaTypeProvider(org.osgi.service.metatype.MetaTypeProvider)
     */
    public String registerMetaTypeProvider(MetaTypeProvider provider) {
        return registry.register(provider);
    }

    /**
     * @see org.cishell.remoting.service.framework.MetaTypeProviderRegistry#unregisterMetaTypeProvider(String)
     */
    public void unregisterMetaTypeProvider(String providerID) {
        registry.unregister(providerID);
    }

}
