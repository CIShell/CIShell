/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 22, 2005 at Indiana University.
 */
package edu.iu.iv.gui;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import edu.iu.iv.IVCGuiPlugin;

/**
 *
 * @author Team IVC
 */
public class IVCImageLoader {
    
    /**
     * Creates and returns an Image contained in the IVC core plugin,
     * inside the /icons folder.
     * 
     * @param name the name of the image to load, i.e. "table.png"
     * @return the Image that was created
     */
    public static Image createImage(String name){
        if(Platform.isRunning()){
            return AbstractUIPlugin.
            	imageDescriptorFromPlugin(IVCGuiPlugin.ID_PLUGIN, 
            	        File.separator + "icons" + File.separator + name).
            	        createImage();
        }
        else {
            return null;
        }            
    }

}
