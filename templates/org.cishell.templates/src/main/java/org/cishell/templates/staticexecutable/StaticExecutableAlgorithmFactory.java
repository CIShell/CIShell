/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 31, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.staticexecutable;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;


public class StaticExecutableAlgorithmFactory implements AlgorithmFactory {
    BundleContext bContext;
    String algName;
    MetaTypeProvider provider;
    
    public StaticExecutableAlgorithmFactory() {}
  
    protected void activate(ComponentContext ctxt) {
        bContext = ctxt.getBundleContext();
        algName = (String)ctxt.getProperties().get("Algorithm-Directory");
        
        try {
            MetaTypeService mts = (MetaTypeService) ctxt.locateService("MTS");
            provider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    protected void deactivate(ComponentContext ctxt) {
        bContext = null;
        algName = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new StaticExecutableAlgorithm(data, parameters, context, this.bContext, this.algName);
    }

    public MetaTypeProvider createParameters(Data[] data) {
        return provider;
    }
        
    public StaticExecutableAlgorithmFactory(String algName, BundleContext bContext) {
    	this.algName = algName;
    	this.bContext = bContext;
    }
}
