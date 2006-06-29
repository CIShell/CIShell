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

public class LocalCIShellContext implements CIShellContext {
    private BundleContext bContext;
    private String[] standardServices;
    
    public LocalCIShellContext(BundleContext bContext) {
        this(bContext, DEFAULT_SERVICES);
    }
    
    public LocalCIShellContext(BundleContext bContext, String[] standardServices) {
        this.bContext = bContext;
        this.standardServices = standardServices;
    }

    /**
     * @see org.cishell.framework.CIShellContext#getService(java.lang.String)
     */
    public Object getService(String service) {
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
        
        return null;
    }
}
