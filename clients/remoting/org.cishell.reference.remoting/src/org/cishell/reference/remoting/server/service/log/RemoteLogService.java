/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 13, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.server.service.log;

import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.reference.remoting.event.AbstractEventConsumerProducer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class RemoteLogService extends AbstractEventConsumerProducer implements
        LogService {

    public RemoteLogService(BundleContext bContext, String sessionID) {
        super(bContext, sessionID, "LogService", false);
    }
    
    /**
     * @see org.osgi.service.log.LogService#log(int, java.lang.String)
     */
    public void log(int level, String message) {
        Dictionary params = new Hashtable();
        
        params.put("level", ""+level);
        params.put("message", message);
        
        postEvent(createEvent("log", params));
    }

    /**
     * @see org.osgi.service.log.LogService#log(int, java.lang.String, java.lang.Throwable)
     */
    public void log(int level, String message, Throwable exception) {
        Dictionary params = new Hashtable();
        
        params.put("level", ""+level);
        params.put("message", message);
        params.put("exception", exception.getMessage());
        params.put("exception_class", exception.getClass().getName());
        
        postEvent(createEvent("loge", params));
    }

    /**
     * @see org.osgi.service.log.LogService#log(org.osgi.framework.ServiceReference, int, java.lang.String)
     */
    public void log(ServiceReference sr, int level, String message) {
        Dictionary params = new Hashtable();
        
        params.put("sr", sr.getProperty(Constants.SERVICE_PID));
        params.put("level", ""+level);
        params.put("message", message);
        
        postEvent(createEvent("logsr", params));
    }

    /**
     * @see org.osgi.service.log.LogService#log(org.osgi.framework.ServiceReference, int, java.lang.String, java.lang.Throwable)
     */
    public void log(ServiceReference sr, int level, String message, Throwable exception) {
        Dictionary params = new Hashtable();
        
        params.put("sr", sr.getProperty(Constants.SERVICE_PID));
        params.put("level", ""+level);
        params.put("message", message);
        params.put("exception", exception.getMessage());
        params.put("exception_class", exception.getClass().getName());
        
        postEvent(createEvent("logsre", params));
    }
}
