/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 11, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.remoting.frontend;

import java.util.Dictionary;
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.Data;
import org.cishell.reference.remoting.CIShellClient;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class ClientInstanceAlg implements AlgorithmFactory, AlgorithmProperty, Algorithm {
    private CIShellClient ciClient;
    protected CIShellContext ciContext;
    private BundleContext bContext;
    private ServiceRegistration reg;
    
    public ClientInstanceAlg(BundleContext bContext, CIShellContext ciContext) {
        this.bContext = bContext;
        this.ciContext = ciContext;
    }
    
    public void connect(String host, int port) {            
        String url = "http://" + host;
        
        if (port != 80) {
            url += ":" + port;
        }
        
        ciClient = new CIShellClient(bContext, ciContext);
        ciClient.open(url);
        
        Dictionary props = new Hashtable();
        props.put(LABEL, "from " + url);
        props.put(DESCRIPTION, "Disconnect from " + url);
        props.put(MENU_PATH, "file/Disconnect/additions");
        props.put(Constants.SERVICE_PID, getClass().getName()+"."+host+"."+port);
        
        reg = bContext.registerService(AlgorithmFactory.class.getName(), this, props);
    }
    
    public void disconnect() {
        if (ciClient != null) {
            ciClient.close();
            
            reg.unregister();
        }
    }
    
    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.data.Data[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters,
            CIShellContext context) {
        return this;
    }
    
    public Data[] execute() {
        disconnect();
        return null;
    }

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createParameters(org.cishell.framework.data.Data[])
     */
    public MetaTypeProvider createParameters(Data[] dm) {
        return null;
    }
}
