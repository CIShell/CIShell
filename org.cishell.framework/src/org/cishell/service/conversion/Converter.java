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
import org.cishell.framework.data.Data;
import org.osgi.framework.ServiceReference;

/**
 * TODO: Documentation for Converter
 */
public interface Converter {
    
    /**
     * Returns an array of ServiceReferences to converters in the order that
     * they will be called when converting a Data
     *  
     * @return An array ServiceReferences to converters to be used 
     */
    public ServiceReference[] getConverterChain();
    
    /**
     * Returns the AlgorithmFactory that can be invoked to convert a given 
     * Data of the correct in format (as specified in the Dictionary from
     * getProperties()) to a Data of the correct out format.
     * 
     * @return The AlgorithmFactory to do the converting
     */
    public AlgorithmFactory getAlgorithmFactory();
    
    /**
     * A set of properties that correspond to the {@link AlgorithmProperty}s 
     * properties. The IN_DATA and OUT_DATA properties are guaranteed to be set
     * in this Dictionary.
     * 
     * @return A set of properties describing the converter (including its in
     *         and out data)
     */
    public Dictionary getProperties();
    
    /**
     * Convenience method to use this converter to convert a Data of the 
     * corrent format to the a Data of the defined out format
     * 
     * @param dm The Data with compatible in format
     * @return A Data of correct out format, or <code>null</code> if the
     *         conversion fails
     */
    public Data convert(Data dm);
}
