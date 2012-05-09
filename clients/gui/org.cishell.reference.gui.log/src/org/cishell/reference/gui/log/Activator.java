package org.cishell.reference.gui.log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.cishell.app.service.datamanager.DataManagerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;
import org.osgi.service.log.LogReaderService;

/**
 * The activator class controls the plug-in life cycle. Adapted from
 * {@code http://blog.kornr.net/index.php/2008/12/09/understanding-the-osgi-logging-service}
 */
public class Activator extends AbstractUIPlugin {
	public static final String PREFERENCE_SPEPARATOR = "|";
	public static final String PLUGIN_ID = "org.cishell.reference.gui.log";
	private static Activator plugin;
	private static BundleContext context;
	public static DataManagerService dataManager;
	
	protected LogToFile fileLogger;
	protected LogToConsole consoleLogger;
	
	protected List<LogReaderService> logReaders = new ArrayList<LogReaderService>();
	
	public Activator() {
		plugin = this;
	}

	private ServiceListener serviceListener = new ServiceListener() {
		@Override
		public void serviceChanged(ServiceEvent event) {
			BundleContext bundleContext = event.getServiceReference()
					.getBundle().getBundleContext();
			LogReaderService logReaderService = (LogReaderService) bundleContext
					.getService(event.getServiceReference());

			if (logReaderService != null) {
				if (event.getType() == ServiceEvent.REGISTERED) {
					Activator.this.logReaders.add(logReaderService);
					logReaderService.addLogListener(Activator.this.fileLogger);
				} else if (event.getType() == ServiceEvent.UNREGISTERING) {
					logReaderService.removeLogListener(Activator.this.fileLogger);
					Activator.this.logReaders.remove(logReaderService);
				}
			}
		}
	};
	
	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
		Activator.context = bundleContext;
		
		/**
		 * Get the file and console logger preferences if they exist otherwise
		 * use a default.
		 * 
		 * Take a look at
		 * http://www.vogella.com/articles/EclipsePreferences/article.html#preferences_plugin.ini
		 **/
		PreferencesService preferenceService = bundleContext
				.getService(bundleContext
						.getServiceReference(PreferencesService.class));
		if (preferenceService != null) {
			Preferences systemPreferences = preferenceService
					.getSystemPreferences();
			String logDirectory = systemPreferences
					.get("logDirectory", null);
			String minOSGILevel = systemPreferences
					.get("minOSGILevel", null);
			String ignoredPrefixes = systemPreferences
					.get("ignoredPrefixes", null);
			String detailedMessages = systemPreferences.get("detailedMessages", null);
			
			if (logDirectory != null && minOSGILevel != null) {				
				this.fileLogger = new LogToFile(logDirectory, Utilities.osgiLevelToJavaLevel(Integer.parseInt(minOSGILevel)));
			} else {
				this.fileLogger = null;
			}
			
			if (detailedMessages != null && minOSGILevel != null && ignoredPrefixes != null) {
				this.consoleLogger = new LogToConsole(Boolean.parseBoolean(detailedMessages), Integer.parseInt(minOSGILevel),
						ignoredPrefixes.split(PREFERENCE_SPEPARATOR));
			} else {
				this.consoleLogger = null;
			}
		}
		if (this.fileLogger == null) {
			this.fileLogger = new LogToFile();
		}
		if (this.consoleLogger == null) {
			this.consoleLogger = new LogToConsole(false);
		}

		String serviceFilter = null;
		Collection<ServiceReference<LogReaderService>> serviceReferences = bundleContext
				.getServiceReferences(LogReaderService.class, serviceFilter);

		
		if (serviceReferences != null) {
			for (ServiceReference<LogReaderService> serviceReference : serviceReferences) {
				LogReaderService reader = bundleContext
						.getService(serviceReference);
				this.logReaders.add(reader);
				reader.addLogListener(this.fileLogger);
				reader.addLogListener(this.consoleLogger);
			}
			
			
			// Add the ServiceListener, but with a filter so that we only
			// receive events related to LogReaderService
			String filter = "(objectclass=" + LogReaderService.class.getName()
					+ ")";
			try {
				context.addServiceListener(this.serviceListener, filter);
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
			}
			
		}
		
		/** Add the data manager **/

        Activator.dataManager = (DataManagerService)
            bundleContext.getService(bundleContext.getServiceReference(
                DataManagerService.class.getName()));
	}

	public void stop(BundleContext bundleContext) throws Exception {
		plugin = null;
		super.stop(bundleContext);
		
		Activator.context = null;
		if (this.fileLogger != null) {
			for (LogReaderService reader : this.logReaders) {
				reader.removeLogListener(this.fileLogger);
				reader.removeLogListener(this.consoleLogger);
				this.logReaders.remove(reader);
			}
		}
	}

	public static Activator getDefault() {
		return plugin;
	}
	
	public static BundleContext getContext() {
		return context;
	}
}
