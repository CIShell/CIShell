/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 23, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.tests.conversion1;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.datamodel.BasicDataModel;
import org.cishell.framework.datamodel.DataModel;
import org.cishell.framework.datamodel.DataModelProperty;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;

/**
 * 
 * @author Bruce Herr
 */
public class AlgA implements AlgorithmFactory {
    private MetaTypeProvider provider;

    protected void activate(ComponentContext ctxt) {
        MetaTypeService mts = (MetaTypeService)ctxt.locateService("MTS");
        provider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle());       
    }
    protected void deactivate(ComponentContext ctxt) {
        provider = null;
    }

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createParameters(org.cishell.framework.datamodel.DataModel[])
     */
    public MetaTypeProvider createParameters(DataModel[] dm) {
        return provider;
    }

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.datamodel.DataModel[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(DataModel[] dm, Dictionary parameters,
            CIShellContext context) {
        return new AlgorithmA(parameters);
    }
    
    private class AlgorithmA implements Algorithm {
        Dictionary parameters;
        
        public AlgorithmA(Dictionary parameters) {
            this.parameters = parameters;
        }

        public DataModel[] execute() {
            String i = (String) parameters.get("org.cishell.tests.conversion1.AlgA.myInput");
            
            DataModel[] dm = new DataModel[]{ new BasicDataModel(i, String.class.getName()) };
            dm[0].getMetaData().put(DataModelProperty.LABEL, "My String: " + i);
            
            return dm;
        }
    }
}
