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
package org.cishell.reference.remoting.client.service.log;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.reference.remoting.event.AbstractEventConsumerProducer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.log.LogService;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class LogEventHandler extends AbstractEventConsumerProducer {
    private LogService log;
    private boolean isRunning;
    
    public LogEventHandler(BundleContext bContext, String sessionID) {
        super(bContext, sessionID, "LogService", true);
        
        log = (LogService) bContext.getService(bContext.getServiceReference(
                LogService.class.getName()));
        isRunning = true;
    }
    
    public void stop() {
        isRunning = false;
        log = null;
    }
    
    public void handleEvent(Event event) {
        if (isRunning) {
            String type = (String)event.getProperty(EVENT_TYPE);
            
            if (type != null) {
                type = type.toLowerCase();
                
                if (type.equals("log")) {
                    log(event);
                } else if (type.equals("loge")) {
                    loge(event);
                } else if (type.equals("logsr")) {
                    logsr(event);
                } else if (type.equals("logsre")) {
                    logsre(event);
                }
            }
        }
    }
    
    private void log(Event event) {
        if (event.getProperty("reply") == null) {
            int level = Integer.parseInt(""+event.getProperty("level"));
            String msg = (String)event.getProperty("message");
            
            getLog().log(level, msg);
        }
    }
    
    private void loge(Event event) {
        int level = Integer.parseInt(""+event.getProperty("level"));
        String msg = (String)event.getProperty("message");
        String exception = (String) event.getProperty("exception");
        String exc_class = (String) event.getProperty("exception_class");
        Throwable e = new Throwable(exc_class + "->" + exception);
        
        getLog().log(level, msg, e);
    }
    
    private void logsr(Event event) {
        int level = Integer.parseInt(""+event.getProperty("level"));
        String msg = (String)event.getProperty("message");
        String pid = (String)event.getProperty("sr");
        
        ServiceReference sr = getServiceReference(pid);
        getLog().log(sr, level, msg);
    }
    
    private void logsre(Event event) {
        int level = Integer.parseInt(""+event.getProperty("level"));
        String msg = (String)event.getProperty("message");
        String exception = (String) event.getProperty("exception");
        String exc_class = (String) event.getProperty("exception_class");
        Throwable e = new Throwable(exc_class + "->" + exception);
        String pid = (String)event.getProperty("sr");
        
        ServiceReference sr = getServiceReference(pid);
        
        getLog().log(sr, level, msg, e);
    }
    
    protected ServiceReference getServiceReference(String pid) {
        String filter = "(&("+Constants.SERVICE_PID+"="+pid+"))";
        
        try {
            ServiceReference[] refs = bContext.getServiceReferences(
                 AlgorithmFactory.class.getName(), filter);
            
            if (refs != null && refs.length > 0) {
                return refs[0];
            }
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    protected LogService getLog() {        
        return log;
    }
}
