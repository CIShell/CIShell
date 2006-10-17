package org.cishell.reference.gui.datamanager;

import org.cishell.app.service.datamanager.DataManagerService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
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
		ServiceReference serviceReference = context.getServiceReference(DataManagerService.class.getName());
		DataManagerService manager = null;
		
		if (serviceReference != null) {
			manager = (DataManagerService) context.getService(serviceReference);
			
		}
		
		return manager;
	}
	
	protected static LogService getLogService() {
		ServiceReference serviceReference = context.getServiceReference(DataManagerService.class.getName());
		LogService log = null;
		
		if (serviceReference != null) {
			log = (LogService) context.getService(
				context.getServiceReference(LogService.class.getName()));
		}
		
		return log;
	}
	
	protected static AlgorithmFactory getService(String service) {
		ServiceReference[] refs;
		try {
			refs = context.getServiceReferences(AlgorithmFactory.class.getName(),
					"(&("+Constants.SERVICE_PID+"="+service+"))");
					//"(&("+Constants.SERVICE_PID+"=org.cishell.reference.gui.persistence.save.Save))");
			if (refs != null && refs.length > 0) {
				return (AlgorithmFactory) context.getService(refs[0]);
			} else {
				return null;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected static BundleContext getBundleContext() {
		return context;
	}

	protected static CIShellContext getCIShellContext() {
		return new LocalCIShellContext(context);
	}
}
