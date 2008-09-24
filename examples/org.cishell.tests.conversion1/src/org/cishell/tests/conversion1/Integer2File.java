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
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class Integer2File implements AlgorithmFactory {

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.data.Data[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters,
            CIShellContext context) {
        return new Integer2FileAlgorithm(dm[0]);
    }
    
    private static class Integer2FileAlgorithm implements Algorithm {
        Integer i;
        String label;
        
        public Integer2FileAlgorithm(Data dm) {
            i = (Integer)dm.getData();
            label = (String)dm.getMetadata().get(DataProperty.LABEL);
        }

        public Data[] execute() throws AlgorithmExecutionException {
            try {
                File file = File.createTempFile("String2File-", "txt");
                FileWriter fw = new FileWriter(file);
                
                fw.write(i.toString());
                fw.close();
                
                Data dm = new BasicData(file, "file:text/plain");
                dm.getMetadata().put(DataProperty.LABEL, "File of "+label);
                
                return new Data[]{dm};
            } catch (IOException e) {
                throw new AlgorithmExecutionException(e);
            }
        }
    }
}
