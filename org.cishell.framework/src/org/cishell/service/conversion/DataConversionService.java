/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 14, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.service.conversion;

import org.cishell.framework.algorithm.AlgorithmFactory;

public interface DataConversionService {
    public AlgorithmFactory converterFor(String inFormat, String outFormat);
    public AlgorithmFactory converterFor(String inFormat, String outFormat,
            int maxHops, String maxComplexity);
}
