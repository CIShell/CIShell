package org.cishell.reference.gui.datamanager;

import org.cishell.app.service.datamanager.DataManagerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.cishell.reference.gui.datamanager";
	private static BundleContext context;

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		Activator.context = context;		
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
	
	protected static DataManagerService getDataManagerService() {
		DataManagerService manager = (DataManagerService) context.getService(
				context.getServiceReference(DataManagerService.class.getName()));
		
		return manager;
	}
	
	protected static LogService getLogService() {
		LogService log = (LogService) context.getService(
				context.getServiceReference(LogService.class.getName()));
		
		return log;
	}
}
