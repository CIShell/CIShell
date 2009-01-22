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

/**
 * The context by which algorithms in the framework can gain access to standard
 * CIShell services. An instantiated CIShellContext must provide access to at 
 * least the default services (as of the 1.0 specification, the OSGi 
 * {@link LogService}, the OSGi {@link PreferencesService}, the 
 * CIShell defined {@link DataConversionService}, and the CIShell defined 
 * {@link GUIBuilderService}). Other services may be made available through
 * this class, but anything beyond the standard services is not guaranteed.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface CIShellContext {
    
    /**
     * Contains an array of the valid strings corresponding to the default services
     */
    public static final String[] DEFAULT_SERVICES = 
            new String[] { LogService.class.getName(), 
                           PreferencesService.class.getName(),
                           DataConversionService.class.getName(),
                           GUIBuilderService.class.getName()};
    
    /**
     * Locates and returns a service given the service name. The 
     * service name is generally the full class name of the service interface.
     * Standard CIShell services are guaranteed to be returned, but requests
     * for non-standard services may return null.
     * For example, <code>LogService</code>'s string is 
     * <code>org.osgi.service.log.LogService</code>.
     * 
     * @param service A string (usually the associated interface's full 
     *                class name) that specifies the service to retrieve
     * @return An instantiated version of the service requested
     */
    public Object getService(String service);
}
