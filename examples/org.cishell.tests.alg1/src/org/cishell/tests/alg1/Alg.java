/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 22, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.tests.alg1;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.osgi.service.log.LogService;

public class Alg implements Algorithm {
    CIShellContext context;
    Dictionary parameters;
    
    public Alg(CIShellContext context, Dictionary parameters) {
        this.context = context;
        this.parameters = parameters;
    }

    public Data[] execute() {
        LogService log = (LogService)context.getService(LogService.class.getName());
        
        log.log(LogService.LOG_INFO, "My Parameters:");
        for(Enumeration keys = parameters.keys();keys.hasMoreElements();) {
            String key = (String)keys.nextElement();
            log.log(LogService.LOG_INFO, key + "->" + parameters.get(key));
        }
        
        Dictionary dict = new Hashtable();
        dict.put(DataProperty.LABEL, "Weee!!!");
        
        return new Data[] {new BasicData(dict, "Weee!!!", String.class.getName())};
    }
}