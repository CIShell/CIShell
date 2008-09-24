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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
public class File2Integer implements AlgorithmFactory {

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.data.Data[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters,
            CIShellContext context) {
        
        return new File2IntegerAlgorithm(dm[0]);
    }
    
    private static class File2IntegerAlgorithm implements Algorithm {
        File file;
        String label;
        
        public File2IntegerAlgorithm(Data dm) {            
            file = (File) dm.getData();
            label = (String)dm.getMetadata().get(DataProperty.LABEL);
        }

        public Data[] execute() throws AlgorithmExecutionException {
            try {
                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String outString = "";

                    String string = reader.readLine();
                    while (string != null) {
                        outString += string;
                        string = reader.readLine();
                    }
                    
                    Data dm = new BasicData(new Integer(outString.trim()), Integer.class.getName());
                    dm.getMetadata().put(DataProperty.LABEL, "Integer for "+label);
                    
                    return new Data[]{dm};
                } else {
                	throw new AlgorithmExecutionException("File does not exist!");
                }
            } catch (IOException e) {
                throw new AlgorithmExecutionException(e);
            }
        }
    }
}
