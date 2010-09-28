package org.cishell.reference.gui.menumanager;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.reference.gui.menumanager.menu.MenuAdapter;
import org.cishell.reference.gui.workspace.CIShellApplication;
import org.cishell.utilities.StringUtilities;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class Activator extends AbstractUIPlugin implements IStartup {
	public static final String PLUGIN_ID = "org.cishell.reference.gui.menumanager";

	public static final String CONFIGURATION_DIRECTORY = "configuration";
	public static final String WELCOME_TEXT_FILE_NAME = "Welcome.properties";

	public static final String DEFAULT_TOOL_NAME = "CIShell";
	public static final String TOOL_NAME_PROPERTY = "toolName";
	public static final String DEFAULT_TOOL_TICKET_URL =
		"http://cns-jira.slis.indiana.edu/secure/CreateIssue.jspa?issuetype=1";
	public static final String TOOL_TICKET_URL_PROPERTY = "toolTicketURL";

	// The shared instance.
	private static Activator plugin;
	private static BundleContext bundleContext;

    @SuppressWarnings("unused")
    private MenuAdapter menuAdapter;

	public Activator() {
		Activator.plugin = this;
	}

	public void start(BundleContext bundleContext) throws Exception {
		super.start(bundleContext);
        
		Activator.bundleContext = bundleContext;
		
        while (getWorkbench() == null) {
            Thread.sleep(500);
        }
        
        IWorkbenchWindow[] windows = getWorkbench().getWorkbenchWindows();
        
        while (windows.length == 0) {
            Thread.sleep(500);
            windows = getWorkbench().getWorkbenchWindows();
        }
        
        final Shell shell = windows[0].getShell();
        IMenuManager menuManager = CIShellApplication.getMenuManager();
        CIShellContext ciShellContext = new LocalCIShellContext(bundleContext);
        Properties properties = getProperties();
        String toolName = getToolName(properties);
        String toolTicketURL = getToolTicketURL(properties);
        
        this.menuAdapter = new MenuAdapter(
        	toolName,
        	toolTicketURL,
        	menuManager,
        	shell,
        	bundleContext,
        	ciShellContext,
        	windows[0]);
        
        try {
        	// Fix to make swing based algorithms work on Macs.
	    	shell.getDisplay().syncExec(new Runnable(){
				public void run() {
					// This will simply initialize the SWT_AWT compatibility mode.
					SWT_AWT.new_Frame(new Shell(SWT.EMBEDDED));
				}});
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	public void stop(BundleContext context) throws Exception {
		Activator.plugin = null;
        this.menuAdapter = null;
        
		super.stop(context);
	}
	
	public static Object getService(String servicePID) {
		ServiceReference serviceReference =
			Activator.bundleContext.getServiceReference(servicePID);

		if (serviceReference != null) {
			return Activator.bundleContext.getService(serviceReference);
		} else {
			return null;
		}
	}

	public static Activator getDefault() {
		return Activator.plugin;
	}

    public void earlyStartup() {
    }

    private static Properties getProperties() {
    	Properties brandBundleProperties = new Properties();

    	try {
    		URL welcomeTextFileURL = new URL(new URL(
         		System.getProperty("osgi.configuration.area")), WELCOME_TEXT_FILE_NAME);
    		brandBundleProperties.load(welcomeTextFileURL.openStream());
    	} catch (IOException e) {
    		throw new RuntimeException(e.getMessage(), e);
    	}

    	return brandBundleProperties;
    }

    private static String getToolName(Properties properties) {
    	String toolName = properties.getProperty(TOOL_NAME_PROPERTY);

    	if (!StringUtilities.isNull_Empty_OrWhitespace(toolName)) {
    		return toolName;
    	} else {
    		return DEFAULT_TOOL_NAME;
    	}
    }

    private static String getToolTicketURL(Properties properties) {
    	String toolTicketURL = properties.getProperty(TOOL_TICKET_URL_PROPERTY);

    	if (!StringUtilities.isNull_Empty_OrWhitespace(toolTicketURL)) {
    		return toolTicketURL;
    	} else {
    		return DEFAULT_TOOL_TICKET_URL;
    	}
    }
}
