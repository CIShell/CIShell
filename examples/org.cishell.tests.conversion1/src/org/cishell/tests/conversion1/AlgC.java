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
import org.cishell.framework.datamodel.DataModel;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr
 */
public class AlgC implements AlgorithmFactory {

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createParameters(org.cishell.framework.datamodel.DataModel[])
     */
    public MetaTypeProvider createParameters(DataModel[] dm) {
        return null;
    }

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.datamodel.DataModel[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(DataModel[] dm, Dictionary parameters,
            CIShellContext context) {
        return new AlgorithmC(dm, context);
    }
    
    private class AlgorithmC implements Algorithm {
        DataModel[] dm;
        CIShellContext ciContext;
        
        public AlgorithmC(DataModel[] dm, CIShellContext ciContext) {
            this.dm = dm;
            this.ciContext = ciContext;
        }

        public DataModel[] execute() {
            Integer i = new Integer(dm[0].getData().toString());
            LogService log = (LogService) ciContext.getService(LogService.class.getName());
            
            log.log(LogService.LOG_INFO, "I got me an integer named " + i + "!");
            
            return null;
        }
    }
}
