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
package org.cishell.reference.remoting.event;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;


public abstract class AbstractEventConsumerProducer implements EventHandler,
        EventConstants, CIShellEventConstants {
    protected BundleContext bContext;
    private EventAdmin eventAdmin;
    protected String sessionID;
    protected String targetService;
    protected IDGenerator idGen;
    private Map receivedEvents;
    
    public AbstractEventConsumerProducer(BundleContext bContext, String sessionID, String targetService, boolean doRegister) {
        this.bContext = bContext;
        this.sessionID = sessionID;
        this.targetService = targetService;
        idGen = new IDGenerator(sessionID+"-"+targetService+"-");
        receivedEvents = new HashMap();
        
        eventAdmin = (EventAdmin) bContext.getService(
                bContext.getServiceReference(EventAdmin.class.getName()));
        
        if (doRegister) 
            registerService();
    }
    
    protected void registerService() {
        String filter = "(&("+TARGET_SERVICE+"="+targetService+")" +
                          "("+SESSION_ID+"="+sessionID+"))";
        
        Dictionary dict = new Hashtable();
        dict.put(EVENT_TOPIC, new String[]{BASE_TOPIC});
        dict.put(EVENT_FILTER, filter);

        bContext.registerService(EventHandler.class.getName(), this, dict);
    }
    
    protected Event createDerivedEvent(String type, Dictionary extraParams, Event oldEvent) {
        Dictionary params = new Hashtable();
        
        String[] keys = oldEvent.getPropertyNames();
        for (int i=0; i < keys.length; i++) {
            params.put(keys[i], oldEvent.getProperty(keys[i]));
        }
        
        if (extraParams != null) {
            for (Enumeration i=extraParams.keys(); i.hasMoreElements(); ) {
                Object key = i.nextElement();
                params.put(key, extraParams.get(key));
            }
        }
        
        return createEvent(type, params);
    }
    
    protected Event createEvent(String type, Dictionary params) {
        if (params == null) {
            params = new Hashtable();
        }
        
        params.put(TARGET_SERVICE, targetService);
        params.put(SESSION_ID, sessionID);
        params.put(EVENT_TYPE, type);
        
        if (params.get(EVENT_ID) == null)
            params.put(EVENT_ID, idGen.newID());
        
        return new Event(BASE_TOPIC, params);
    }
    
    protected synchronized Event postEventAndWait(Event event) {
        postEvent(event);
        
        Event response = null;
        while (response == null) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            response = getResponse(event);
        }
        
        return response;
    }
    
    private Event getResponse(Event event) {
        String id = (String) event.getProperty(EVENT_ID);
        
        return (Event) receivedEvents.remove(id);
    }
    
    public synchronized void handleEvent(Event event) {
        String id = (String) event.getProperty(EVENT_ID);
        
        if (id != null) {
            receivedEvents.put(id, event);
            
            notifyAll();
        }
    }
    
    protected void postEvent(Event event) {
        eventAdmin.postEvent(event);
    }
}
