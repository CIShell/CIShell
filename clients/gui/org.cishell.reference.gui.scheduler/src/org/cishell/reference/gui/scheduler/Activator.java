package org.cishell.reference.gui.scheduler;

import java.io.File;

import org.cishell.app.service.scheduler.SchedulerService;
import org.cishell.reference.gui.workspace.CIShellApplication;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {
	public static final String PLUGIN_ID = "org.cishell.reference.gui.scheduler";
	private static Activator plugin;
	private static BundleContext context;
	private boolean waitForBundleContext;
	
	private static final int ATTEMPTS_TO_FIND_TOOLBAR = 15;
	private static final int SLEEP_TIME = 100;
	
	public Activator() {
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		Activator.context = context;
		if (waitForBundleContext) {
			earlyStartup();
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

	public void earlyStartup() {
		if (context != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					/* 
					 * 
					 */
					Action scheduler = new SchedulerAction();
					IMenuManager manager = CIShellApplication.getMenuManager();
					
					
					IMenuManager newManager = null;
					for (int i = 0; i < ATTEMPTS_TO_FIND_TOOLBAR && newManager == null; i++) {
						try {
						Thread.sleep(SLEEP_TIME);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						newManager = manager.findMenuUsingPath("tools");
					}
					
					manager = manager.findMenuUsingPath("tools");
					
					if (manager == null) {
						System.err.println( "Unable to add Scheduler to Tools menu, since Tools menu does not exist.");
					} else {
						manager.appendToGroup("start", scheduler);
					}
					SchedulerView view = SchedulerView.getDefault();
					boolean visible = view != null
							&& PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getActivePage()
									.isPartVisible(view);
					scheduler.setChecked(visible);
					IMenuManager otherManagerReference = CIShellApplication.getMenuManager();
					if(otherManagerReference == null) {
						System.err.println("The menu manager is still null. Surprise.");
					} else {
						otherManagerReference.update(true);
					}
				}
			});
			waitForBundleContext = false;
		}
		else {
			waitForBundleContext = true;
		}
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
