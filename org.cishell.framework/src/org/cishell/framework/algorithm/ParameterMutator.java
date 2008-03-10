/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Mar 7, 2008 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.framework.algorithm;

import org.cishell.framework.data.Data;
import org.osgi.service.metatype.AttributeDefinition;
import org.osgi.service.metatype.ObjectClassDefinition;

/**
 * An additional interface an {@link AlgorithmFactory} can implement that allows
 * for adding, modifying, or removing input parameters before being shown to the
 * end-user for input. This interface is often implemented by algorithms that 
 * wish to customize the user interface based on the actual input data.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface ParameterMutator {

    /**
     * Adds, modifies, or removes {@link Algorithm} parameters 
     * ({@link AttributeDefinition}s) from a given {@link ObjectClassDefinition}
     * returning either the same (if no changes are made) input or a new, 
     * mutated version of the input
     * 
     * @param data An optional argument, the Data array that will be given to 
     *             this class to create an Algorithm with the createAlgorithm 
     *             method. Applications that don't know the Data array that is going
     *             to be used ahead of time can give a <code>null</code> value. 
     * @param parameters A set of AttributeDefinitions which define the 
     *             algorithm's input parameters
     * @return An OSGi {@link ObjectClassDefinition} that defines the parameters
     *             needed by the Algorithm this class creates
     */
	public ObjectClassDefinition mutateParameters(Data[] data, ObjectClassDefinition parameters);
}
