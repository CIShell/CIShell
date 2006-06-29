/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 13, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework;

import org.cishell.service.conversion.DataConversionService;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.osgi.service.log.LogService;
import org.osgi.service.prefs.PreferencesService;


public interface CIShellContext {
    public static final String[] DEFAULT_SERVICES = 
            new String[] { LogService.class.getName(), 
                           PreferencesService.class.getName(),
                           DataConversionService.class.getName(),
                           GUIBuilderService.class.getName()};
    
    public Object getService(String service);
}
