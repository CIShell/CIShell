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
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;

/**
 * 
 * @author Bruce Herr
 */
public class AlgA implements AlgorithmFactory {
    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.data.Data[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters,
            CIShellContext context) {
        return new AlgorithmA(parameters);
    }
    
    private class AlgorithmA implements Algorithm {
        Dictionary parameters;
        
        public AlgorithmA(Dictionary parameters) {
            this.parameters = parameters;
        }

        public Data[] execute() {
            String i = (String) parameters.get("org.cishell.tests.conversion1.AlgA.myInput");
            
            Data[] dm = new Data[]{ new BasicData(i, String.class.getName()) };
            dm[0].getMetadata().put(DataProperty.LABEL, "My String: " + i);
            
            return dm;
        }
    }
}
