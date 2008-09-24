package org.cishell.templates.wizards;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.cishell.templates.wizards";

	// The shared instance
	private static Activator plugin;
    
    //Resource bundle.
    private ResourceBundle resourceBundle;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
        try {
            resourceBundle = ResourceBundle.getBundle(Activator.class.getName());
        } catch (MissingResourceException x) {
            resourceBundle = null;
        }
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
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
