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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;


public class EventQueue implements EventHandler, CIShellEventConstants, EventConstants {
    protected Map queues;
    protected BundleContext bContext;
    
    public EventQueue(BundleContext bContext, boolean doRegister) {
        this.bContext = bContext;
        queues = new HashMap();
        
        if (doRegister) 
            registerService();
    }
    
    public EventQueue(BundleContext bContext) {
        this(bContext, true);
    }
    
    protected void registerService() {
        Dictionary dict = new Hashtable();
        dict.put(EVENT_TOPIC, new String[]{BASE_TOPIC});
        dict.put(EVENT_FILTER, "(!(reply=*))");

        bContext.registerService(EventHandler.class.getName(), this, dict);
    }

    public void handleEvent(Event e) {
        List q = getQueue((String)e.getProperty(SESSION_ID));
        q.add(e);
    }
    
    public List pop(String id, int maxSize) {
        List objects = new ArrayList(maxSize);
        List q = getQueue(id);
        
        for (int i=0; i < maxSize; i++) {
            synchronized (q) {
                if (q.size() > 0) {
                    objects.add(q.remove(0));
                } else {
                    break;
                }
            }
        }
        
        return objects;
    }
    
    public int size(String id) {
        return getQueue(id).size();
    }
    
    public synchronized List getQueue(String id) {
        List queue = (List) queues.get(id);
        
        if (queue == null) {
            queue = Collections.synchronizedList(new ArrayList());
            queues.put(id, queue);
        }
        
        return queue;
    }
    
    public void removeQueue(String id) {
        queues.remove(id);
    }
}
