package edu.iu.iv.gui;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.WorkbenchAdvisor;

import edu.iu.iv.common.guibuilder.GUIBuilder;
import edu.iu.iv.core.EclipseIVCDelegate;
import edu.iu.iv.core.IVC;
import edu.iu.iv.gui.builder.SwtGUIBuilder;

public class IVCApplication implements IPlatformRunnable {
	private static Shell shell; //associated SWT shell w/ this app
	private static IMenuManager menu;

    public Object run(Object args) {        
        WorkbenchAdvisor workbenchAdvisor = new IVCWorkbenchAdvisor();
        Display display = PlatformUI.createDisplay();
        
        SwtGUIBuilder.setParent(getShell());
        GUIBuilder.setGUIBuilder(SwtGUIBuilder.getGUIBuilder());
        IVC.setDelegate(new EclipseIVCDelegate());
        IVC.getDelegate().setDefaultSettings();
        
        try {
            int returnCode = PlatformUI.createAndRunWorkbench(display,
                    workbenchAdvisor); 
            
            if (returnCode == PlatformUI.RETURN_RESTART) {
                return IPlatformRunnable.EXIT_RESTART;
            } else {
                return IPlatformRunnable.EXIT_OK;
            }
        } finally {
            display.dispose();
        }   
    }
    
	
	/**
	 * Sets the Shell that is used by this IVC instance.  This is the main window of the
	 * application, which is used to launch dialog boxes for example.
	 * 
	 * @param shell the Shell that is used by this IVC instance
	 */
	public static void setShell(Shell shell){
	    IVCApplication.shell = shell;
	}
	
	/**
	 * Returns the shell that is used by this IVC instance, this is the main
	 * window of the application
	 * 
	 * @return the shell that is used by this IVC instance
	 */
	public static Shell getShell(){
	    return shell;
	}
	
	/**
	 * Sets the IMenuManager used by this IVC instance.  This menu manager is
	 * the manager of the main menu of the application and can be used to
	 * access menu items, add items, or change existing items.
	 * 
	 * @param menu the IMenuManager that is used by this IVC instance.
	 */
	public static void setMenuManager(IMenuManager menu){
	    IVCApplication.menu = menu;
	}

	/**
	 * Gets the IMenuManager used by this IVC instance.  This menu manager is
	 * the manager of the main menu of the application and can be used to
	 * access menu items, add items, or change existing items.
	 * 
	 * @return the IMenuManager that is used by this IVC instance.
	 */
	public static IMenuManager getMenuManager(){
	    return menu;
	}
}