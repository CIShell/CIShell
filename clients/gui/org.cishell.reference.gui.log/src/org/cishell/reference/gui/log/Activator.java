package org.cishell.reference.gui.log;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.cishell.reference.gui.log";
	private static Activator plugin;
	
	public Activator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
        
        LogListener listener = new LogListener() {
            public void logged(LogEntry e) {
                if (goodMessage(e.getMessage())) {
                    System.out.println(e.getMessage());
                }
            }
            
            public boolean goodMessage(String msg) {
                if (msg == null || 
                        msg.startsWith("ServiceEvent ") || 
                        msg.startsWith("BundleEvent ") || 
                        msg.startsWith("FrameworkEvent ")) {
                    return false;
                } else {
                    return true;   
                }
            }
        };
        
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
}
