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
import org.cishell.framework.data.Data;
import org.osgi.service.log.LogService;

/**
 * 
 * @author Bruce Herr
 */
public class AlgC implements AlgorithmFactory {
    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.data.Data[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters,
            CIShellContext context) {
        return new AlgorithmC(dm, context);
    }
    
    private class AlgorithmC implements Algorithm {
        Data[] dm;
        CIShellContext ciContext;
        
        public AlgorithmC(Data[] dm, CIShellContext ciContext) {
            this.dm = dm;
            this.ciContext = ciContext;
        }

        public Data[] execute() {
            Integer i = new Integer(dm[0].getData().toString());
            LogService log = (LogService) ciContext.getService(LogService.class.getName());
            
            log.log(LogService.LOG_INFO, "I got me an integer named " + i + "!");
            
            return null;
        }
    }
}
