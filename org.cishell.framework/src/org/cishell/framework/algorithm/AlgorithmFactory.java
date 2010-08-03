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
import org.osgi.service.metatype.MetaTypeService;

/**
 * A service interface for creating {@link Algorithm}s to be executed.
 * An algorithm developer must create an implementation of this interface and 
 * register it (along with some standard metadata about the algorithm, defined 
 * in the {@link AlgorithmProperty} class) in the OSGi service registry. 
 * If the algorithm requires input in addition to the raw data provided, a 
 * {@link MetaTypeProvider} must be published to OSGi's {@link MetaTypeService} 
 * (usually through a METADATA.XML file in the algorithm's bundle).
 * 
 * See the <a href="http://cishell.org/dev/docs/spec/cishell-spec-1.0.pdf">
 * CIShell Specification 1.0</a> for documentation on the full requirements for
 * algorithm creation.
 * 
 */
public interface AlgorithmFactory {
	
    /**
     * Creates an {@link Algorithm} to be executed
     * 
     * @param data       The data to be given to the Algorithm to process. 
     *                   Some Algorithms may ignore this value. The order and
     *                   type of data given are specified in the service
     *                   dictionary (the 'in_data' key) when registered as a 
     *                   service in OSGi.
     * @param parameters A set of key-value pairs that were created based on 
     *                   the associated input specification published to the 
     *                   {@link MetaTypeService}
     * @param ciShellContext    The context by which the Algorithm can gain access to 
     *                   standard CIShell services
     * @return An <code>Algorithm</code> primed for execution
     */
	/* TODO: Add AlgorithmCreationCanceledException and AlgorithmCreationFailedException to
	 *  the signature, and update the entire code base to conform to it.
	 */
    public Algorithm createAlgorithm(
    		Data[] data, Dictionary<String, Object> parameters, CIShellContext ciShellContext);
}
