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
package org.cishell.reference.remoting;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.cishell.framework.CIShellContext;
import org.cishell.reference.remoting.server.AlgorithmFactoryRegistryServer;
import org.cishell.reference.remoting.server.AlgorithmRegistryServer;
import org.cishell.reference.remoting.server.AttributeDefinitionRegistryServer;
import org.cishell.reference.remoting.server.CIShellFrameworkServer;
import org.cishell.reference.remoting.server.DataModelRegistryServer;
import org.cishell.reference.remoting.server.MetaTypeProviderRegistryServer;
import org.cishell.reference.remoting.server.ObjectClassDefinitionRegistryServer;
import org.cishell.reference.remoting.server.service.conversion.RemoteDataConversionServiceServer;
import org.cishell.remoting.service.conversion.RemoteDataConversionService;
import org.cishell.remoting.service.framework.AlgorithmFactoryRegistry;
import org.cishell.remoting.service.framework.AlgorithmRegistry;
import org.cishell.remoting.service.framework.AttributeDefinitionRegistry;
import org.cishell.remoting.service.framework.CIShellFramework;
import org.cishell.remoting.service.framework.DataModelRegistry;
import org.cishell.remoting.service.framework.MetaTypeProviderRegistry;
import org.cishell.remoting.service.framework.ObjectClassDefinitionRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;


public class CIShellServer {
    private BundleContext bContext;
    private List services;
    
    public void start(BundleContext bContext, CIShellContext ciContext) {
        this.bContext = bContext;
        this.services = new ArrayList();
        
        register(AlgorithmFactoryRegistry.class, 
                new AlgorithmFactoryRegistryServer(bContext, ciContext),
                AlgorithmFactoryRegistry.SERVICE_NAME,
                null);
        
        register(AlgorithmRegistry.class, 
                new AlgorithmRegistryServer(bContext, ciContext),
                AlgorithmRegistry.SERVICE_NAME,
                null);
        
        register(DataModelRegistry.class,
                 new DataModelRegistryServer(bContext, ciContext),
                 DataModelRegistry.SERVICE_NAME,
                 null);
        
        register(MetaTypeProviderRegistry.class,
                new MetaTypeProviderRegistryServer(bContext, ciContext),
                MetaTypeProviderRegistry.SERVICE_NAME,
                null);
        
        register(ObjectClassDefinitionRegistry.class,
                new ObjectClassDefinitionRegistryServer(bContext, ciContext),
                ObjectClassDefinitionRegistry.SERVICE_NAME,
                null);
        
        register(AttributeDefinitionRegistry.class,
                new AttributeDefinitionRegistryServer(bContext, ciContext),
                AttributeDefinitionRegistry.SERVICE_NAME,
                null);
        
        register(CIShellFramework.class,
                new CIShellFrameworkServer(bContext, ciContext),
                CIShellFramework.SERVICE_NAME,
                null);
        
        register(RemoteDataConversionService.class,
                new RemoteDataConversionServiceServer(bContext, ciContext),
                RemoteDataConversionService.SERVICE_NAME,
                null);
    }
    
    private void register(Class service, Object impl, String serviceName, String methods) {
        Dictionary dict = new Hashtable();
        
        dict.put("SOAP.service.name", serviceName);
        dict.put("server", "true");
        
        if (methods != null) {
            dict.put("SOAP.service.methods", methods);
        }
        
        ServiceRegistration reg = 
            bContext.registerService(service.getName(), impl, dict);
        
        if (reg != null) {
            services.add(reg);
        }
    }

    public void stop() {
        if (services != null) {
            for (Iterator iter = services.iterator(); iter.hasNext();) {
                ServiceRegistration reg = (ServiceRegistration) iter.next();
                if (reg != null) {
                    reg.unregister();
                }
            }
            services = null;
        }
        bContext = null;
    }
}
