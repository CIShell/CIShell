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
import org.cishell.framework.datamodel.DataModel;
import org.osgi.service.metatype.MetaTypeProvider;

public interface AlgorithmFactory {
    public MetaTypeProvider createParameters(DataModel[] dm);
    public Algorithm newInstance(DataModel[] dm, 
                                 Dictionary parameters,
                                 CIShellContext context);
}
