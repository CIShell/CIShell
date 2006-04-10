/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 23, 2005 at Indiana University.
 */

package edu.iu.iv.schedulerview;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.iu.iv.core.IVC;
import edu.iu.iv.gui.IVCApplication;


/**
 *
 * @author Team IVC
 */
public class SchedulerViewPlugin extends AbstractUIPlugin implements IStartup {

    public static final String ID_PLUGIN = "edu.iu.iv.schedulerview";
    
	//The shared instance.
	private static SchedulerViewPlugin plugin;
	
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * Creates a new PersistencePlugin
	 */
	public SchedulerViewPlugin() {
		super();
		if(plugin == null)
		    plugin = this;	
		try {
			resourceBundle = ResourceBundle.getBundle("edu.iu.iv.schedulerview.SchedulerViewPlugin");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation.  It handles the job
	 * of requesting the scanning of the Persister directory and 
	 * registering all found Persisters with IVC
	 * 
	 * @param context The BundleContext for this Plugin
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);	    
		IVC.getInstance().getScheduler().addSchedulerListener(SchedulerContentModel.getInstance());
	}

	/**
	 * This method is called when the plug-in is stopped. Currently it
	 * simply clears out the registry of Persisters if the PersistencePlugin
	 * is stopped.
	 * 
	 * @param context The BundleContext for this Plugin
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance of PersistencePlugin (Singleton)
	 * 
	 * @return the shared instance of PersistencePlugin (Singleton)
	 */
	public static SchedulerViewPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * 
	 * @param key The key of the item to retrieve from the ResourceBundle
	 * @return the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = SchedulerViewPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle.
	 * 
	 * @return the Plugin's ResourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
    public void earlyStartup() {
		Display.getDefault().asyncExec(new Runnable(){
		    public void run(){
			    Action scheduler = new SchedulerAction();
			    IMenuManager manager = IVCApplication.getMenuManager();
			    manager = manager.findMenuUsingPath("tools");
			    manager.appendToGroup("start", scheduler);
			    SchedulerView view = SchedulerView.getDefault();
			    boolean visible = view != null && PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isPartVisible(view);			    
			    scheduler.setChecked(visible);
			    IVCApplication.getMenuManager().update(true);
		    }
		});   
    }
    
    private class SchedulerAction extends Action {
        public SchedulerAction(){
            super("Scheduler", IAction.AS_CHECK_BOX);
            setId("scheduler");
        }
        
        public void run(){
            if(this.isChecked()){
	            try {
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SchedulerView.ID_VIEW);
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
            else{
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(SchedulerView.getDefault());
            }
        }	    
    }
}
