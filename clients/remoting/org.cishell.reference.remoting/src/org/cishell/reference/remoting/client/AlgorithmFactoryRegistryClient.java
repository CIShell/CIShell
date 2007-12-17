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

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.reference.remoting.RemotingClient;
import org.cishell.remoting.service.framework.AlgorithmFactoryRegistry;
import org.cishell.remoting.service.framework.AlgorithmRegistry;
import org.cishell.remoting.service.framework.DataModelRegistry;
import org.cishell.remoting.service.framework.MetaTypeProviderRegistry;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class AlgorithmFactoryRegistryClient extends RemotingClient implements
        AlgorithmFactoryRegistry {
    protected AlgorithmRegistry algReg;
    protected MetaTypeProviderRegistry mtpReg; 
    protected DataModelRegistry dmReg;
    protected String sessionID;

    public AlgorithmFactoryRegistryClient(String sessionID, AlgorithmRegistry algReg, 
            MetaTypeProviderRegistry mtpReg, DataModelRegistry dmReg) {
        super("/soap/services/AlgorithmFactoryRegistry");
        
        this.algReg = algReg;
        this.mtpReg = mtpReg;
        this.dmReg = dmReg;
        this.sessionID = sessionID;
        
        setCacheing("createParameters", false);
        setCacheing("createAlgorithm", true);
    }
    
    /**
     * @see org.cishell.remoting.service.framework.AlgorithmFactoryRegistry#createAlgorithm(String, java.lang.String, Vector, java.util.Hashtable)
     */
    public String createAlgorithm(String sessionID, String servicePID,
            Vector dataModelIDs, Hashtable dictionary) {
        Object[] parms = new Object[] { sessionID, servicePID, dataModelIDs, dictionary };
        Object r = doCall("createAlgorithm", parms);

        return r == null ? null : r.toString();
    }

    /**
     * @see org.cishell.remoting.service.framework.AlgorithmFactoryRegistry#createParameters(java.lang.String, Vector)
     */
    public String createParameters(String servicePID, Vector dataModelIDs) {
        Object[] parms = new Object[]{servicePID, dataModelIDs};
        Object r = doCall("createParameters", parms);
                
        return r == null ? null : r.toString();
    }
    
    public Hashtable getProperties(String servicePID) {
        Object r = doCall("getProperties", servicePID);
        
        return (Hashtable) r;
    }

    /**
     * @see org.cishell.remoting.service.framework.AlgorithmFactoryRegistry#getAlgorithmFactory(java.lang.String)
     */
    public AlgorithmFactory getAlgorithmFactory(String servicePID) {
        return new RemoteAlgorithmFactory(servicePID);
    }
    
    protected class RemoteAlgorithmFactory implements AlgorithmFactory {
        private String servicePID;
        
        public RemoteAlgorithmFactory(String servicePID) {
            this.servicePID = servicePID;
        }

        public Algorithm createAlgorithm(Data[] dm, Dictionary parameters, CIShellContext context) {
            Vector dataModelIDs = dmReg.registerDataModels(dm);
            Hashtable ht = toHashtable(parameters);
            String algID = AlgorithmFactoryRegistryClient.this.createAlgorithm(sessionID, servicePID, dataModelIDs, ht);
            
            return algReg.getAlgorithm(algID); 
        }

        public MetaTypeProvider createParameters(Data[] dm) {
            Vector dataModelIDs = dmReg.registerDataModels(dm);
            String providerID = AlgorithmFactoryRegistryClient.this.createParameters(servicePID, dataModelIDs); 
            
            return mtpReg.getMetaTypeProvider(providerID);
        }
    }
}
