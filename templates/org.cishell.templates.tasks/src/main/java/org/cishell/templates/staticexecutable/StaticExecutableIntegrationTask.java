/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 2, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.staticexecutable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Replace;
import org.apache.tools.ant.util.FileUtils;


public class StaticExecutableIntegrationTask extends Task {
    private PrintWriter mf;
    private String componentAttr;
    private File baseDir;
    private File baseProperties;
    private FileUtils util;
    private File template;
    private String symbolicName;
    
    public StaticExecutableIntegrationTask() {
        componentAttr = new String();
        util = FileUtils.getFileUtils();
        symbolicName = "unknown";
    }
    
    public void setTemplate(File file) {
        template = file;
    }
        
    public void setBasedir(File file) {
        baseDir = file;
    }
    
    public void setBaseproperties(File file) {
        this.baseProperties = file;
    }
        
    public void execute() throws BuildException {
        if (baseDir != null && baseDir.exists() && baseDir.isDirectory() &&
            baseProperties != null && baseProperties.exists() &&
            template != null && template.exists()) {
            try {
                setupManifest();
                
                File[] files = baseDir.listFiles();
                for (int i=0; i < files.length; i++) {
                    if (files[i].isDirectory() && 
                            !files[i].getName().equalsIgnoreCase("default")) {
                        addAlgorithm(files[i]);
                    }
                }
                
                writeManifest();
            } catch (Throwable e) {
                e.printStackTrace();
                throw new BuildException(e);
            }
        } else {
            throw new BuildException("Invalid or missing argument(s).");
        }
    }
    
    protected void addAlgorithm(File dir) throws IOException {
        File serviceFile = null;
        File configFile = null;
        List osDirs = new ArrayList();
        
        File[] files = dir.listFiles();
        for (int i=0; i < files.length; i++) {
            String name = files[i].getName();
            if (name.equalsIgnoreCase("config.properties")) {
                configFile = files[i];
            } else if (name.equalsIgnoreCase("service.properties")) {
                serviceFile = files[i];
            } else if (files[i].isDirectory() && 
                    !name.equalsIgnoreCase("default")) {
                osDirs.add(files[i]);
            }
        }
        
        if (serviceFile != null && configFile != null) {
            File componentFile = new File(serviceFile.getParentFile().getPath()+
                    File.separator+"component.xml");
            util.copyFile(template, componentFile);
            
            String algName = serviceFile.getParentFile().getName();
            replace(componentFile,"${service.properties}",algName+"/"+serviceFile.getName());
            replace(componentFile,"${component.id}",symbolicName+"." + algName + ".component");
            replace(componentFile,"${alg.dir}",algName);
            
            if (componentAttr.length() != 0) {
                componentAttr += ", ";
            }
            componentAttr += algName+"/component.xml";
        }
    }
    
    protected void replace(File file, String token, String value) {
        Replace replace = new Replace();
        replace.setProject(getProject());
        replace.setFile(file);
        replace.setToken(token);
        
        replace.setValue(value);
        replace.execute();
    }
    
    protected void writeManifest() {
        mf.println("Service-Component: " + componentAttr);
        mf.close();
    }
    
    protected void setupManifest() throws IOException {
        File mfDir = new File(baseDir.getPath()+File.separator+"META-INF");
        File mfFile = new File(mfDir+File.separator+"MANIFEST.MF");
        mfDir.mkdirs();
        
        mf = new PrintWriter(new FileOutputStream(mfFile));
        
        mf.println("Manifest-Version: 1.0");
        mf.println("Bundle-ManifestVersion: 2");
        
        Properties properties = new Properties();
        properties.load(new FileInputStream(baseProperties));
        
        for (Enumeration i=properties.keys(); i.hasMoreElements(); ) {
            String key = (String)i.nextElement();
            String value = properties.getProperty(key);
            
            if ("Bundle-SymbolicName".equals(key)) {
                symbolicName = value;
            }
            
            mf.println(key+": "+value);
        }
        
        mf.println("Bundle-Localization: plugin");
        mf.println("X-AutoStart: true");
        mf.println("Import-Package: org.cishell.framework.algorithm, " +
                "org.cishell.framework.data, " +
                "org.osgi.framework;version=\"1.3.0\", " +
                "org.cishell.templates.staticexecutable, " +
                "org.osgi.service.component;version=\"1.0.0\", " +
                "org.osgi.service.metatype;version=\"1.1.0\", " +
                "org.osgi.service.log");
    }
}
