/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 7, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework.algorithm;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.data.Data;
import org.osgi.service.metatype.MetaTypeProvider;

/**
 * A class for creating {@link Algorithm}s. This class provides the 
 * parameters needed by its associated <code>Algorithm</code> on demand and when
 * given correct data, will create an <code>Algorithm</code> that can be executed. 
 * An algorithm developer who wishes to be usable by CIShell applications must 
 * create an implementation of this interface and register it (along with some 
 * standard metadata about the algorithm, defined in the 
 * {@link AlgorithmProperty} class) in the OSGi service registry. 
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface AlgorithmFactory {
    /**
     * Creates a set of parameter definitions that define what parameters are 
     * needed in order to run its associated Algorithm.  
     * 
     * @param data An optional argument, the Data array that will be given to 
     *           this class to create an Algorithm with the createAlgorithm 
     *           method. Clients that don't know the Data array that is going
     *           to be used ahead of time can give a <code>null</code> value. 
     * @return An OSGi {@link MetaTypeProvider} that defines the parameters
     *         needed by the Algorithm this class creates. May be 
     *         <code>null</code> if no parameters are needed.
     */
    public MetaTypeProvider createParameters(Data[] data);
    
    /**
     * Creates an {@link Algorithm} to be executed. 
     * 
     * @param data       The data to be given to the Algorithm to process. 
     *                   Some Algorithms may ignore this value. The order and
     *                   type of data given are specified in the service
     *                   dictionary (the 'in_data' key) when registered as a 
     *                   service in OSGi.
     * @param parameters A set of key-value pairs that were created based on 
     *                   the parameters given by the createParameters method.
     * @param context    The context by which the Algorithm can gain access to 
     *                   standard CIShell services
     * @return An <code>Algorithm</code> primed for execution
     */
    public Algorithm createAlgorithm(Data[] data, 
                                     Dictionary parameters,
                                     CIShellContext context);
}
