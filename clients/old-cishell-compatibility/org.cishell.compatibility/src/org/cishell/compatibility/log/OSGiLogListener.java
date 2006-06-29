/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.compatibility.log;

import org.cishell.framework.CIShellContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.messaging.ConsoleLevel;
import edu.iu.iv.core.messaging.ConsoleManager;

/**
 * 
 * @author Bruce Herr
 */
public class OSGiLogListener implements LogListener {
    private BundleContext bContext;

    public OSGiLogListener(BundleContext bContext,CIShellContext ciContext) {
        this.bContext = bContext;
        
        ServiceReference ref = bContext.getServiceReference(LogReaderService.class.getName());
        LogReaderService reader = (LogReaderService)bContext.getService(ref);
        reader.addLogListener(this);    
    }

    /**
     * @see org.osgi.service.log.LogListener#logged(org.osgi.service.log.LogEntry)
     */
    public void logged(LogEntry entry) {
        ConsoleManager console = IVC.getInstance().getConsole();
        
        ConsoleLevel level;
        switch (entry.getLevel()) {
        case LogService.LOG_INFO:
            level = ConsoleLevel.ALGORITHM_INFORMATION;
            break;
        case LogService.LOG_ERROR:
            level = ConsoleLevel.SYSTEM_ERROR;
            break;
        case LogService.LOG_WARNING:
            level = ConsoleLevel.SYSTEM_WARNING;
            break;
        case LogService.LOG_DEBUG:
            level = ConsoleLevel.SYSTEM_INFORMATION;
            break;
        default:
            level = ConsoleLevel.ALGORITHM_INFORMATION;
        }
        
        
        if (goodMessage(entry.getMessage())) {
            console.print(entry.getMessage() + "\n", level);
        }
    }
    
    public boolean goodMessage(String msg) {
        if (msg == null || 
                msg.startsWith("ServiceEvent ") || 
                msg.startsWith("BundleEvent ") || 
                msg.startsWith("FrameworkEvent ")) {
            return false;
        } else {
            return true;   
        }
    }
    
    public void stop() {
        ServiceReference ref = bContext.getServiceReference(LogReaderService.class.getName());
        LogReaderService reader = (LogReaderService)bContext.getService(ref);
        if (reader != null) reader.removeLogListener(this);
        
        bContext = null;
    }
}
