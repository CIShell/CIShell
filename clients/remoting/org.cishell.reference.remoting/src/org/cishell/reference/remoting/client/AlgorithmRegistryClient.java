/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 6, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.client;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Vector;

import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.cishell.reference.remoting.RemotingClient;
import org.cishell.reference.remoting.event.AbstractEventConsumerProducer;
import org.cishell.remoting.service.framework.AlgorithmRegistry;
import org.cishell.remoting.service.framework.DataModelRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class AlgorithmRegistryClient extends RemotingClient implements
        AlgorithmRegistry {
    protected DataModelRegistry dmReg;
    protected BundleContext bContext;
    protected String sessionID;

    public AlgorithmRegistryClient(BundleContext bContext, String sessionID, DataModelRegistry dmReg) {
        super("/soap/services/AlgorithmRegistry");
        this.bContext = bContext;
        this.sessionID = sessionID;
        this.dmReg = dmReg;
    }
    
    /**
     * @see org.cishell.remoting.service.framework.AlgorithmRegistry#execute(String, String)
     */
    public void execute(String sessionID, String algorithmID) {
        doCall("execute", new Object[]{sessionID, algorithmID});
    }

    /**
     * @see org.cishell.remoting.service.framework.AlgorithmRegistry#getAlgorithm(String)
     */
    public Algorithm getAlgorithm(String algorithmID) {
        return new RemoteAlgorithm(bContext, sessionID, algorithmID);
    }

    /**
     * @see org.cishell.remoting.service.framework.AlgorithmRegistry#registerAlgorithm(org.cishell.framework.algorithm.Algorithm)
     */
    public String registerAlgorithm(Algorithm algorithm) {
        return "-1";
    }

    /**
     * @see org.cishell.remoting.service.framework.AlgorithmRegistry#unregisterAlgorithm(String)
     */
    public void unregisterAlgorithm(String algorithmID) {
        doCall("unregisterAlgorithm", algorithmID);
    }
    
    protected class RemoteAlgorithm extends AbstractEventConsumerProducer implements Algorithm {
        private String algorithmID;
        private Vector dmID;
        
        public RemoteAlgorithm(BundleContext bContext, String sessionID, String algorithmID) {
            super(bContext, sessionID,"AlgorithmRegistry", false);
            this.algorithmID = algorithmID;
            
            registerService();
        }
        
        protected void registerService() {
            String filter = "(&("+TARGET_SERVICE+"="+targetService+")" +
                              "("+SESSION_ID+"="+sessionID+")" +
                              "(algorithmID="+algorithmID+"))";

            Dictionary dict = new Hashtable();
            dict.put(EVENT_TOPIC, new String[]{BASE_TOPIC});
            dict.put(EVENT_FILTER, filter);
            
            bContext.registerService(EventHandler.class.getName(), this, dict);
        }

        public synchronized Data[] execute() {
            AlgorithmRegistryClient.this.execute(sessionID, algorithmID);
            
            while (dmID == null) {
                try {
                    wait(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            
            return dmReg.getDataModels(dmID); 
        }
        
        public synchronized void handleEvent(Event event) {
            dmID = (Vector) event.getProperty("dataModelIDs");
            
            if (dmID != null) {
                notifyAll();
            }
        }
        
        protected void finalize() {
            unregisterAlgorithm(algorithmID);
        }
    }
}
