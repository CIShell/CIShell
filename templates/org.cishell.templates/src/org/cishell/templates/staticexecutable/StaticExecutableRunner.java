/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 4, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.staticexecutable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.cishell.templates.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class StaticExecutableRunner implements Algorithm {
    protected final String tempDir;
    protected final GUIBuilderService guiBuilder;
    protected final Data[] data;
    protected Dictionary parameters;
    protected Properties props;
    protected CIShellContext ciContext;
    

    public StaticExecutableRunner(BundleContext bContext, CIShellContext ciContext, Properties props, Dictionary parameters, Data[] data) throws IOException {
        this.ciContext = ciContext;
        this.props = props;
        this.parameters = parameters;
        this.data = data;
        
        guiBuilder = (GUIBuilderService)ciContext.getService(GUIBuilderService.class.getName());

        
        if (data == null) data = new Data[0];
        if (parameters == null) parameters = new Hashtable();
        
        tempDir = makeTempDirectory();
    }
    
    /**
     * @see org.cishell.framework.algorithm.Algorithm#execute()
     */
    public Data[] execute() {
        try {
            String algDir = tempDir + File.separator + props.getProperty("Algorithm-Directory") + File.separator;
            
            chmod(algDir);
            File[] output = execute(getTemplate(algDir), algDir);
            
            return toData(output);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    protected Data[] toData(File[] files) {
        String[] formats = ((String)props.get(AlgorithmProperty.OUT_DATA)).split(",");
        
        Map nameToFileMap = new HashMap();
        for (int i=0; i < files.length; i++) {
            nameToFileMap.put(files[i].getName(), files[i]);
        }
        
        Data[] data = null;
        if (formats.length > files.length) {
            data = new Data[formats.length];
        } else {
            data = new Data[files.length];
        }
        
        for (int i=0; i < data.length; i++) {
            String file = props.getProperty("outFile["+i+"]", null);
            
            if (i < formats.length) {
                File f = (File) nameToFileMap.remove(file);
                
                if (f != null) {
                    data[i] = new BasicData(f,formats[i]);
                    
                    String label = props.getProperty("outFile["+i+"].label", f.getName());
                    data[i].getMetaData().put(DataProperty.LABEL, label);
                    
                    String type = props.getProperty("outFile["+i+"].type", DataProperty.OTHER_TYPE);
                    type = type.trim();
                    if (type.equalsIgnoreCase(DataProperty.MATRIX_TYPE)) {
                        type = DataProperty.MATRIX_TYPE;
                    } else if (type.equalsIgnoreCase(DataProperty.NETWORK_TYPE)) {
                        type = DataProperty.NETWORK_TYPE;
                    } else if (type.equalsIgnoreCase(DataProperty.TREE_TYPE)) {
                        type = DataProperty.TREE_TYPE;
                    } else {
                        type = DataProperty.OTHER_TYPE;
                    }
                                   
                    data[i].getMetaData().put(DataProperty.TYPE, type);
                }
            } else {
                Iterator iter = nameToFileMap.values().iterator();
                while (iter.hasNext()) {
                    File f = (File) iter.next();
                    
                    data[i] = new BasicData(f, "file:text/plain");
                    data[i].getMetaData().put(DataProperty.LABEL, f.getName());
                    
                    i++;
                }
                break;
            }
        }
        
        return data;
    }
    
    protected void chmod(String baseDir) {
        //FIXME: Surely java has a way to do this!!!!
        if (new File("/bin/chmod").exists()) {
            try {
                String executable = baseDir + props.getProperty("executable");
                Runtime.getRuntime().exec("/bin/chmod +x " + executable).waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    protected File[] execute(String[] cmdarray, String baseDir) throws Exception {
        File dir = new File(baseDir);
        String[] beforeFiles = dir.list();
        
        Process process = Runtime.getRuntime().exec(cmdarray, null, new File(baseDir));

        logStream(LogService.LOG_INFO, process.getInputStream());
        logStream(LogService.LOG_ERROR, process.getErrorStream());
        process.waitFor();
        
        //successfully ran?
        if (process.exitValue() != 0) {
        	//display the error message using gui builder
   			guiBuilder.showError("Algorithm Could Not Finish Execution", "Sorry, the algorithm could not finish execution.", 
   					"Please check the console window for the error log messages and report the bug.\n"
   					+"Thank you.");
        }
        
        //get the outputted files
        String[] afterFiles = dir.list();
        
        Arrays.sort(beforeFiles);
        Arrays.sort(afterFiles);
        
        List outputs = new ArrayList();
        
        int beforeIndex = 0;
        int afterIndex = 0;
        
        while (beforeIndex < beforeFiles.length && afterIndex < afterFiles.length) {
            if (beforeFiles[beforeIndex].equals(afterFiles[afterIndex])) {
                beforeIndex++;
                afterIndex++;
            } else {
                outputs.add(new File(baseDir + afterFiles[afterIndex]));
                afterIndex++;
            }
        }
        
        //get any remaining new files
        while (afterIndex < afterFiles.length) {
            outputs.add(new File(baseDir + afterFiles[afterIndex]));
            afterIndex++;
        }
        
        return (File[]) outputs.toArray(new File[]{});
    }
    
    protected void logStream(int logLevel, InputStream is) {
        LogService log = (LogService) ciContext.getService(LogService.class.getName());
        
        if (log == null) return;
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line = reader.readLine();
            while (line != null) {
                log.log(logLevel, line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String[] getTemplate(String algDir) {
        String template = "" + props.getProperty("template");
        String[] cmdarray = template.split("\\s");
        
        for (int i=0; i < cmdarray.length; i++) {
            cmdarray[i] = substiteVars(cmdarray[i]);
        }
        cmdarray[0] = algDir + cmdarray[0];
        
        return cmdarray;
    }
    
    protected String substiteVars(String str) {
        str = str.replaceAll("\\$\\{executable\\}", props.getProperty("executable"));
        
        for (int i=0; i < data.length; i++) {
            String file = ((File) data[i].getData()).getAbsolutePath();
            
            if (File.separatorChar == '\\') {
            	file = file.replace(File.separatorChar, '/');
        	}
            
            str = str.replaceAll("\\$\\{inFile\\["+i+"\\]\\}", file);
            
            if (File.separatorChar == '\\') {
            	str = str.replace('/',File.separatorChar);
            }
        }
        
        for (Enumeration i=parameters.keys(); i.hasMoreElements(); ) {
            String key = (String)i.nextElement();
            Object value = parameters.get(key);
            
            if (value == null) value = "";
            
            str = str.replaceAll("\\$\\{"+key+"\\}", value.toString());
        }
        
        return str;
    }
    
    public File getTempDirectory() {
        return new File(tempDir);
    }
    
    protected String makeTempDirectory() throws IOException {
        File sessionDir = Activator.getTempDirectory();
        File dir = File.createTempFile("StaticExecutableRunner-", "", sessionDir);
        
        dir.delete();
        dir.mkdirs();
        
        return dir.getAbsolutePath();
    }
}
