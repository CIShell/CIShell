/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 20, 2005 at Indiana University.
 */
package edu.iu.iv.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;

import edu.iu.iv.internal.AbstractIVCDelegate;

/**
 * 
 * @author Bruce Herr
 */
public class EclipseIVCDelegate extends AbstractIVCDelegate {
	private File tempDirName;
	
    public EclipseIVCDelegate() {
        super(new EclipseIVCConfiguration());
    }
    
    public void setDefaultSettings() {
    	super.setDefaultSettings();
    	setupTempDir();
    }
    
    private void setupTempDir() {
        File tempDir = new File(getConfiguration().getString(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE));
        try {
			tempDirName = File.createTempFile("IVC-Session-", "", tempDir);
			tempDirName.delete();
			tempDirName.mkdir();
		} catch (IOException e) {
			tempDirName = tempDir;
			e.printStackTrace();
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread () {
			public void run() {
				remove(tempDirName);
			}
			private void remove(File file) {
				if (file.isDirectory()) {
					File[] files = file.listFiles();

					for (int i = 0; i < files.length; i++) {
						remove(files[i]);
					}
				}
				
				file.delete();
			}
		});
    }


    /**
	 * @see edu.iu.iv.internal.AbstractIVCDelegate#getPluginPath(java.lang.String)
	 */
    public String getPluginPath(String pluginID) {
        try {
            URL url = Platform.find(Platform.getBundle(pluginID), new Path(""));
            url = Platform.resolve(url);
            
            int cutoff = Platform.getOS() == Platform.OS_WIN32 ? 6 : 5;
            return url.toExternalForm().replace('/', File.separatorChar).substring(cutoff);
             
         } catch (MalformedURLException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
         
         //failure somewhere...        
         return null;     
    }

    /**
     * @see edu.iu.iv.internal.AbstractIVCDelegate#getPluginPath(org.eclipse.core.runtime.Plugin)
     */
    public String getPluginPath(Object object) {
        if (!(object instanceof org.eclipse.core.runtime.Plugin)) {
            return getPluginPath(object.toString());
        } else {
            org.eclipse.core.runtime.Plugin plugin = (Plugin) object;
	        try {
	            URL url = Platform.find(plugin.getBundle(), new Path(""));
	            url = Platform.resolve(url);
                
                int cutoff = Platform.getOS() == Platform.OS_WIN32 ? 6 : 5;
	            return url.toExternalForm().replace('/', File.separatorChar).substring(cutoff);
	             
	         } catch (MalformedURLException e) {
	             e.printStackTrace();
	         } catch (IOException e) {
	             e.printStackTrace();
	         }
	         
	         //failure somewhere...        
	         return null;
        }
    }

    /**
     * @see edu.iu.iv.internal.AbstractIVCDelegate#getDefaultDataFolder()
     */
    public String getDefaultDataFolder() {
        return getConfiguration().getString(IVCPreferences.DEFAULT_DATA_FOLDER_PREFERERNCE);
    }

    /**
     * @see edu.iu.iv.internal.AbstractIVCDelegate#getTemporaryFilesFolder()
     */
    public String getTemporaryFilesFolder() {
        //return getConfiguration().getString(IVCPreferences.TEMPORARY_FILES_FOLDER_PREFERENCE);
    	return tempDirName.toString();
    }
}