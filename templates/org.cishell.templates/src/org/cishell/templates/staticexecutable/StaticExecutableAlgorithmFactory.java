/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 31, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.staticexecutable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.metatype.MetaTypeProvider;
import org.osgi.service.metatype.MetaTypeService;


public class StaticExecutableAlgorithmFactory implements AlgorithmFactory {
    BundleContext bContext;
    String algName;
    MetaTypeProvider provider;

    protected void activate(ComponentContext ctxt) {
        bContext = ctxt.getBundleContext();
        algName = (String)ctxt.getProperties().get("Algorithm-Directory");
        
        try {
            MetaTypeService mts = (MetaTypeService) ctxt.locateService("MTS");
            provider = mts.getMetaTypeInformation(ctxt.getBundleContext().getBundle());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    protected void deactivate(ComponentContext ctxt) {
        bContext = null;
        algName = null;
    }

    public Algorithm createAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
        return new StaticExecutableAlgorithm(data, parameters, context);
    }

    public MetaTypeProvider createParameters(Data[] data) {
        return provider;
    }
    
    private class StaticExecutableAlgorithm implements Algorithm {
        Data[] data;
        Dictionary parameters;
        CIShellContext context;
        
        public StaticExecutableAlgorithm(Data[] data, Dictionary parameters, CIShellContext context) {
            this.data = data;
            this.parameters = parameters;
            this.context = context;
        }

        public Data[] execute() {
            try {
                Properties serviceProps = getProperties("/"+algName+"/service.properties");
                Properties configProps = getProperties("/"+algName+"/config.properties");
                
                serviceProps.putAll(configProps);
                serviceProps.put("Algorithm-Directory", algName);
                
                StaticExecutableRunner runner = 
                    new StaticExecutableRunner(bContext, context, serviceProps, parameters, data);
                
                copyFiles(runner.getTempDirectory());
            
                return runner.execute();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        
        private void copyFiles(File dir) throws IOException {
            Enumeration e = bContext.getBundle().getEntryPaths("/"+algName);
            
            dir = new File(dir.getPath() + File.separator + algName);
            dir.mkdirs();
            
            String os = bContext.getProperty("osgi.os");
            String arch = bContext.getProperty("osgi.arch");
            boolean foundExecutable = false;
            
            while (e != null && e.hasMoreElements()) {
                String path = (String)e.nextElement();
                
                if (path.endsWith("/")) {
                    if (path.endsWith("default/")) {
                        copyDir(dir, path);
                    } else if (path.endsWith(os+"."+arch+"/") ||
                               path.endsWith("win32/") && os.equals("win32")) {
                        copyDir(dir, path);
                        foundExecutable = true;
                    }
                } else {
                    //copyFile(dir, path);
                }
            }
            
            if (!foundExecutable) {
                throw new RuntimeException("Unable to find compatible executable");
            }
        }
        
        private void copyDir(File dir, String dirPath) throws IOException {
            Enumeration e = bContext.getBundle().getEntryPaths(dirPath);
            
            dirPath = dirPath.replace('/', File.separatorChar);
            
            while (e != null && e.hasMoreElements()) {
                String path = (String)e.nextElement();
                
                if (path.endsWith("/")) {
                    String dirName = getName(path);
                    
                    dir = new File(dir.getPath() + File.separator + dirName);
                    dir.mkdirs();
                    copyDir(dir, path);
                } else {
                    copyFile(dir, path);
                }
            }
        }
        
        private void copyFile(File dir, String path) throws IOException {
            URL entry = bContext.getBundle().getEntry(path);
            
            path = path.replace('/', File.separatorChar);
            String file = getName(path);
            FileOutputStream outStream = new FileOutputStream(dir.getPath() + File.separator + file);
            
            ReadableByteChannel in = Channels.newChannel(entry.openStream());
            FileChannel out = outStream.getChannel();
            out.transferFrom(in, 0, Integer.MAX_VALUE);
            
            in.close();
            out.close();
        }
        
        private String getName(String path) {
            if (path.lastIndexOf(File.separator) == path.length()-1) {
                path = path.substring(0, path.length()-1);
            }
            
            path = path.substring(path.lastIndexOf(File.separatorChar)+1, 
                    path.length());
            
            return path;
        }
        
        private Properties getProperties(String entry) throws IOException {
            URL url = bContext.getBundle().getEntry(entry);
            Properties props = null;
            
            if (url != null) {
                props = new Properties();
                props.load(url.openStream());
            }
            return props;
        }
    }
}
