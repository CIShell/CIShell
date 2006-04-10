/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 10, 2005 at Indiana University.
 */
package edu.iu.iv;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class SwtGUIBuilderImageLoader {

    public SwtGUIBuilderImageLoader() {
        
    }
    
    public static Image createImage(String name) {
        if(Platform.isRunning()){
            return AbstractUIPlugin.
                imageDescriptorFromPlugin(SwtGUIBuilderPlugin.ID_PLUGIN, 
                        File.separator + "icons" + File.separator + name).
                        createImage();
        }
        else {
            return null;
        }   
    }

}
