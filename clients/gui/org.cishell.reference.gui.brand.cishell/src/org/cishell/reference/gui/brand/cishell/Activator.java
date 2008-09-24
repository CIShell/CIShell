package org.cishell.reference.gui.brand.cishell;

import java.io.IOException;
import java.util.Properties;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class Activator extends AbstractUIPlugin implements IStartup {
    private BundleContext bContext;
    private boolean alreadyLogged;

	// The plug-in ID
	public static final String PLUGIN_ID = "org.cishell.reference.gui.brand.cishell";
	private static Activator plugin;
	
	public Activator() {
		plugin = this;
        alreadyLogged = false;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
        this.bContext = context;
        
        if (!alreadyLogged) {
            earlyStartup();
        }
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}

    public void earlyStartup() {
        if (bContext != null) {
            String blurb = null;
            Properties props = new Properties();

            try {
                props.load(bContext.getBundle().getEntry("/plugin.properties").openStream());                
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            blurb = props.getProperty("blurb", null);
            
            ServiceReference ref = bContext.getServiceReference(LogService.class.getName());
                
            if (ref != null && blurb != null) {
                alreadyLogged = true;
                
                LogService logger = (LogService)bContext.getService(ref);
                logger.log(LogService.LOG_INFO, blurb);
            }
        }
    }
}
