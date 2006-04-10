/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 10, 2005 at Indiana University.
 */
package edu.iu.iv.templates;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * Plugin class that controls the lifecycle of the IVC Templates Plugin.
 *
 * @author Team IVC (James Ellis)
 */
public class TemplatesPlugin extends AbstractUIPlugin {

    //plugin ID
    public static final String ID_PLUGIN = "edu.iu.iv.templates";
    
	//The shared instance.
	private static TemplatesPlugin plugin;
	
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * Creates a new TemplatesPlugin
	 */
	public TemplatesPlugin() {
		super();
		if(plugin == null)
		    plugin = this;	
		try {
			resourceBundle = ResourceBundle.getBundle("edu.iu.iv.templates.TemplatesPlugin");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation.
	 * 
	 * @param context The BundleContext for this Plugin
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);		
	}

	/**
	 * This method is called when the plug-in is stopped.
	 * 
	 * @param context The BundleContext for this Plugin
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance of TemplatesPlugin (Singleton)
	 * 
	 * @return the shared instance of TemplatesPlugin (Singleton)
	 */
	public static TemplatesPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * 
	 * @param key The key of the item to retrieve from the ResourceBundle
	 * @return the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = TemplatesPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle.
	 * 
	 * @return the Plugin's ResourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	

}
