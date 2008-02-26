/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 10, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.reference.remoting.ObjectRegistry;
import org.cishell.reference.remoting.event.CIShellEventConstants;
import org.cishell.reference.remoting.event.EventQueue;
import org.cishell.reference.remoting.event.IDGenerator;
import org.cishell.remoting.service.framework.CIShellFramework;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;


public class CIShellFrameworkServer implements CIShellFramework {
    private BundleContext bContext;
    private EventQueue q;
    private ObjectRegistry listeners;
    private EventAdmin eventAdmin;
    
    public CIShellFrameworkServer(BundleContext bContext, CIShellContext ciContext) {
        this.bContext = bContext;
        
        q = new EventQueue(bContext);
        
        String host = "localhost";
        
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {}
        
        listeners = new ObjectRegistry(host+":8180-");
        eventAdmin = (EventAdmin) bContext.getService(
                bContext.getServiceReference(EventAdmin.class.getName()));
    }

    /**
     * @see org.cishell.remoting.service.framework.CIShellFramework#createSession(java.lang.String)
     */
    public String createSession(String clientURL) {
        AlgServiceListener listener = new AlgServiceListener();
        String filter = "(" + Constants.OBJECTCLASS + "=" 
                            + AlgorithmFactory.class.getName() + ")";
        try {
            bContext.addServiceListener(listener, filter);
            String sessionID = listeners.register(listener);
            
            listener.setSessionID(sessionID);
            
            return sessionID; 
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * @see org.cishell.remoting.service.framework.CIShellFramework#closeSession(java.lang.String)
     */
    public void closeSession(String sessionID) {
        ServiceListener listener = (ServiceListener) listeners.getObject(sessionID);
        bContext.removeServiceListener(listener);
        
        listeners.unregister(sessionID);
    }

    /**
     * @see org.cishell.remoting.service.framework.CIShellFramework#getAlgorithmFactories()
     */
    public Vector getAlgorithmFactories() {
        String filter = "(&("+Constants.SERVICE_PID+"=*)" +
                          "("+AlgorithmProperty.REMOTEABLE+"=*))";
        
        ServiceReference[] refs = null;
        try {
            refs = bContext.getServiceReferences(AlgorithmFactory.class.getName(), filter);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }
        
        Vector v = new Vector();
        
        if (refs != null) {
            for (int i=0; i < refs.length; i++) {
                v.add(refs[i].getProperty(Constants.SERVICE_PID));
            }
        }
        
        return v;
    }
    
    /**
     * @see org.cishell.remoting.service.framework.CIShellFramework#getEvents(java.lang.String)
     */
    public Vector getEvents(String sessionID) {
        int size = q.size(sessionID);
        List events = q.pop(sessionID, size);
        Vector v = new Vector(events.size());
        
        for (int i=0; i < events.size(); i++) {
            Event event = (Event) events.get(i);
            String[] keys = event.getPropertyNames();
            
            Hashtable outEvent = new Hashtable();
            outEvent.put(EventConstants.EVENT_TOPIC, event.getTopic());
            
            for (int j=0; j < keys.length; j++) {
                outEvent.put(keys[j], event.getProperty(keys[j]));
            }
            
            v.add(outEvent);
        }
        
        AlgServiceListener listener = (AlgServiceListener) listeners.getObject(sessionID);
        v.addAll(listener.getEvents());
        
        return v;
    }
    
    /**
     * @see org.cishell.remoting.service.framework.CIShellFramework#putEvents(java.lang.String, java.util.Vector)
     */
    public void putEvents(String sessionID, Vector events) {        
        for (int i=0; i < events.size(); i++) {
            Hashtable inEvent = (Hashtable) events.get(i);
            
            String topic = (String)inEvent.get(EventConstants.EVENT_TOPIC);
            if (topic != null) {
                Event event = new Event(topic, inEvent);
                
                eventAdmin.postEvent(event);
            }
        }
    }

    private static class AlgServiceListener implements ServiceListener, EventConstants, CIShellEventConstants {
        static IDGenerator idGen = new IDGenerator("AlgSL-EID-");
        Vector events = new Vector();
        String sid;
        
        public void setSessionID(String sid) {
            this.sid = sid;
        }

        public synchronized void serviceChanged(ServiceEvent event) {            
            Dictionary outEvent = new Hashtable();
            
            outEvent.put(TARGET_SERVICE, "AlgServiceListener");
            outEvent.put(SESSION_ID, sid);
            outEvent.put(EVENT_TYPE, ""+event.getType());
            outEvent.put(EVENT_ID, idGen.newID());
            outEvent.put(EventConstants.EVENT_TOPIC, BASE_TOPIC);
            
            outEvent.put(Constants.SERVICE_PID, 
                    event.getServiceReference().getProperty(Constants.SERVICE_PID));
            
            events.add(outEvent);
        }
        
        public Vector getEvents() {
            Vector v = null;
            synchronized(this) {
                v = events;
                events = new Vector();
            }
            return v;
        }
    }
}
