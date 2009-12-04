package org.cishell.reference.gui.workspace;


import java.io.BufferedWriter;
import java.io.FileWriter;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
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
public class ApplicationActionBarAdvisor extends ActionBarAdvisor{

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
        /* 
         * If we don't create the File and the Help menus at initialization, they don't show up correctly. I create the file and the help
         * menus and add them. Later the default_menu.xml file is parse and the additional menus are added in between
         * the File and the Help menus.
         * 
         * The File menu is created normally, as we modify it again in the MenuAdapter code. The Help menu is created as having
         * three divisions, start, additions, and end. The File menu later adds these divisions so that additional packages will be added
         * between the Test submenu and the Exit item. The Help menu adds additional packages before the configuration and update items.
         * 
         * Modified by: Tim Kelley
         * Date: May, 8-9, 2007
         * Additional Code found in: org.cishell.reference.gui.menumanager.MenuAdapter.java
         */
        
       
        
        MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE); 	//Create File menu as a normal Menu
        menuBar.add(fileMenu);																//add it to the MenuBar
        MenuManager helpMenu = createMenu("&Help", IWorkbenchActionConstants.M_HELP);		//Create the Help Menu with start, end, and additions as divisions
        helpMenu.add(new Separator());														//Add a separator and then add the "About" MenuItem.
        helpMenu.appendToGroup("end",aboutAction);    
        menuBar.add(new GroupMarker("start"));												//Add divisions to the MenuBar so we can add
        																					//additional menus to the MenuBar between File and Help menus																					//the File and Help menus.
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(new GroupMarker("end"));
        
		menuBar.add(helpMenu);																//Add the Help menu after the divisions.
	}
	
	private MenuManager createMenu(String text, String id) {
        MenuManager menu = new MenuManager(text, id);
        menu.add(new GroupMarker("start"));
        menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menu.add(new GroupMarker("end"));

        return menu;
    }

//	protected void register(IAction action) {
//		try {
//			FileWriter fstream = new FileWriter("C:/Documents and Settings/pataphil/Desktop/out.txt", true);
//			BufferedWriter out = new BufferedWriter(fstream);
//			out.write("action: " + action + "\r\n");
//			out.close();
//		} catch (Exception e) {
//			System.err.println("Error: " + e.getMessage());
//		}
//	}
}
