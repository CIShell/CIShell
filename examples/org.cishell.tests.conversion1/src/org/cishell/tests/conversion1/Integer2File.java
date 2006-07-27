/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 21, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.tests.conversion1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.datamodel.BasicDataModel;
import org.cishell.framework.datamodel.DataModel;
import org.cishell.framework.datamodel.DataModelProperty;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class Integer2File implements AlgorithmFactory {

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.datamodel.DataModel[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(DataModel[] dm, Dictionary parameters,
            CIShellContext context) {
        
        return new Integer2FileAlgorithm(dm[0]);
    }

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createParameters(org.cishell.framework.datamodel.DataModel[])
     */
    public MetaTypeProvider createParameters(DataModel[] dm) {
        return null;
    }
    
    private static class Integer2FileAlgorithm implements Algorithm {
        Integer i;
        String label;
        
        public Integer2FileAlgorithm(DataModel dm) {
            i = (Integer)dm.getData();
            label = (String)dm.getMetaData().get(DataModelProperty.LABEL);
        }

        public DataModel[] execute() {
            try {
                File file = File.createTempFile("String2File-", "txt");
                FileWriter fw = new FileWriter(file);
                
                fw.write(i.toString());
                fw.close();
                
                DataModel dm = new BasicDataModel(file, "file:text/plain");
                dm.getMetaData().put(DataModelProperty.LABEL, "File of "+label);
                
                return new DataModel[]{dm};
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return null;
        }
    }
}
