/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 7, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates;

import java.io.File;
import java.io.IOException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class Activator implements BundleActivator {
    private static File tempDirectory;

    /**
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        
    }

    /**
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        finalize();
    }
    
    protected void finalize() {
        if (tempDirectory != null) {
            delete(tempDirectory);
            tempDirectory = null;
        }
    }
    
    private void delete(File dir) {
        File[] files = dir.listFiles();
        
        for (int i=0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                delete(files[i]);
            } else {
                files[i].delete();
            }
        }
        
        dir.delete();
    }

    public synchronized static File getTempDirectory() {
        if (tempDirectory == null) {
            try {
                tempDirectory = File.createTempFile("CIShell-Session-", "");
                
                tempDirectory.delete();
                tempDirectory.mkdirs();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return tempDirectory;
    }
}
