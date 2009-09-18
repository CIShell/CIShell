package org.cishell.reference.gui.log;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.cishell.reference.gui.log";
	private static Activator plugin;
	private static BundleContext context;
	
	public Activator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		Activator.context = context; 
		
		LogListener fileLogListener = new LogToFile();
        ServiceReference serviceReference =
        	context.getServiceReference(LogReaderService.class.getName());
        LogReaderService logReaderService =
        	(LogReaderService) context.getService(serviceReference);
        
        if (logReaderService != null) {
            logReaderService.addLogListener(fileLogListener);
        }
	}

	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		super.stop(bundleContext);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	public static BundleContext getContext() {
		return context;
	}
}
