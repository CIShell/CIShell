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
import org.cishell.framework.algorithm.DataModelValidator;
import org.cishell.framework.datamodel.BasicDataModel;
import org.cishell.framework.datamodel.DataModel;
import org.cishell.framework.datamodel.DataModelProperty;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr
 */
public class AlgB implements AlgorithmFactory, DataModelValidator {

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
        return new AlgorithmB(dm);
    }
    
    public String validate(DataModel[] dm) {
        try {
            new Integer(dm[0].getData().toString());
            return "";
        } catch (NumberFormatException e) {
            return dm[0].getData().toString() + " is not an integer!";
        }
    }
    
    private class AlgorithmB implements Algorithm {
        DataModel[] dm;
        
        public AlgorithmB(DataModel[] dm) {
            this.dm = dm;
        }

        public DataModel[] execute() {
            Integer i = new Integer(dm[0].getData().toString());           
            
            DataModel[] dm1 = new DataModel[]{ new BasicDataModel(i, Integer.class.getName()) };
            dm1[0].getMetaData().put(DataModelProperty.LABEL, "My Integer: " + i);
            //dm1[0].getProperties().put(DataModelProperty.PARENT, dm[0]);
            
            return dm1;
        }
    }
}
