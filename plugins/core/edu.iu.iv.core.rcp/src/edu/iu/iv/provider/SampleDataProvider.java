/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 11, 2005 at Indiana University.
 */
package edu.iu.iv.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.ui.IStartup;

import edu.iu.iv.core.IVC;

/**
 * An abstract class for providing sample data for 
 * sampledata plugins.
 * 
 * @author Bruce Herr
 */
public abstract class SampleDataProvider implements IStartup {
    private String pluginId;
    private String subDirectory;

    public SampleDataProvider(String pluginId, String subDirectory) {
        this.pluginId = pluginId;
        this.subDirectory = subDirectory;
    }

    /**
     * @see org.eclipse.ui.IStartup#earlyStartup()
     */
    public void earlyStartup() {
        String path = IVC.getInstance().getPluginPath(pluginId);
        File myData = new File(path + "sampledata");
        File[] files = myData.listFiles();               
        String dest = System.getProperty("user.dir") + 
            File.separator + "sampledata" + File.separator + subDirectory +
            File.separator;                
        for(int i = 0; i < files.length; i++){
            String newName = dest + files[i].getName();
            File newFile = new File(newName);
            if(!newFile.getParentFile().exists())
                newFile.getParentFile().mkdirs();
            //files[i].renameTo(newFile);
            copy(files[i], newFile);
        }
    }
    
    private void copy(File file, File copy) {
        if (!copy.exists()) {
            try {
                FileInputStream in = new FileInputStream(file);
                FileOutputStream out = new FileOutputStream(copy);
                
                while (in.available() > 0) {
                    byte[] data = new byte[in.available()];
                    
                    in.read(data);
                    out.write(data);
                }
            } catch (FileNotFoundException e) {
                log("" + file + "->" + copy + " = " + e);
            } catch (IOException e) {
                log("" + file + "->" + copy + " = " + e);
            }
        }
    }
    
    private void log(String msg) {
        IVC.getInstance().getErrorLogger().error(msg);
    }
}
