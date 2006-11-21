package org.cishell.reference.gui.scheduler;

import java.io.File;

import org.cishell.app.service.scheduler.SchedulerService;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.cishell.reference.gui.scheduler";
	private static Activator plugin;
	private static BundleContext context;
	
	public Activator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		Activator.context = context; 
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
	
	protected static SchedulerService getSchedulerService() {
		ServiceReference serviceReference = context.getServiceReference(SchedulerService.class.getName());
		SchedulerService manager = null;
		
		if (serviceReference != null) {
			manager = (SchedulerService) context.getService(serviceReference);
		}
		
		return manager;
	}
	
    public static Image createImage(String name){
        if(Platform.isRunning()){
            return AbstractUIPlugin.
            	imageDescriptorFromPlugin(PLUGIN_ID, 
            	        File.separator + "icons" + File.separator + name).
            	        createImage();
        }
        else {
            return null;
        }            
    }
}
