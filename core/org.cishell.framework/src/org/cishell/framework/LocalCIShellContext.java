/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.framework;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * A simple implementation of {@link CIShellContext} that pulls the
 * CIShell services from the provided {@link BundleContext} that all OSGi
 * bundles receive on activation. This was included in the standard API since 
 * it will be used frequently by CIShell application developers. 
 * 
 * This implementation only returns standard services or the service strings
 * given to it in its constructor.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class LocalCIShellContext implements CIShellContext {
    private BundleContext bContext;
    private String[] standardServices;
    
    /**
     * Initializes the CIShell context based on the provided 
     * <code>BundleContext</code>
     * 
     * @param bContext The <code>BundleContext</code> to use to find 
     *                 the registered standard services
     */
    public LocalCIShellContext(BundleContext bContext) {
        this(bContext, DEFAULT_SERVICES);
    }
    
    /**
     * Initializes the CIShell context with a custom set of standard services.
     * Only the service in the array will be allowed to be retrieved from 
     * this <code>CIShellContext</code>.
     * 
     * @param bContext         The <code>BundleContext</code> to use to find 
     *                         registered standard services
     * @param standardServices An array of strings specifying the services that
     *                         are allowed to be retrieved from this class
     */
    public LocalCIShellContext(BundleContext bContext, String[] standardServices) {
        this.bContext = bContext;
        this.standardServices = standardServices;
    }

    /**
     * @see org.cishell.framework.CIShellContext#getService(java.lang.String)
     */
    public Object getService(String service) {
    	//check if the requested service is a standard service
        for (int i=0; i < standardServices.length; i++) {
            if (standardServices[i].equals(service)) {
                ServiceReference ref = bContext.getServiceReference(service);
                if (ref != null) {
                    return bContext.getService(ref);
                } else {
                    throw new RuntimeException("Standard CIShell Service: " + 
                                               service + " not installed!");
                }
            }
        }
        
        /*
         * if it is not a standard service, we try to retrieve it anyway,
         * but make no guarantees as to its availability
         */
        
        ServiceReference ref = bContext.getServiceReference(service);
        return bContext.getService(ref); //may be null
    }
}
