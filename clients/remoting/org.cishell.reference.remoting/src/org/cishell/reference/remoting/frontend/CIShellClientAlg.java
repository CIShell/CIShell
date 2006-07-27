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
package org.cishell.reference.remoting.frontend;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;


public class CIShellClientAlg implements AlgorithmFactory {
    private BundleContext bContext;
    private MetaTypeProvider provider;
    
    protected void activate(ComponentContext ctxt) {
        bContext = ctxt.getBundleContext();
        
        MetaTypeService mts = (MetaTypeService)ctxt.locateService("MTS");
        provider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle()); 
    }
    
    protected void deactivate(ComponentContext ctxt) {
        bContext = null;
        provider = null;
    }
    
    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.data.Data[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(Data[] dm, final Dictionary parameters,
            final CIShellContext context) {
        
        Algorithm alg = new Algorithm() {
            public Data[] execute() {
                String host = (String) parameters.get("org.cishell.reference.remoting.frontend.client.host");
                int port = ((Integer) parameters.get("org.cishell.reference.remoting.frontend.client.port")).intValue();
                
                new ClientInstanceAlg(bContext, context).connect(host, port);
                return null;
            }};
        
        return alg;
    }

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createParameters(org.cishell.framework.data.Data[])
     */
    public MetaTypeProvider createParameters(Data[] dm) {
        return provider;
    }
}
