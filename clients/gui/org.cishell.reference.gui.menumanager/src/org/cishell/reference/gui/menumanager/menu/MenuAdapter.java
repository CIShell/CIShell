/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 16, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.reference.gui.menumanager.menu;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MenuAdapter implements AlgorithmProperty {
	public static final String DEFAULT_MENU_FILE_NAME = "default_menu.xml";

	// Tags in DEFAULT_MENU_FILE_NAME.
    public static final String TAG_TOP_MENU = "top_menu";
    public static final String TAG_MENU = "menu";    
    public static final String TYPE_ATTRIBUTE = "type";
    public static final String NAME_ATTRIBUTE = "name";
    public static final String PID_ATTRIBUTE = "pid";
    public static final String PRESERVED_GROUP = "group";
    public static final String PRESERVED_BREAK = "break";
    public static final String PRESERVED_EXIT = "Exit";
    public static final String PRESERVED_SERVICE_PID = "service.pid";
    public static final String PRESERVED = "preserved";

    public static final String HELP_DESK_EMAIL_ADDRESS = "nwb-helpdesk@googlegroups.com";

    private String toolName;
    private String toolTicketURL;
    private IMenuManager menuManager;
    private Shell shell;
    private BundleContext bundleContext;
    private CIShellContext ciShellContext;
    private Map<String, Action> algorithmsToActions;
    private Map<Action, IMenuManager> actionsToMenuManagers;
    private ContextListener contextListener;
    private IWorkbenchWindow workbenchWindow;
    private LogService logger;
    /*
     * This map holds a pid as a key and the corresponding ServiceReference as a value.
     * It is built when preprocessServiceBundles() is invoked.
     * Then the entries are gradually removed when the pids are specified in
     *  DEFAULT_MENU_FILE_NAME.
     * If any entries are left, in processLeftServiceBundles(), those plug-ins that have specified
     *  the menu_path and label but are not listed in DEFAULT_MENU_FILE_NAME will be added on to
     *  the menu.
     */
    private Map<String, ServiceReference> pidsToServiceReferences;
    /*
     * This is the exactly same copy of pidsToServiceReferences. 
     * Since some plug-ins could display on menu more than once, it provides a map between a pid
     *  and a serviceReference while in pidsToServiceReferences that pid has been removed. 
     */
    private Map<String, ServiceReference> pidsToServiceReferencesCopy;
    private Document documentObjectModel;

    private Runnable updateAction = new Runnable() {
        public void run() {
            MenuAdapter.this.menuManager.updateAll(true);
        }
    };

    private Runnable stopAction = new Runnable() {
        public void run() {
            String[] algorithmKeys =
            	MenuAdapter.this.algorithmsToActions.keySet().toArray(new String[]{});
            
            for (int ii = 0; ii < algorithmKeys.length; ii++) {
                Action item = MenuAdapter.this.algorithmsToActions.get(algorithmKeys[ii]);
                IMenuManager targetMenu = MenuAdapter.this.actionsToMenuManagers.get(item);
                
                targetMenu.remove(item.getId());
                MenuAdapter.this.algorithmsToActions.remove(algorithmKeys[ii]);
                MenuAdapter.this.actionsToMenuManagers.remove(item);
            }
        }        
    };

    public MenuAdapter(
    		String toolName,
    		String toolTicketURL,
    		IMenuManager menuManager,
    		Shell shell, 
            BundleContext bundleContext,
            CIShellContext ciShellContext,
            IWorkbenchWindow workbenchWindow) {
    	this.toolName = toolName;
    	this.toolTicketURL = toolTicketURL;
        this.menuManager = menuManager;
        this.shell = shell;
        this.bundleContext = bundleContext;
        this.ciShellContext = ciShellContext;
        this.workbenchWindow = workbenchWindow;
        this.algorithmsToActions = new HashMap<String, Action>();
        this.actionsToMenuManagers = new HashMap<Action, IMenuManager>();
        this.pidsToServiceReferences = new HashMap<String, ServiceReference>();
        this.pidsToServiceReferencesCopy = new HashMap<String, ServiceReference>();
        this.logger = (LogService)this.ciShellContext.getService(LogService.class.getName());

        /*
    	 * The intention of this clearShortcuts was to programmatically clear all of the
    	 *  bound shortcuts, so any found in our own plugins wouldn't have conflicts.
    	 * Doing this doesn't immediately seem very possible, though there may be some promise in
    	 *  "redirecting" the actions taken by the shortcuts.
    	 * (See: http://dev.eclipse.org/newslists/news.eclipse.platform/msg79882.html )
    	 * As a note, the keyboard shortcuts are actually called accelerator key codes, and the
    	 *  machinery that makes them work is very deeply ingrained in Eclipse.
    	 * The difficulties I faced with trying to clear already-bound shortcuts has led me to
    	 *  suspect three possible things:
    	 *   We may be "abusing" Eclipse by using it as the foundation for our own applications.
    	 *   There may be a way to customize/configure (or interface with) the culprit plugin
    	 *    that's binding the shortcuts before us (org.eclipse.ui.workbench).  This seems to be
    	 *    highly-likely, and more research on the matter can probably be justified at
    	 *    some point.
    	 *   The intended way TO work around the shortcuts already being bound is to redirect the
    	 *    actions taken by them, as mentioned above.
    	 * Either way, to get these shortcuts working, I'm just going to use non-standard key
    	 *  combinations.
    	 */
        //clearShortcuts();

        /*
         * Appears to add a context listener which updates the menu item whenever a corresponding
         *  change occurs in the bundle (registers, unregisters, etc...).
         */
        String filter = "(" + Constants.OBJECTCLASS + "=" + AlgorithmFactory.class.getName() + ")";
        this.contextListener = new ContextListener();

        try {
            bundleContext.addServiceListener(this.contextListener, filter);
            preprocessServiceBundles();
            String applicationLocation = System.getProperty("osgi.configuration.area");
         
            /*
             * Comments below refer to problems with earlier versions of this document.
             * Keeping these around for now, as well as the system.out.printlns, until we are sure
             *  that the current fix works.
             */
            
            /*
             * This is a temporary fix. A bit complex to explain the observation I got so far.
             * On Windows XP, 
             *  app_location =
             *   file:/C:/Documents and Settings/huangb/Desktop/nwb-sept4/nwb/configuration/
             * If I didn't trim "file:/", on some windows machines 
             *  new File(fileFullpath).exists()
             *  will return false, and initializaMenu() will be invoked.
             * When initializaMenu() is invoked, not all required top menus will show up.
             * So either Bruce code or Tim's fix has some problems. Can not append top menu such as 
             *  Tools-->Scheduler if Tools is not specified in the XML.
             * If pass trimmed file path
             *  C:/Documents and Settings/huangb/Desktop/nwb-sept4/nwb/configuration/
             *  to createMenuFromXML, on some machines,
             *  URL = C:/Documents and Settings/huangb/Desktop/nwb-sept4/nwb/configuration/ 
             *  is a bad one, and can not create a document builder instance and the
             *  DOM representation of the XML file.
             * 
             * This piece of code needs to be reviewed and refactored!!!
			 */
            
            /*
             * Better to use System.err, since it prints the stream immediately instead of storing
             *  it in a buffer which might be lost if there is a crash.
             */
            String fileFullPath = applicationLocation + DEFAULT_MENU_FILE_NAME; 
            URL configurationDirectoryURL = new URL(fileFullPath);

            try {
            	configurationDirectoryURL.getContent();
            	//System.out.println(">>>config.ini Exists!");
            	createMenuFromXML(fileFullPath);
            	processLeftServiceBundles();  
            } catch (IOException ioException) {
            	ioException.printStackTrace();
            	//System.err.println("config.ini does not exist... Reverting to backup plan");
            	initializeMenu();
            }

            Display.getDefault().asyncExec(this.updateAction);
        } catch (InvalidSyntaxException invalidSyntaxException) {
        	// TODO: Improve this error message.  "Invalid Syntax" is terrible!
            this.logger.log(LogService.LOG_DEBUG, "Invalid Syntax", invalidSyntaxException);
        } catch (Throwable exception) {
        	/*
        	 * TODO: Improve this.
        	 * Should catch absolutely everything catchable. Will hopefully reveal the error coming
        	 *  out of the URI constructor.
        	 * No time to test today, just commiting this for testing later.
        	 */
        	exception.printStackTrace();
        }
    }
        
    /*
     * This method scans all service bundles. If a bundle specifies  menu_path and label,
     *  get service.pid of this bundle (key), let the service reference as the value, and put
     *  key/value pair  to pidsToServiceReferences for further processing.
     */
    private void preprocessServiceBundles() throws InvalidSyntaxException {
        ServiceReference[] serviceReferences =
        	this.bundleContext.getAllServiceReferences(AlgorithmFactory.class.getName(), null);

        if (serviceReferences != null){
        	for (int ii = 0; ii < serviceReferences.length; ii++) {
        		String path = (String)serviceReferences[ii].getProperty(MENU_PATH);

        		if (path == null) {
        			continue;
        		} else {       
        			String pid = (String)serviceReferences[ii].getProperty(PRESERVED_SERVICE_PID);
        			this.pidsToServiceReferences.put(pid.toLowerCase().trim(), serviceReferences[ii]);
        			this.pidsToServiceReferencesCopy.put(
        				pid.toLowerCase().trim(), serviceReferences[ii]);
        		}
        	}
        }
    }
    /*
     * Parse DEFAULT_MENU_FILE_NAME file.
     * For each menu node, get the value of the attribute pid.
     * Check if the pid exists in pidsToServiceReferences.
     * If so, get the action and add to the parent menu.
     * If not, ignore this menu.
     * At the end of each top menu or subgroup menu or before help menu,  add "additions" so that
     *  new algorithms can be added on later. 
     * 
     * What is the reasonable logic?
     * If a plug-in has been specified in the DEFAULT_MENU_FILE_NAME, always use that menu layout
     * If a plug-in specified in the DEFAULT_MENU_FILE_NAME, use the menu_path specified in the
     *  properties file.
     * If a plug-in specifies a label in the properties file, always use it.
     */
    private void createMenuFromXML(String menuFilePath) throws InvalidSyntaxException{
    	parseXMLFile(menuFilePath);    	
    	// Get the root elememt.
		Element documentElement = this.documentObjectModel.getDocumentElement();
			
		// Get a nodelist of the top menu elements.
		NodeList topMenuList = documentElement.getElementsByTagName(TAG_TOP_MENU);

		if ((topMenuList != null) && (topMenuList.getLength() > 0)) {
			for (int ii = 0; ii < topMenuList.getLength(); ii++) {				
				Element element = (Element)topMenuList.item(ii);
				processTopMenu(element);				
			}
		}    	
    }  
    
    private void processTopMenu(Element topMenuNode) {
    	/*	
    	 * The File and Help menus are created in ApplicationActionBarAdvisor.java.
    	 * This function now parses the XML file and appends the new menus/menu items to the
    	 *  correct group.
    	 * We first check to see if the menu already exists in our MenuBar.
    	 * If it does, we modify the already existing menu. If not, then we create a new Menu.
    	 * 
    	 * Additional code at: org.cishell.reference.gui.workspace.ApplicationActionBarAdvisor.java
    	 */
    	
    	String topMenuName = topMenuNode.getAttribute(NAME_ATTRIBUTE);
    	MenuManager topMenuBar = (MenuManager)this.menuManager.findUsingPath(topMenuName);

    	if (topMenuBar == null) {
    		topMenuBar = new MenuManager(topMenuName, topMenuName.toLowerCase());
    		this.menuManager.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, topMenuBar);
    	}
    	
    	// Second process submenu.
    	processSubMenu(topMenuNode, topMenuBar);
    }
    
    /*
     * Recursively process sub menu and group menu.
     */
    private void processSubMenu(Element menuNode, MenuManager parentMenuBar){
    	NodeList subMenuList =  menuNode.getElementsByTagName(TAG_MENU);

    	if ((subMenuList != null) && (subMenuList.getLength() > 0)) {
    		for (int ii = 0; ii < subMenuList.getLength(); ii++) {			
				Element element = (Element)subMenuList.item(ii);
				
				/*
				 * Only process direct children nodes and drop all grand or grand of grand
				 *  children nodes.
				 * TODO: Why?
				 */
				if (!element.getParentNode().equals(menuNode)) {
					continue;
				}

				String menuType = element.getAttribute(TYPE_ATTRIBUTE);
				String algorithmPID = element.getAttribute(PID_ATTRIBUTE);

				if (((menuType == null) || (menuType.length() == 0)) && (algorithmPID != null)) {
					processAMenuNode(element, parentMenuBar);
				} else if (menuType.equalsIgnoreCase(PRESERVED_GROUP)) {
					String groupName = element.getAttribute(NAME_ATTRIBUTE);
					MenuManager groupMenuBar = new MenuManager(groupName, groupName.toLowerCase());
					parentMenuBar.add(groupMenuBar);
					processSubMenu(element, groupMenuBar);					
				}				
				else if (menuType.equalsIgnoreCase(PRESERVED_BREAK)){	
					/*
					 * It seems that the framework automatically takes care of issues such as
					 *  double separators and a separator at the top or bottom.
					 */					
					parentMenuBar.add(new Separator());
				}
				else if (menuType.equalsIgnoreCase(PRESERVED)){
					String menuName = element.getAttribute(NAME_ATTRIBUTE);
					if(menuName.equalsIgnoreCase(PRESERVED_EXIT) ){
					  //allow to add more menu before "File/Exit"	
					  if(parentMenuBar.getId().equalsIgnoreCase(IWorkbenchActionConstants.M_FILE)){
						parentMenuBar.add(new GroupMarker(START_GROUP));
						parentMenuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
						parentMenuBar.add(new GroupMarker(END_GROUP));
					  }
					  IWorkbenchAction exitAction = ActionFactory.QUIT.create(workbenchWindow);
					  parentMenuBar.add(new Separator());
					  parentMenuBar.add(exitAction);						
					}
				}
			}
			//allow to append new submenu(s) at the bottom under each top menu
			//except "File" and "Help"
			if(!parentMenuBar.getId().equalsIgnoreCase(IWorkbenchActionConstants.M_FILE)&&
			   !parentMenuBar.getId().equalsIgnoreCase(IWorkbenchActionConstants.M_HELP))			
			{			
				parentMenuBar.add(new GroupMarker(START_GROUP));
				parentMenuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
				parentMenuBar.add(new GroupMarker(END_GROUP));
			}
    	}
    }

    /*
     * TODO: Better name?
     * Process a menu (algorithm).	
     */
    private void processAMenuNode(Element menuNode, MenuManager parentMenuBar) {
 		String menuName = menuNode.getAttribute(NAME_ATTRIBUTE);
		String pid = menuNode.getAttribute(PID_ATTRIBUTE);

		if ((pid == null) || (pid.length() == 0)) {
			/*
			 * TODO: Check if the name is one of the preserved one, and add the default action if
			 *  it is?
			 */
		} else {
			// Check if the PID has registered in pidsToServiceReferences.
			if (this.pidsToServiceReferencesCopy.containsKey(pid.toLowerCase().trim())){
				ServiceReference serviceReference =
					this.pidsToServiceReferencesCopy.get(pid.toLowerCase().trim());
				this.pidsToServiceReferences.remove(pid.toLowerCase().trim());
    			AlgorithmAction action =
    				new AlgorithmAction(serviceReference, this.bundleContext, this.ciShellContext); 
    			String menuLabel = (String)serviceReference.getProperty(LABEL);

    			if ((menuName!= null) && (menuName.trim().length() > 0)) {
    				// Use the name specified in the XML to overwrite the label.
    				action.setText(menuName);
    				action.setId(getItemID(serviceReference));
    				parentMenuBar.add(action);
    				handleActionAccelerator(action, parentMenuBar, serviceReference);
    			} else {
    				if ((menuLabel != null) && (menuLabel.trim().length() > 0)) {
    					action.setText(menuLabel);
    					action.setId(getItemID(serviceReference));
    					parentMenuBar.add(action);
    					handleActionAccelerator(action, parentMenuBar, serviceReference);
    				} else {
    					/*
    					 * TODO: This is a problem: No label is specified in the plug-in's
    					 *  properties file and no name is specified in the XML file.
    					 */
    				}
    			}
				
			} else {
				String algorithmNotFoundFormat =
					"Oops!  %s tried to place an algorithm with the id '%s' " +
					"on the menu, but the algorithm could not be found.";
				String algorithmNotFoundMessage =
					String.format(algorithmNotFoundFormat, this.toolName, pid);
//				String algorithmNotFoundMessage =
//					"Oops! Network Workbench tried to place an algorithm with the id '" +
//					pid +
//					"' on the menu, but the algorithm could not be found.";
				String contactInformationFormat =
					"If you see this error, please contact %s, " +
					"or post a ticket on our bug tracker at: %s .";
				String contactInformationMessage = String.format(
					contactInformationFormat, HELP_DESK_EMAIL_ADDRESS, this.toolTicketURL);
//				String contactInformationMessage =
//					"If you see this error, please contact nwb-helpdesk@googlegroups.com, or " +
//					"post a ticket on our bug tracker at: " +
//					"http://cns-trac.slis.indiana.edu/trac/nwb .";
				this.logger.log(LogService.LOG_DEBUG, algorithmNotFoundMessage);
				this.logger.log(LogService.LOG_DEBUG, contactInformationMessage);
			}
		}
    }    
    
    private void parseXMLFile(String menuFilePath){
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setCoalescing(true);

		try {
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        
	        this.documentObjectModel = documentBuilder.parse(menuFilePath);	

		} catch(ParserConfigurationException parserConfigurationException) {
			parserConfigurationException.printStackTrace();
		} catch(SAXException saxException) {
			saxException.printStackTrace();
		} catch(IOException ioException) {
			ioException.printStackTrace();
		}
	}
    
    /*
     * Handle some service bundles that have specified the menu_path and label
     * but not specified in the DEFAULT_MENU_FILE_NAME
     */
    private void processLeftServiceBundles() {
    	if (!this.pidsToServiceReferences.isEmpty()){
    		for (String key : this.pidsToServiceReferences.keySet()) {
    			ServiceReference serviceReference = this.pidsToServiceReferences.get(key);
    			makeMenuItem(serviceReference);
    		}
//    		Object[] keys = this.pidsToServiceReferences.keySet().toArray();
//
//    		for (int ii = 0; ii < keys.length; ii++) {
//    			ServiceReference serviceReference =
//    				(ServiceReference)this.pidsToServiceReferences.get((String)keys[ii]);
//    			makeMenuItem(serviceReference);
//    		}    		
    	}    		
    }

    private void initializeMenu() throws InvalidSyntaxException {   
        ServiceReference[] serviceReferences = this.bundleContext.getAllServiceReferences(
        	AlgorithmFactory.class.getName(), null);
     
 		if (serviceReferences != null) {
            for (int ii = 0; ii < serviceReferences.length; ii++) {
                makeMenuItem(serviceReferences[ii]);
            }
        }
    }

    private class ContextListener implements ServiceListener {
        public void serviceChanged(ServiceEvent event) {
            switch (event.getType()) {
            case ServiceEvent.REGISTERED:
                makeMenuItem(event.getServiceReference());

                break;
            case ServiceEvent.UNREGISTERING:
                removeMenuItem(event.getServiceReference());

                break;
            case ServiceEvent.MODIFIED:
                updateMenuItem(event.getServiceReference());

                break;
            }
        }
    }
    
    private void makeMenuItem(ServiceReference serviceReference) {
        String path = (String) serviceReference.getProperty(MENU_PATH);
        String[] items = null;

        if (path != null) {
        	items = path.split("/");
        }

        IMenuManager menu = null;

        if ((items != null) && (items.length > 1)) {
            AlgorithmAction action =
            	new AlgorithmAction(serviceReference, this.bundleContext, this.ciShellContext);
            action.setId(getItemID(serviceReference));
            
            IMenuManager targetMenu = this.menuManager;
            String group = items[items.length - 1];
            
            for (int ii = 0; ii < items.length - 1; ii++) {
                menu = targetMenu.findMenuUsingPath(items[ii]);
 
                if ((menu == null) && (items[ii] != null)) {
                    menu = targetMenu.findMenuUsingPath(items[ii].toLowerCase());                  
                }
                
                if (menu == null) {                	
                    menu = createMenu(items[ii], items[ii]);
                    targetMenu.appendToGroup(ADDITIONS_GROUP, menu);
                }
                
                targetMenu = menu;
            }
            
            group = items[items.length - 1];
            IContributionItem groupItem = targetMenu.find(group);
            
            if (groupItem == null) {
                groupItem = new GroupMarker(group);
                targetMenu.appendToGroup(ADDITIONS_GROUP, groupItem);
            }
            
            targetMenu.appendToGroup(group, action);
            handleActionAccelerator(action, targetMenu, serviceReference);
            targetMenu.appendToGroup(group, new Separator());
            algorithmsToActions.put(getItemID(serviceReference), action);
            actionsToMenuManagers.put(action, targetMenu);
            
            Display.getDefault().asyncExec(this.updateAction);
        } else {
            this.logger.log(
            	LogService.LOG_DEBUG,
            	"Bad menu path for Algorithm: " + serviceReference.getProperty(LABEL));
        }
    }
    
    private String getItemID(ServiceReference serviceReference) {
    	return
    		serviceReference.getProperty("PID:" + Constants.SERVICE_PID) +
    		"-SID:" +
    		serviceReference.getProperty(Constants.SERVICE_ID);
    }

    private MenuManager createMenu(String name, String id){
        MenuManager menu = new MenuManager(name, id);
        menu.add(new GroupMarker(START_GROUP));
        menu.add(new GroupMarker(ADDITIONS_GROUP));
        menu.add(new GroupMarker(END_GROUP));

        return menu;
    }

    private void updateMenuItem(ServiceReference serviceReference) {
        Action item = (Action)this.algorithmsToActions.get(getItemID(serviceReference));
        
        if (item != null) {
        	this.logger.log(
        		LogService.LOG_DEBUG, "updateMenuItem for " + getItemID(serviceReference));
            item.setText("" + serviceReference.getProperty(LABEL));
        }
    }
    
    private void removeMenuItem(ServiceReference serviceReference) {
        String path = (String)serviceReference.getProperty(MENU_PATH);
        final Action item = this.algorithmsToActions.get(getItemID(serviceReference));
        
        if ((path != null) && (item != null)) {
            int index = path.lastIndexOf('/');

            if (index != -1) {
                path = path.substring(0, index);
                final IMenuManager targetMenu = this.menuManager.findMenuUsingPath(path);

                if (targetMenu != null) {
                	if (!this.shell.isDisposed()) {
                		this.shell.getDisplay().syncExec(new Runnable() {
                			public void run() {
                				targetMenu.remove(item.getId());
                			}
                		});
                    }

                    this.algorithmsToActions.remove(getItemID(serviceReference));
                    this.actionsToMenuManagers.remove(item);
                }
            }   
        }
    }

    public void stop() {
        this.bundleContext.removeServiceListener(this.contextListener);
        
        if (!this.shell.isDisposed()) {
            this.shell.getDisplay().syncExec(this.stopAction);
        }
    }

    //private void clearShortcuts() {
    	/*IWorkbench workbench = this.window.getWorkbench();
    	IBindingService bindingService =
    		(IBindingService)workbench.getService(IBindingService.class);
    	Binding[] bindings = bindingService.getBindings();
    	IHandlerService handlerService =
    		(IHandlerService)workbench.getService(IHandlerService.class);*/
    	//getLog().log(LogService.LOG_WARNING, "handlerService: " + handlerService);
    	//for (int ii = 0; ii < bindings.length; ii++) {
    		// getLog().log(LogService.LOG_INFO, "Binding[" + ii + "]: " + bindings[ii]);
    		/*String bindingInfo =
    			"\tcontextID: " + bindings[ii].getContextId() + "\n" +
    			"\tparameterized command: " + bindings[ii].getParameterizedCommand() + "\n" +
    			"\ttrigger sequence: " + bindings[ii].getTriggerSequence().format();
    		getLog().log(LogService.LOG_INFO, "Binding:\n" + bindingInfo);*/
    		
    		/*KeyBinding(
    			bindings[ii].getTriggerSequence(),
    			null,
    			bindings[ii].getSchemeId(),
    			bindings[ii].getContextId(),
    			null,
    			null,
    			null,
    			Binding.SYSTEM);*/
    	//}
    //}

    private void handleActionAccelerator(
    		Action action, IMenuManager parentMenuBar, ServiceReference serviceReference) {
    	action.setAccelerator(determineActionAcceleratorKeyCode(serviceReference, action));
    }

    private static int determineActionAcceleratorKeyCode(
    		ServiceReference serviceReference, Action action) {
    	String shortcutString = (String)serviceReference.getProperty(SHORTCUT);

        if (shortcutString != null) {
        	return Action.convertAccelerator(shortcutString);
        } else {
        	return 0;
        }
    }

    /*
     * printElementAttributes takes in a xml document, gets the nodes, then prints the attributes.
     * Copied from Java Tutorial on XML Parsing by Tim Kelley for debugging purposes.
     */
    static void printElementAttributes(Document doc)
    {
       NodeList nl = doc.getElementsByTagName("*");
       Element e;
       Node n;
       NamedNodeMap nnm;
     
       String attrname;
       String attrval;
       int i, len;
     
       len = nl.getLength();

       for (int j=0; j < len; j++)
       {
          e = (Element)nl.item(j);
          System.err.println(e.getTagName() + ":");
          nnm = e.getAttributes();
     
          if (nnm != null)
          {
             for (i=0; i<nnm.getLength(); i++)
             {
                n = nnm.item(i);
                attrname = n.getNodeName();
                attrval = n.getNodeValue();
                System.err.print(" " + attrname + " = " + attrval);
             }
          }
          System.err.println();
       }
    }
}
