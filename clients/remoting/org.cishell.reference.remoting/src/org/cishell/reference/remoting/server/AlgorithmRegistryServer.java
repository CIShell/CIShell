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
package org.cishell.reference.remoting.server;


import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.cishell.app.service.scheduler.SchedulerAdapter;
import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.reference.remoting.ObjectRegistry;
import org.cishell.reference.remoting.event.CIShellEventConstants;
import org.cishell.reference.remoting.event.IDGenerator;
import org.cishell.remoting.service.framework.AlgorithmRegistry;
import org.cishell.remoting.service.framework.DataModelRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class AlgorithmRegistryServer extends SchedulerAdapter implements AlgorithmRegistry {
    private ObjectRegistry registry;
    private ServiceTracker dmReg;
    private ServiceTracker eventReg;
    private ServiceTracker schedulerReg;
    private IDGenerator idGen;
    
    public AlgorithmRegistryServer(BundleContext bContext, CIShellContext ciContext) {
        idGen = new IDGenerator("AlgorithmRegistry-");
        registry = new ObjectRegistry();
        
        dmReg = new ServiceTracker(bContext, DataModelRegistry.class.getName(), null);
        eventReg = new ServiceTracker(bContext, EventAdmin.class.getName(), null);
        schedulerReg = new ServiceTracker(bContext, SchedulerService.class.getName(), null);
        
        dmReg.open();
        eventReg.open();
        schedulerReg.open();
        
        SchedulerService scheduler = (SchedulerService) schedulerReg.getService();
        scheduler.addSchedulerListener(this);
    }

    /**
     * @see org.cishell.remoting.service.framework.AlgorithmRegistry#execute(String, String)
     */
    public void execute(String sessionID, String algorithmID) {
        SchedulerService scheduler = (SchedulerService) schedulerReg.getService();
        
        WrapperAlgorithm alg = new WrapperAlgorithm(getAlgorithm(algorithmID), 
                sessionID, algorithmID);
        
        scheduler.runNow(alg, null);
    }

    /**
     * @see org.cishell.remoting.service.framework.AlgorithmRegistry#getAlgorithm(String)
     */
    public Algorithm getAlgorithm(String algorithmID) {
        Algorithm alg = (Algorithm) registry.getObject(algorithmID);
        return alg == null ? NULL_ALG : alg;
    }

    /**
     * @see org.cishell.remoting.service.framework.AlgorithmRegistry#registerAlgorithm(org.cishell.framework.algorithm.Algorithm)
     */
    public String registerAlgorithm(Algorithm algorithm) {
        return registry.register(algorithm);
    }

    /**
     * @see org.cishell.remoting.service.framework.AlgorithmRegistry#unregisterAlgorithm(String)
     */
    public void unregisterAlgorithm(String algorithmID) {
        registry.unregister(algorithmID);
    }
        
    public void algorithmFinished(Algorithm algorithm, Data[] createdDM) {
        if (algorithm instanceof WrapperAlgorithm) {
            WrapperAlgorithm alg = (WrapperAlgorithm) algorithm;
            
            DataModelRegistry dmRegistry = (DataModelRegistry) dmReg.getService();
            Vector dmID = dmRegistry.registerDataModels(createdDM);
            
            if (dmID == null) {
                dmID = new Vector();
            }
            
            Dictionary params = new Hashtable();
            params.put("dataModelIDs", dmID);
            params.put("algorithmID", alg.algorithmID);
            params.put(CIShellEventConstants.TARGET_SERVICE, "AlgorithmRegistry");
            params.put(CIShellEventConstants.SESSION_ID, alg.sessionID);
            params.put(CIShellEventConstants.EVENT_TYPE, "algorithmFinished");
            
            if (params.get(CIShellEventConstants.EVENT_ID) == null)
                params.put(CIShellEventConstants.EVENT_ID, idGen.newID());
            
            EventAdmin eventAdmin = (EventAdmin) eventReg.getService();
            eventAdmin.postEvent(new Event(CIShellEventConstants.BASE_TOPIC, params));
        }
    }
        
    private static final Algorithm NULL_ALG = new Algorithm() {
        public Data[] execute() {
            return null;
        }};
        
    private static class WrapperAlgorithm implements Algorithm {
        private Algorithm algorithm;
        public String sessionID;
        public String algorithmID;
        
        public WrapperAlgorithm(Algorithm algorithm, String sessionID, String algorithmID) {
            this.algorithm = algorithm;
            this.sessionID = sessionID;
            this.algorithmID = algorithmID;
        }

        public Data[] execute() throws AlgorithmExecutionException {
            return algorithm.execute();
        }
    }
}
