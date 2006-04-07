package edu.iu.iv.gui;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.update.ui.UpdateManagerUI;

import edu.iu.iv.IVCGuiPlugin;
import edu.iu.iv.common.configuration.Configuration;
import edu.iu.iv.core.IVC;
import edu.iu.iv.core.IVCPreferences;



public class IVCWorkbenchAdvisor extends WorkbenchAdvisor {
  
    private static ImageDescriptor findAndInstallImage;
    private static ImageDescriptor manageImage;
    static {
        findAndInstallImage = AbstractUIPlugin.imageDescriptorFromPlugin(IVCGuiPlugin.ID_PLUGIN, File.separator + "icons" + File.separator + "findandinstall.gif");
        manageImage = AbstractUIPlugin.imageDescriptorFromPlugin(IVCGuiPlugin.ID_PLUGIN, File.separator + "icons" + File.separator + "manage.gif");
    }
    
    private static final String USERGUIDE_SITE = "http://iv.slis.indiana.edu/sw/index.html";
    private static final String IVC_WEBSITE = "http://sourceforge.net/projects/ivc/";
    private static final String JAVADOCS_SITE = "http://iv.slis.indiana.edu/ivc/dev/api/";
    private static final String DEVELOPER_SITE = "http://iv.slis.indiana.edu/ivc/dev/";
    private static final String SOURCEFORGE_SITE = "http://sourceforge.net/projects/ivc";
  
    private static IVCWorkbenchAdvisor defaultAdvisor;
    private List closeActions;
    
    public IVCWorkbenchAdvisor(){
        defaultAdvisor = this;
        closeActions = new ArrayList();
    }
    
    public static IVCWorkbenchAdvisor getDefault(){
        return defaultAdvisor;
    }
    
    /**
     * Registers the given IAction to be run before
     * @param action
     */
    public void addCloseAction(IVCCloseAction action){
        closeActions.add(action);
    }      
    
    public void removeCloseAction(IVCCloseAction action){
        closeActions.remove(action);
    }

    public String getInitialWindowPerspectiveId() {
        return IVCPerspective.ID_PERSPECTIVE;
    }
    
	public void initialize(IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}
    	
    public void preWindowOpen(IWorkbenchWindowConfigurer configurer) {       
        configurer.setInitialSize(new Point(600, 600));
        configurer.setShowCoolBar(false);        
        configurer.setShowStatusLine(false);
    }
    
    public void postWindowOpen(IWorkbenchWindowConfigurer configurer){                
        IVCApplication.setShell(configurer.getWindow().getShell());        
    }
    
    public boolean preWindowShellClose(IWorkbenchWindowConfigurer configurer){
        //check if scheduler has stuff going on
        boolean success = true;
        if(!IVC.getInstance().getScheduler().isEmpty() ||
            IVC.getInstance().getScheduler().isRunning()){
            
            String message = "There are currently items either running " +
            		"or waiting to run in the Scheduler.  These scheduled " +
            		"items will be lost if IVC is exited at this point, " +
            		"Continue?";
        
            success = IVC.showQuestion("Scheduler Running", message, "");
            if(!success)
                return false;
        }

        //run any action that an extension set (ie. persistence plugin)
        Iterator iterator = closeActions.iterator();
        while(success && iterator.hasNext()){
            IVCCloseAction action = (IVCCloseAction)iterator.next();
            success = action.run();
        }
        
        //if the close actions succeeded, do the confirmation if 
        //the preference is set
        if(success)            
            return confirmExit();
        else //otherwise dont close
            return false;
    }
    
	public void fillActionBars(IWorkbenchWindow window, IActionBarConfigurer configurer, int flags) {
//	    if ((flags & ActionBarAdvisor.FILL_MENU_BAR) != 0) {
//	        fillMenuBar(window, configurer);
//	    }
        fillMenuBar(window, configurer);
	}
	//File, Preprocessing, Modeling, Analysis, Visualization, Interaction, Tools, Help
	private void fillMenuBar(IWorkbenchWindow window, IActionBarConfigurer configurer) {
	    IMenuManager menuBar = configurer.getMenuManager();
	    menuBar.add(createFileMenu(window));
	    menuBar.add(createMenu("Preprocessing", "preprocessing"));
	    menuBar.add(createMenu("Modeling", "modeling"));
	    menuBar.add(createMenu("Analysis", "analysis"));
	    menuBar.add(createMenu("Visualization", "visualization"));
	    menuBar.add(createMenu("Interaction", "interaction"));
	    menuBar.add(createMenu("Converters", "converters"));
	    menuBar.add(createToolsMenu(window));
	    
	    //any additions go here
	    menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	    
	    menuBar.add(createHelpMenu(window));
	    
	    IVCApplication.setMenuManager(menuBar);
	}
	
	private MenuManager createMenu(String name, String id){
	    MenuManager menu = new MenuManager(name, id);
	    menu.add(new GroupMarker("start"));
	    menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	    menu.add(new GroupMarker("end"));
	    return menu;
	}
	
	private MenuManager createFileMenu(IWorkbenchWindow window) {	    
	    MenuManager menu = new MenuManager("File", IWorkbenchActionConstants.M_FILE);
	    menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
	    menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));	   
	    menu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
        final IWorkbenchAction quit = ActionFactory.QUIT.create(window);
        
		IAction action = new Action(){
		    public void run(){
		        if(preWindowShellClose(null))
		            quit.run();
		    }
		};

		action.setId(quit.getId());
        action.setText(quit.getText());
        action.setAccelerator(quit.getAccelerator());
        action.setToolTipText(quit.getToolTipText());
    
	    menu.add(action);
	    return menu;	    	    
	}
	
	private MenuManager createToolsMenu(IWorkbenchWindow window){
	    MenuManager menu = new MenuManager("Tools", "tools");
	    menu.add(new GroupMarker("start"));	    	    
	    menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(ActionFactory.PREFERENCES.create(window));
	    menu.add(new GroupMarker("end"));
	    return menu;
	}
	
	//IVC Website, User Guide, Update, Manage BasicConfiguration, Developer, About 
	private MenuManager createHelpMenu(IWorkbenchWindow window) {
		MenuManager menu = new MenuManager("Help", IWorkbenchActionConstants.M_HELP); //$NON-NLS-1$
		// Welcome or intro page would go here
		//menu.add(ActionFactory.HELP_CONTENTS.create(window));
		// Tips and tricks page would go here
		menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_START));
			
		menu.add(new Action("IVC Sourceforge"){
		   public void run(){
		       Program.launch(IVC_WEBSITE);
		   }		   
		});

		menu.add(new Action("IVC Software"){
		    public void run(){
		        Program.launch(USERGUIDE_SITE);
		    }
		});
		
		//software updates menu			
		final Shell s = window.getShell();
		Action update = new Action("Update..."){
		    public void run(){
		        UpdateManagerUI.openInstaller(s);
		    }		    
		};
		update.setImageDescriptor(findAndInstallImage);				
		Action manage = new Action("Manage Configuration..."){
		    public void run(){
		        UpdateManagerUI.openConfigurationManager(s);
		    }
		};
		manage.setImageDescriptor(manageImage);
		menu.add(update);
		menu.add(manage);		

		//Developer menu -> JavaDoc, Developer Guide, SourceForge Site.
		MenuManager developer = new MenuManager("Developer");
		developer.add(new Action("Javadoc"){
		    public void run(){
		        Program.launch(JAVADOCS_SITE);
		    }
		});
		developer.add(new Action("Developer Guide"){
		   public void run(){
		       Program.launch(DEVELOPER_SITE);
		   }
		});
		developer.add(new Action("SourceForge Site"){
		    public void run(){
		        Program.launch(SOURCEFORGE_SITE);
		    }
		});		
		menu.add(developer);	
		
		menu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new GroupMarker(IWorkbenchActionConstants.HELP_END));		
		
		// About should always be at the bottom
		menu.add(new Separator());
		menu.add(ActionFactory.ABOUT.create(window));
		
		return menu;
	}
	
    private boolean confirmExit(){
	    final Configuration cfg = IVC.getInstance().getConfiguration();
	    boolean noPrompt = cfg.getBoolean(IVCPreferences.EXIT_WITHOUT_PROMPT);
	    if(noPrompt)
	        return true;	    
	    
        IVCDialog dialog = new IVCDialog(IVCApplication.getShell(), "Confirm Exit", IVCDialog.QUESTION){
            public void createDialogButtons(Composite parent) {
                Button confirm = new Button(parent, SWT.PUSH);
                confirm.setText("Yes");
                confirm.addSelectionListener(new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e) {
                        close(true);
                    }
                });
                Button deny = new Button(parent, SWT.PUSH);
                deny.setText("No");
                deny.addSelectionListener(new SelectionAdapter(){
                    public void widgetSelected(SelectionEvent e) {
                        close(false);
                    }
                });
            }

            public Composite createContent(Composite parent) {
                Composite content = new Composite(parent, SWT.NONE);
                content.setLayout(new RowLayout());
                final Button checkbox = new Button(content, SWT.CHECK);
                checkbox.setText("Always exit without prompt");
                checkbox.setSelection(cfg.getBoolean(IVCPreferences.EXIT_WITHOUT_PROMPT));
                checkbox.addSelectionListener(new SelectionAdapter(){
                   public void widgetSelected(SelectionEvent e){
                       cfg.setValue(IVCPreferences.EXIT_WITHOUT_PROMPT, checkbox.getSelection());
                   }                    
                });
                return content;
            }            
        };
        dialog.setDescription("Are you sure you want to exit?");
        dialog.setDetails("By selecting the checkbox above, this confirmation" +
        		" dialog will be avoided in the future if you do not wish to be" +
        		" asked for confirmation of exit.  This preference is toggleable" +
        		" in the preferences window under the \"General IVC\" section.");
        return dialog.open();
    }    
}