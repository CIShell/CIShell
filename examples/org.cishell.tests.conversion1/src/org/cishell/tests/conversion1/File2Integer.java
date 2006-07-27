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
import org.cishell.framework.datamodel.BasicDataModel;
import org.cishell.framework.datamodel.DataModel;
import org.cishell.framework.datamodel.DataModelProperty;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class File2Integer implements AlgorithmFactory {

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createAlgorithm(org.cishell.framework.datamodel.DataModel[], java.util.Dictionary, org.cishell.framework.CIShellContext)
     */
    public Algorithm createAlgorithm(DataModel[] dm, Dictionary parameters,
            CIShellContext context) {
        
        return new File2IntegerAlgorithm(dm[0]);
    }

    /**
     * @see org.cishell.framework.algorithm.AlgorithmFactory#createParameters(org.cishell.framework.datamodel.DataModel[])
     */
    public MetaTypeProvider createParameters(DataModel[] dm) {
        return null;
    }
    
    private static class File2IntegerAlgorithm implements Algorithm {
        File file;
        String label;
        
        public File2IntegerAlgorithm(DataModel dm) {            
            file = (File) dm.getData();
            label = (String)dm.getMetaData().get(DataModelProperty.LABEL);
        }

        public DataModel[] execute() {
            try {
                if (file.exists()) {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String outString = "";

                    String string = reader.readLine();
                    while (string != null) {
                        outString += string;
                        string = reader.readLine();
                    }
                    
                    DataModel dm = new BasicDataModel(new Integer(outString.trim()), Integer.class.getName());
                    dm.getMetaData().put(DataModelProperty.LABEL, "Integer for "+label);
                    
                    return new DataModel[]{dm};
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            return null;
        }
    }
}
