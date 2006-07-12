/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 15, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.service.conversion;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class DataConversionServiceImpl implements DataConversionService, AlgorithmProperty {
    private BundleContext bcontext;
    private CIShellContext ciContext;
    
    public DataConversionServiceImpl(BundleContext bcontext, CIShellContext ciContext) {
        this.bcontext = bcontext;
        this.ciContext = ciContext;
    }

    /**
     * @see org.cishell.service.conversion.DataConversionService#converterFor(java.lang.String, java.lang.String)
     */
    public AlgorithmFactory converterFor(String inFormat, String outFormat) {
        try {
            String filter = "(&("+IN_DATA+"="+inFormat+") " +
                              "("+OUT_DATA+"="+outFormat+"))";

            ServiceReference[] refs = bcontext.getServiceReferences(
                    AlgorithmFactory.class.getName(), filter);
            if (refs != null && refs.length > 0) {
                return (AlgorithmFactory)bcontext.getService(refs[0]);
            }
        } catch (InvalidSyntaxException e) {
            getLog().log(LogService.LOG_ERROR, "Incorrect Syntax", e);
        }
        return null;
    }

    /**
     * @see org.cishell.service.conversion.DataConversionService#converterFor(java.lang.String, java.lang.String, int, java.lang.String)
     */
    public AlgorithmFactory converterFor(String inFormat, String outFormat,
            int maxHops, String maxComplexity) {
        return converterFor(inFormat, outFormat);
    }
    
    private LogService getLog() {
        return (LogService)ciContext.getService(LogService.class.getName());
    }
}
