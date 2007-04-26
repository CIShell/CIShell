package org.cishell.reference.gui.workspace;


import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

	// Actions - important to allocate these only in makeActions, and then use
	// them in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.
	private IWorkbenchAction exitAction;
    private IWorkbenchAction aboutAction;   

	public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
		super(configurer);
	}

	protected void makeActions(final IWorkbenchWindow window) {
        CIShellApplication.setWorkbench(window);
		// Creates the actions and registers them.
		// Registering is needed to ensure that key bindings work.
		// The corresponding commands keybindings are defined in the plugin.xml
		// file. Registering also provides automatic disposal of the actions 
		// when the window is closed.
		exitAction = ActionFactory.QUIT.create(window);
		register(exitAction);
        
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);        
	}

	protected void fillMenuBar(IMenuManager menuBar) {
        CIShellApplication.setMenuManager(menuBar);

	}
    
}
