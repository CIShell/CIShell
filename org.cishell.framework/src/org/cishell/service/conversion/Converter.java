/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 20, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.service.conversion;

import java.util.Dictionary;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.datamodel.DataModel;
import org.osgi.framework.ServiceReference;

/**
 * TODO: Documentation for Converter
 */
public interface Converter {
    public ServiceReference[] getConverterChain();
    public AlgorithmFactory getAlgorithmFactory();
    public Dictionary getProperties();
    
    public DataModel convert(DataModel dm);
}
