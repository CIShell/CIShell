/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 14, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.server;

import java.util.HashMap;
import java.util.Map;

import org.cishell.framework.LocalCIShellContext;
import org.cishell.reference.remoting.server.service.log.RemoteLogService;
import org.cishell.reference.remoting.server.service.prefs.RemotePreferencesService;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.service.prefs.PreferencesService;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class RemoteCIShellContext extends LocalCIShellContext {
    public static final String[] REMOTE_SERVICES = new String[] {
        LogService.class.getName(),
        PreferencesService.class.getName()
    };
    Map remoteServices;

    public RemoteCIShellContext(BundleContext bContext, String sessionID) {
        super(bContext);
        
        remoteServices = new HashMap();
        remoteServices.put(LogService.class.getName(),
                           new RemoteLogService(bContext, sessionID));
        remoteServices.put(PreferencesService.class.getName(), 
                           new RemotePreferencesService(bContext, sessionID));
    }

    /**
     * @see org.cishell.framework.CIShellContext#getService(java.lang.String)
     */
    public Object getService(String service) {
        if (remoteServices.containsKey(service)) {
            return remoteServices.get(service);
        } else {
            return super.getService(service);
        }
    }
}
