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
package org.cishell.templates.dataset;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Replace;
import org.apache.tools.ant.util.FileUtils;


public class DatasetIntegrationTask extends Task {
    private PrintWriter mf;
    private String componentAttr;
    private File baseDir;
    private Properties baseProperties;
    private FileUtils util;
    private File template;
    private String symbolicName;
    
    public DatasetIntegrationTask() {
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
    
    public void setBaseproperties(File file) throws IOException {
        baseProperties = new Properties();
        baseProperties.load(new FileInputStream(file));
    }
        
    public void execute() throws BuildException {
        if (baseDir != null && baseDir.exists() && baseDir.isDirectory() &&
            baseProperties != null && template != null && template.exists()) {
            try {
                setupManifest();
                
                File dataDir = new File(baseDir.getAbsolutePath() + File.separator + "data");
                
                if (dataDir.exists() && dataDir.isDirectory()) {
                    processDir(dataDir);
                } else {
                    throw new BuildException("Missing required 'data' directory");
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
    
    protected void processDir(File dir) throws IOException {
        File[] files = dir.listFiles(new FileFilter(){
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                } else {
                    return pathname.getName().endsWith(".properties");
                }
            }});
        
        for (int i=0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                processDir(files[i]);
            } else {
                addDataset(files[i]);
            }
        }
    }
    
    protected void addDataset(File dataProps) throws IOException {
        File dataFile = new File(dataProps.getParent() + File.separator + 
                dataProps.getName().substring(0, dataProps.getName().length()-11));
        
        if (!dataFile.exists()) {
            throw new BuildException("Data file: " + dataFile.getName() + " for .properties file does not exist!");
        }
        
        PrintWriter out = new PrintWriter(new FileWriter(dataProps, true));
        out.println();
        out.println("type=dataset");
        out.println("remoteable=true");

        String symbolicDir = "";
        File parent = dataProps.getParentFile();
        while (parent != null && !parent.getName().equals("data") &&
                !parent.getParent().equals(baseDir.getName())) {
            symbolicDir = parent.getName() + "." + symbolicDir;
            parent = parent.getParentFile();
        }
        symbolicDir = "." + symbolicDir;
        
        String pid = symbolicName + dataFile.getName();
        
        out.println("service.pid="+pid);
        out.close();

        //now /data/dir1/dir2/
        symbolicDir = "/data" + symbolicDir;
        
        File componentFile = new File(dataFile.getPath()+".component.xml");
        util.copyFile(template, componentFile);
               
        replace(componentFile, "${service.properties}", symbolicDir+dataProps.getName());
        replace(componentFile, "${component.id}", pid + ".component");
        replace(componentFile, "${dataset}", symbolicDir+dataFile.getName());
                
        if (componentAttr.length() != 0) {
            componentAttr += ", ";
        }
        componentAttr += symbolicDir+componentFile.getName();
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
        
        Properties properties = baseProperties;
        
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
                "org.cishell.templates.dataset, " +
                "org.osgi.service.component;version=\"1.0.0\", " +
                "org.osgi.service.metatype;version=\"1.1.0\", " +
                "org.osgi.service.log");
    }
}
