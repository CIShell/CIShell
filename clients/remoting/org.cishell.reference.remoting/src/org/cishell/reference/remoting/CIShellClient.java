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
package org.cishell.reference.remoting;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.reference.remoting.client.AlgorithmFactoryRegistryClient;
import org.cishell.reference.remoting.client.AlgorithmRegistryClient;
import org.cishell.reference.remoting.client.AttributeDefinitionRegistryClient;
import org.cishell.reference.remoting.client.CIShellFrameworkClient;
import org.cishell.reference.remoting.client.DataModelRegistryClient;
import org.cishell.reference.remoting.client.MetaTypeProviderRegistryClient;
import org.cishell.reference.remoting.client.ObjectClassDefinitionRegistryClient;
import org.cishell.reference.remoting.client.service.conversion.RemoteDataConversionServiceClient;
import org.cishell.reference.remoting.client.service.log.LogEventHandler;
import org.cishell.reference.remoting.event.CIShellEventConstants;
import org.cishell.reference.remoting.event.EventQueue;
import org.cishell.remoting.service.framework.AlgorithmFactoryRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;


public class CIShellClient {
    protected BundleContext bContext;
    protected CIShellFrameworkClient fw;
    protected Timer timer;
    protected String sessionID;
    protected Map algToRegMap;
    protected AlgorithmFactoryRegistry algFactoryReg;
    protected String host;
    protected EventAdmin eventAdmin;
    protected EventQueue q;
    protected LogEventHandler logHandler;
    protected CIShellContext ciContext;
    
    public CIShellClient(BundleContext bContext, CIShellContext ciContext) {
        this.bContext = bContext;
        this.ciContext = ciContext;
    }
    
    public void open(String host) {
        fw = new CIShellFrameworkClient();
        fw.open(host);
        sessionID = fw.createSession("localhost");
        
        setupEventQueue();
        
        RemoteDataConversionServiceClient remoteConverter = 
            new RemoteDataConversionServiceClient();
        AttributeDefinitionRegistryClient attrReg = 
            new AttributeDefinitionRegistryClient();
        DataModelRegistryClient dmReg = 
            new DataModelRegistryClient(ciContext, remoteConverter);
        AlgorithmRegistryClient algReg = 
            new AlgorithmRegistryClient(bContext, sessionID, dmReg);
        ObjectClassDefinitionRegistryClient ocdReg = 
            new ObjectClassDefinitionRegistryClient(attrReg);
        MetaTypeProviderRegistryClient mtpReg = 
            new MetaTypeProviderRegistryClient(ocdReg);
        AlgorithmFactoryRegistryClient algFactoryReg = 
            new AlgorithmFactoryRegistryClient(sessionID, algReg, mtpReg, dmReg);

        remoteConverter.open(host);
        algFactoryReg.open(host);
        algReg.open(host);
        attrReg.open(host);
        dmReg.open(host);
        mtpReg.open(host);
        ocdReg.open(host);
        
        this.algFactoryReg = algFactoryReg;
        this.host = host;
        
        eventAdmin = (EventAdmin) bContext.getService(
                bContext.getServiceReference(EventAdmin.class.getName()));
        
        algToRegMap = new HashMap();
        
        logHandler = new LogEventHandler(bContext, sessionID);
        
        publishAllAlgs();
        
        timer = new Timer(true);
        TimerTask task = new TimerTask() {
            public void run() {
                getNewEvents();
                putEventReplies();
            }
        };
        timer.scheduleAtFixedRate(task, 0, 2000);
    }
    
    public void close() {
        fw.closeSession(sessionID);
        timer.cancel();
        logHandler.stop();
        
        for (Iterator iter = algToRegMap.values().iterator(); iter.hasNext();) {
            ServiceRegistration reg = (ServiceRegistration) iter.next();
            
            if (reg != null) reg.unregister();
        }
        
        algToRegMap = null;
        sessionID = null;
        bContext = null;
        timer = null;
        algFactoryReg = null;
        logHandler = null;
    }
    
    private void setupEventQueue() {
        q = new EventQueue(bContext, false);
        
        String filter = "(&(reply=*)" +
            "("+CIShellEventConstants.SESSION_ID+"="+sessionID+"))";

        Dictionary dict = new Hashtable();
        dict.put(EventConstants.EVENT_TOPIC, 
                 new String[]{CIShellEventConstants.BASE_TOPIC});
        dict.put(EventConstants.EVENT_FILTER, filter);
        
        bContext.registerService(EventHandler.class.getName(), q, dict);
    }
    
    private void putEventReplies() {
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
        
        if (v.size() > 0) {
            fw.putEvents(sessionID, v);
        }
    }
    
    private void getNewEvents() {        
        Vector v = fw.getEvents(sessionID);
        
        for (int i=0; i < v.size(); i++) {
            Hashtable inEvent = (Hashtable) v.get(i);
            
            String topic = (String)inEvent.get(EventConstants.EVENT_TOPIC);
            if (topic != null) {
                Event event = new Event(topic, inEvent);
                
                if ("AlgServiceListener".equalsIgnoreCase(
                        (String)inEvent.get(CIShellEventConstants.TARGET_SERVICE))){
                    
                    processAlgEvent(event);
                } else {
                    eventAdmin.postEvent(event);
                }
            }
        }
    }
    
    private void processAlgEvent(Event inEvent) {
        String pid = (String) inEvent.getProperty(Constants.SERVICE_PID);
        String event = ((String) inEvent.getProperty(CIShellEventConstants.EVENT_TYPE));
        
        switch (Integer.parseInt(event)) {
        case ServiceEvent.MODIFIED:
            updateAlg(pid);
            break;
        case ServiceEvent.REGISTERED:
            publishAlg(pid);
            break;
        case ServiceEvent.UNREGISTERING:
            unpublishAlg(pid);
            break;
        }
    }
    
    private void publishAllAlgs() {
        Vector v = fw.getAlgorithmFactories();
        
        for (int i=0; i < v.size(); i++) {
            publishAlg((String)v.get(i));
        }
    }
    
    private void publishAlg(String servicePID) {
        Dictionary props = algFactoryReg.getProperties(servicePID);
        
        String label = (String) props.get(AlgorithmProperty.LABEL);
        props.put(AlgorithmProperty.LABEL, label + " (remote)");
        
        String pid = (String) props.get(Constants.SERVICE_PID);
        props.put(Constants.SERVICE_PID, pid + ".remote."+host);
        
        props.put(AlgorithmProperty.REMOTE, host);
        
        AlgorithmFactory factory = algFactoryReg.getAlgorithmFactory(servicePID);
        
        if (props != null && factory != null) {
            ServiceRegistration reg = bContext.registerService(
                    AlgorithmFactory.class.getName(), factory, props);
            
            algToRegMap.put(servicePID, reg);
        }
    }
    
    private void unpublishAlg(String servicePID) {
        Object o = algToRegMap.remove(servicePID);
        
        if (o != null) {
            ((ServiceRegistration) o).unregister();
        }
    }
    
    private void updateAlg(String servicePID) {
        Object o = algToRegMap.get(servicePID);
        
        if (o != null) {
            Dictionary props = algFactoryReg.getProperties(servicePID);
            ((ServiceRegistration) o).setProperties(props);
        }
    }
}
