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
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class File2String implements AlgorithmFactory {

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.data.Data[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(Data[] dm, Dictionary parameters,
            CIShellContext context) {
        
        return new File2StringAlgorithm(dm[0]);
    }

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createParameters(org.cishell.framework.data.Data[])
     */
    public MetaTypeProvider createParameters(Data[] dm) {
        return null;
    }
    
    private static class File2StringAlgorithm implements Algorithm {
        File file;
        String label;
        
        public File2StringAlgorithm(Data dm) {
            file = (File) dm.getData();
            label = (String)dm.getMetaData().get(DataProperty.LABEL);
        }

        public Data[] execute() {
            try {
                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String outString = "";

                    String string = reader.readLine();
                    while (string != null) {
                        outString += string;
                        string = reader.readLine();
                    }
                    
                    Data dm = new BasicData(outString, String.class.getName());
                    dm.getMetaData().put(DataProperty.LABEL, "String for "+label);
                    
                    return new Data[]{dm};
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return null;
        }
    }
}
