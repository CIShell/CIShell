package org.cishell.reference.gui.log;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.framework.ServiceReference;

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
		
		LogListener listener = new LogToFile();
        ServiceReference ref = context.getServiceReference(LogReaderService.class.getName());
        LogReaderService reader = (LogReaderService) context.getService(ref);
        if (reader != null) {
            reader.addLogListener(listener);   
        }
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	public static BundleContext getContext() {
		return context;
	}
}
