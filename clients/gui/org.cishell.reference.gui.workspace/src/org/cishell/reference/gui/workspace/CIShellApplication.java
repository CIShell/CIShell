package org.cishell.reference.gui.workspace;

import org.eclipse.core.runtime.IPlatformRunnable;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class CIShellApplication implements IPlatformRunnable {
    private static IMenuManager menuManager;

	/**
	 * @see org.eclipse.core.runtime.IPlatformRunnable#run(java.lang.Object)
	 */
	public Object run(Object args) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IPlatformRunnable.EXIT_RESTART;
			}
			return IPlatformRunnable.EXIT_OK;
		} finally {
			display.dispose();
		}
	}
    
    public static void setMenuManager(IMenuManager menuManager) {
        CIShellApplication.menuManager = menuManager; 
    }
    
    public static IMenuManager getMenuManager() {
        return menuManager;
    }
}
