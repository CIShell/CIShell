package org.cishell.reference.gui.menumanager;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.LocalCIShellContext;
import org.cishell.reference.gui.menumanager.menu.MenuAdapter;
import org.cishell.reference.gui.workspace.CIShellApplication;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.cishell.reference.gui.menumanager";

	// The shared instance
	private static Activator plugin;
	
    MenuAdapter menuAdapter;
    
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
        
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
        CIShellContext ciContext = new LocalCIShellContext(context);
        
        menuAdapter = new MenuAdapter(menuManager,shell,context,ciContext, windows[0]);
        
        try {
        	//Fix to make swing based algorithms work on Macs
	    	shell.getDisplay().syncExec(new Runnable(){
				public void run() {
					//This will simply initialize the SWT_AWT compatibility mode
					SWT_AWT.new_Frame(new Shell(SWT.EMBEDDED));
				}});
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	/**
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
        menuAdapter = null;
        
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

    public void earlyStartup() {
        
    }
}
