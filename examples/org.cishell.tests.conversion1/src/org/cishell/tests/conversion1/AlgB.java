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
import org.cishell.framework.algorithm.DataValidator;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr
 */
public class AlgB implements AlgorithmFactory, DataValidator {
    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.data.Data[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters,
            CIShellContext context) {
        return new AlgorithmB(dm);
    }
    
    public String validate(Data[] dm) {
        try {
            new Integer(dm[0].getData().toString());
            return "";
        } catch (NumberFormatException e) {
            return dm[0].getData().toString() + " is not an integer!";
        }
    }
    
    private class AlgorithmB implements Algorithm {
        Data[] dm;
        
        public AlgorithmB(Data[] dm) {
            this.dm = dm;
        }

        public Data[] execute() {
            Integer i = new Integer(dm[0].getData().toString());           
            
            Data[] dm1 = new Data[]{ new BasicData(i, Integer.class.getName()) };
            dm1[0].getMetadata().put(DataProperty.LABEL, "My Integer: " + i);
            //dm1[0].getProperties().put(DataProperty.PARENT, dm[0]);
            
            return dm1;
        }
    }
}
