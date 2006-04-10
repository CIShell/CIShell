/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu)
 */
package $packageName$;

import java.net.MalformedURLException;
import java.net.URL;

import edu.iu.iv.core.plugin.AbstractPlugin;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.plugin.PluginProperty;

/**
 * Plugin class for this addition to IVC.
 *
 * @author
 */
public class $pluginClassName$ extends AbstractPlugin {
    //id of this Plugin
    public static final String ID_PLUGIN = "$pluginId$";
    
    //basic Plugin information
    private static final String AUTHOR = "!FIXME!";
    private static final String DESCRIPTION = "!FIXME!";
    private static final String CITATION_STRING = "!FIXME!";   
    private static final String UNSUPPORTED_REASON = "!FIXME!";
    private static final String DOCUMENTATION_URL = "!FIXME!";
    
    /**
     * Creates a new $pluginClassName$.
     */
	public $pluginClassName$() {
	    //add the proper information to this Plugin's PropertyMap
        propertyMap.put(PluginProperty.AUTHOR, AUTHOR);
        propertyMap.put(PluginProperty.CITATION_STRING, CITATION_STRING);
        
        //remove if not providing a Documentation URL
		try {
			propertyMap.put(PluginProperty.DOCUMENTATION_LINK, new URL(DOCUMENTATION_URL)) ;
		} catch (MalformedURLException e) {}	   
	}

	/**
	 * Returns the description of this Plugin.
	 * 
	 * @return the description of this Plugin.
	 */
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Launches this Plugin.  This method is called if, and only if, the given
     * model has first passed the 'supports' test defined below.  This method should
     * then perform any desired tasks such as loading a GUI and executing an Algorithm.
     * 
     * @param model the data model to be used to launch this Plugin, if needed.
     */
    public void launch(DataModel model) {
        //this template simple creates a $algorithmClassName$ and executes it,
        //replace as needed.
        $algorithmClassName$ algorithm = new $algorithmClassName$();
        algorithm.execute();        
    }

    /**
     * Determines if this Plugin supports the given data model. This method determines
     * whether or not this Plugin's menu item will be enabled when the given model
     * is selected in IVC.
     * 
     * @param model the data model to check if this Plugin supports
     * @return true if the model is supported, false otherwise.
     */
    public boolean supports(DataModel model) {
        //this template simply returns true, replace as needed.
        return true;
    }

    /**
     * Returns the reason that the given model is not supported by this Plugin.
     * 
     * @param model the model to determine why it is unsupported
     * @return the reason that the given model is not supported by this Plugin.
     */
    public String unsupportedReason(DataModel model) {
        return UNSUPPORTED_REASON;
    }
}