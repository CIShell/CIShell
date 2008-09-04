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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
    private IMenuManager menuBar;
    private Shell shell;
    private BundleContext bContext;
    private CIShellContext ciContext;
    private Map algorithmToItemMap;
    private Map itemToParentMap;
    private ContextListener listener;
    private IWorkbenchWindow window;
    
    /*
     * This map holds a pid as a key and the corresponding 
     * ServiceReference as a value. It is built when 
     * preprocessServiceBundles() is invoked. Then the entries 
     * are gradually removed when the pids are specified in
     * the defaul_menu.xml. If any entries are left, in 
     * processLeftServiceBundles(), those plug-ins that have 
     * specified the menu_path and label but are not listed in
     * default_menu.xml will be added on to the menu.
     */
    private Map pidToServiceReferenceMap;
    /*
     * This is the exactly same copy of pidToServiceReferenceMap. 
     * Since some plug-ins could display on menu more than once, it 
     * provides a map between a pid and a ref while in pidToServiceReferenceMap
     * that pid has been removed. 
     */
    private Map pidToServiceReferenceMapCopy;
    private Document dom;
    private static String DEFAULT_MENU_FILE_NAME = "default_menu.xml";
    
    /*
     * The following section specify the tags in the default_menu.xml
     */
    private static String TAG_TOP_MENU = "top_menu";
    private static String TAG_MENU = "menu";    
    private static String ATTR_TYPE = "type";
    private static String ATTR_NAME = "name";
    private static String ATTR_PID = "pid";
    private static String PRESERVED_GROUP = "group";
    private static String PRESERVED_BREAK = "break";
    private static String PRESERVED_EXIT = "Exit";
    private static String PRESERVED_SERVICE_PID="service.pid";
    private static String PRESERVED = "preserved";
    


    
    public MenuAdapter(IMenuManager menu, Shell shell, 
            BundleContext bContext,CIShellContext ciContext,
            IWorkbenchWindow window) {
    	//basic initialization
        this.menuBar = menu;
        this.shell = shell;
        this.bContext = bContext;
        this.ciContext = ciContext;
        this.window = window;
        
        this.algorithmToItemMap = new HashMap();
        this.itemToParentMap = new HashMap();
        
        pidToServiceReferenceMap = new HashMap();
        pidToServiceReferenceMapCopy = new HashMap();
        
        //appears to add a listener which updates the menu item whenever a 
        //corresponding change occurs in the bundle (registers, unregisters, etc...)
        String filter = "(" + Constants.OBJECTCLASS + 
                        "=" + AlgorithmFactory.class.getName() + ")";
        listener = new ContextListener();
       
        
        try {
        
            bContext.addServiceListener(listener, filter);
            preprocessServiceBundles();
            String app_location = System.getProperty("osgi.configuration.area");
         
            //Comments below refer to problems with earlier versions of this document.
            //Keeping these around for now, as well as the system.out.printlns,
            //until we are sure that the current fix works.
            
            /*
             * This is a temporary fix. A bit complex to explain the observation 
             * I got so far. On Windows XP 
             * app_location = file:/C:/Documents and Settings/huangb/Desktop/
             * nwb-sept4/nwb/configuration/
             * If I didn't trim "file:/", on some windows machines 
             * new File(fileFullpath).exists() will return false, and 
             * initializaMenu() will be invoked. When initializaMenu() is invoked,
             * not all required top menus will show up. So either Bruce code 
             * or Tim's fix has some problems. Can not append top menu such as 
             * Tools-->Scheduler if Tools is not specified in the XML
             * If pass trimed file path C:/Documents and Settings/huangb/Desktop/
             * nwb-sept4/nwb/configuration/ to createMenuFromXML, on some machines,
             * URL =  C:/Documents and Settings/huangb/Desktop/nwb-sept4/nwb/configuration/ 
             * is a bad one, and can not create a document builder instance and the
             * DOM representation of the XML file.
             * 
             * This piece of code needs to be reviewed and refactored!!!
			 */
            
            System.out.println(">>>app_location = "+app_location);
            String fileFullPath = app_location + DEFAULT_MENU_FILE_NAME; 
            System.out.println(">>>fileFullPath = " + fileFullPath);
            URI configurationDirectoryURI = new URI(fileFullPath);
            System.out.println(">>>URI = " + configurationDirectoryURI.toString());
            System.out.println(">>> file at URI = " + new File(configurationDirectoryURI).toString());
            if (new File(configurationDirectoryURI).exists()) {
            	System.out.println("config.ini Exists!");
            String fullpath = app_location + DEFAULT_MENU_FILE_NAME;
            createMenuFromXML(fullpath);
            } else {
            	System.out.println("config.ini does not exist... Reverting to backup plan");
            processLeftServiceBundles();   
            }
            Display.getDefault().asyncExec(updateAction);
     
        } catch (InvalidSyntaxException e) {
            getLog().log(LogService.LOG_DEBUG, "Invalid Syntax", e);
        } catch (URISyntaxException e) {
        	getLog().log(LogService.LOG_DEBUG, "Invalid Syntax", e);
        }
    }
        
    /*
     * This method scans all service bundles. If a bundle specifies 
     * menu_path and label, get service.pid of this bundle (key), let the service
     * reference as the value, and put key/value pair 
     * to pidToServiceReferenceMap for further processing.
     * 
     */
    private void preprocessServiceBundles() throws InvalidSyntaxException{
        ServiceReference[] refs = bContext.getAllServiceReferences(
                AlgorithmFactory.class.getName(), null);
        if (refs != null){
        	for (int i=0; i < refs.length; i++) {
        		String path = (String)refs[i].getProperty(MENU_PATH);
        		String label = (String)refs[i].getProperty(LABEL);
        		if (path == null){
        			continue;
        		}
        		else{       
        			String pid = (String)refs[i].getProperty(PRESERVED_SERVICE_PID);
        			pidToServiceReferenceMap.put(pid.toLowerCase().trim(), refs[i]);
        			pidToServiceReferenceMapCopy.put(pid.toLowerCase().trim(), refs[i]);
        		}
        	}
        }
    }
    /*
     * Parse default_menu.xml file. For each menu node, get the value of the attribut "pid"
     * check if the pid exists in pidToServiceReferenceMap. If so, get the action and add to the parent menu
     * If not, ignore this menu. At the end of each top menu or subgroup menu or before help menu, 
     * add "additions" so that new algorithms can be added on later 
     * 
     * What is the reasonable logic?
     * If a plug-in has been specified in the default_menu.xml, always use that menu layout
     * If a plug-in has not been specified in the default_menu.xml, use the menu_path 
     * specified in the properties file.
     * If a plug-in specifies a label in the properties file, always use it.
     *
     */
    private void createMenuFromXML(String menuFilePath) throws InvalidSyntaxException{
    	parseXmlFile(menuFilePath);    	
    	//get the root elememt
		Element docEle = dom.getDocumentElement();
			
		//get a nodelist of the top menu elements
		NodeList topMenuList = docEle.getElementsByTagName(TAG_TOP_MENU);
		if(topMenuList != null && topMenuList.getLength() > 0) {
			for(int i = 0 ; i < topMenuList.getLength();i++) {				
				Element el = (Element)topMenuList.item(i);
				processTopMenu(el);				
			}
		}    	
    }  
    
    private void processTopMenu (Element topMenuNode){    	
    	MenuManager topMenuBar = null;    	
    
    	/*	
    	 * The File and Help menus are created in ApplicationActionBarAdvisor.java
    	 * This function now parses the XML file and appends the new menus/menu items to the correct group
    	 * We first check to see if the menu already exists in our MenuBar. If it does, we modify the already
    	 * existing menu. If not, then we create a new Menu.
    	 * 
    	 * Modified by: Tim Kelley
    	 * Date: May 8-9, 2007
    	 * Additional code at: org.cishell.reference.gui.workspace.ApplicationActionBarAdvisor.java
    	 */
    	
    	String topMenuName = topMenuNode.getAttribute(ATTR_NAME);
    	if((topMenuBar = (MenuManager)menuBar.findUsingPath(topMenuName)) == null){  		//Check to see if menu exists
    		topMenuBar= new MenuManager(topMenuName, topMenuName.toLowerCase());    		//Create a new menu if it doesn't
    		menuBar.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, topMenuBar);
    	}
    	
    	//Second process submenu
    	processSubMenu(topMenuNode, topMenuBar);
    }
    
    /*
     * Recursively process sub menu and group menu
     */
    private void processSubMenu (Element menuNode, MenuManager parentMenuBar){
       	
    	NodeList subMenuList =  menuNode.getElementsByTagName(TAG_MENU);
    	if(subMenuList != null && subMenuList.getLength() > 0) {
    		for(int i = 0 ; i < subMenuList.getLength();i++) {			
				Element el = (Element)subMenuList.item(i);
				
				//only process direct children nodes and
				//drop all grand or grand of grand children nodes
				if (!el.getParentNode().equals(menuNode))
					continue;
				
				String menu_type = el.getAttribute(ATTR_TYPE);
				String pid=el.getAttribute(ATTR_PID);
				if ((menu_type == null || menu_type.length()==0)&& pid !=null){
					processAMenuNode(el, parentMenuBar);
				}				
				else if (menu_type.equalsIgnoreCase(PRESERVED_GROUP)){
					String groupName = el.getAttribute(ATTR_NAME);
					MenuManager groupMenuBar = new MenuManager(groupName, groupName.toLowerCase());
					parentMenuBar.add(groupMenuBar);
					processSubMenu(el, groupMenuBar);					
				}				
				else if (menu_type.equalsIgnoreCase(PRESERVED_BREAK)){	
					//It seems that Framework automatically takes care of issues
					//such as double separators, a separator at the top or bottom					
					parentMenuBar.add(new Separator());
				}
				else if (menu_type.equalsIgnoreCase(PRESERVED)){
					String menuName = el.getAttribute(ATTR_NAME);
					if(menuName.equalsIgnoreCase(PRESERVED_EXIT) ){
					  //allow to add more menu before "File/Exit"	
					  if(parentMenuBar.getId().equalsIgnoreCase(IWorkbenchActionConstants.M_FILE)){
						parentMenuBar.add(new GroupMarker(START_GROUP));
						parentMenuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
						parentMenuBar.add(new GroupMarker(END_GROUP));
					  }
					  IWorkbenchAction exitAction = ActionFactory.QUIT.create(window);
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
     * process a menu (algorithm)	
     */
    private void processAMenuNode(Element menuNode, MenuManager parentMenuBar ){
 		String menuName = menuNode.getAttribute(ATTR_NAME);
		String pid = menuNode.getAttribute(ATTR_PID);
		if (pid == null || pid.length()==0){
			//check if the name is one of the preserved one
			//if so add the default action
		}
		else{
			//check if the pid has registered in pidToServiceReferenceMap
			if (pidToServiceReferenceMapCopy.containsKey(pid.toLowerCase().trim())){
				ServiceReference ref = (ServiceReference) pidToServiceReferenceMapCopy.
											get(pid.toLowerCase().trim());
				pidToServiceReferenceMap.remove(pid.toLowerCase().trim());
    			AlgorithmAction action = new AlgorithmAction(ref, bContext, ciContext); 
    			String menuLabel = (String)ref.getProperty(LABEL);
    			if(menuName!= null && menuName.trim().length()>0){
    				//use the name specified in the xml to overwrite the label
    				action.setText(menuName);
    				action.setId(getItemID(ref));
    				parentMenuBar.add(action);
    			}
    			else{
    				if (menuLabel!= null && menuLabel.trim().length()>0){
    					action.setText(menuLabel);
    					action.setId(getItemID(ref));
        				parentMenuBar.add(action);
    				}
    				else {
    					//this is a problem -- no label is specified in the plug-in's properties file
    					//and no name is specified in the xml file.
    				}
    			}
				
			}
			else{
				//otherwise log the error
				getLog().log(LogService.LOG_DEBUG, 
	                    "Oops! Network Workbench tried to place an algorithm with the id '" + pid + "' on the menu, but the algorithm could not be found.");
				getLog().log(LogService.LOG_DEBUG, "If you see this error, please contact nwb-helpdesk@googlegroups.com, or post a ticket on our bug tracker at " +
						"http://cns-trac.slis.indiana.edu/trac/nwb  .");
			}
		}
    }    
    
    private void parseXmlFile(String menuFilePath){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setCoalescing(true);
		try {			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file	        
	        dom = db.parse(menuFilePath);	
	    // printElementAttributes(dom);

		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
    
    /*
     * Handle some service bundles that have specified the menu_path and label
     * but not specified in the default_menu.xml
     */
    private void processLeftServiceBundles(){
    	if (!pidToServiceReferenceMap.isEmpty()){
    		Object[] keys = pidToServiceReferenceMap.keySet().toArray();
    		for (int i=0; i<keys.length; i++){
    			ServiceReference ref= (ServiceReference) 
    						pidToServiceReferenceMap.get((String)keys[i]);
    			makeMenuItem(ref);
    		}    		
    	}    		
    }
    

    private void initializeMenu() throws InvalidSyntaxException{   
        ServiceReference[] refs = bContext.getAllServiceReferences(
                AlgorithmFactory.class.getName(), null);
     
 		if (refs != null) {
            for (int i=0; i < refs.length; i++) {
                makeMenuItem(refs[i]);
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
    
    private void makeMenuItem(ServiceReference ref) {
        String path = (String)ref.getProperty(MENU_PATH);
        String[] items = (path == null) ? null : path.split("/");
        IMenuManager menu = null;
        if (items != null && items.length > 1) {
            AlgorithmAction action = new AlgorithmAction(ref, bContext, ciContext);
            action.setId(getItemID(ref));
            
            IMenuManager targetMenu = menuBar;
            String group = items[items.length-1];
            
            for (int i=0; i < items.length-1; i++) {
            
                menu = targetMenu.findMenuUsingPath(items[i]);
 
                if (menu == null && items[i] != null) {
                    menu = targetMenu.findMenuUsingPath(items[i].toLowerCase());                  
                }
                
                if (menu == null) {                	
                    menu = createMenu(items[i],items[i]);
                    targetMenu.appendToGroup(ADDITIONS_GROUP, menu);
                }
                
                targetMenu = menu;
            }
            
            group = items[items.length-1];
            IContributionItem groupItem = targetMenu.find(group);
            
            if (groupItem == null) {
                groupItem = new GroupMarker(group);
                targetMenu.appendToGroup(ADDITIONS_GROUP, groupItem);
            }
            
            targetMenu.appendToGroup(group, action);
            targetMenu.appendToGroup(group, new Separator());
            algorithmToItemMap.put(getItemID(ref), action);
            itemToParentMap.put(action, targetMenu);
            
            Display.getDefault().asyncExec(updateAction);
        } else {
            getLog().log(LogService.LOG_DEBUG, 
                    "Bad menu path for Algorithm: " + ref.getProperty(LABEL));
        }
    }
    
    private Runnable updateAction = new Runnable() {
        public void run() {
            menuBar.updateAll(true);
        }
    };
    
    private String getItemID(ServiceReference ref) {
    	return ref.getProperty("PID:" + Constants.SERVICE_PID) + "-SID:" + 
                                ref.getProperty(Constants.SERVICE_ID);
    }
    
    private MenuManager createMenu(String name, String id){
        MenuManager menu = new MenuManager(name, id);
        menu.add(new GroupMarker(START_GROUP));
        menu.add(new GroupMarker(ADDITIONS_GROUP));
        menu.add(new GroupMarker(END_GROUP));
        return menu;
    }
    
    private void updateMenuItem(ServiceReference ref) {
        Action item = (Action) algorithmToItemMap.get(getItemID(ref));
        
        if (item != null) 
            item.setText(""+ref.getProperty(LABEL));
    }
    
    private void removeMenuItem(ServiceReference ref) {
        String path = (String)ref.getProperty(MENU_PATH);
        final Action item = (Action) algorithmToItemMap.get(getItemID(ref));
        
        if (path != null && item != null) {
            int index = path.lastIndexOf('/');
            if (index != -1) {
                path = path.substring(0, index);
                
                final IMenuManager targetMenu = menuBar.findMenuUsingPath(path);

                if (targetMenu != null) {
                    
                    if (!shell.isDisposed()) {
                        shell.getDisplay().syncExec(new Runnable() {
                            public void run() {
                                targetMenu.remove(item.getId());
                            }});
                    }
                    
                    
                    algorithmToItemMap.remove(getItemID(ref));
                    itemToParentMap.remove(item);
                }
            }   
        }
    }
    
    private Runnable stopAction = new Runnable() {
        public void run() {
            String[] algs = (String[])
                    algorithmToItemMap.keySet().toArray(new String[]{});
            
            for (int i=0; i < algs.length; i++) {
                Action item = (Action)algorithmToItemMap.get(algs[i]);
                IMenuManager targetMenu = (IMenuManager)itemToParentMap.get(item);
                
                targetMenu.remove(item.getId());
                algorithmToItemMap.remove(algs[i]);
                itemToParentMap.remove(item);
            }
        }        
    };
    
    public void stop() {
        bContext.removeServiceListener(listener);
        
        if (!shell.isDisposed()) {
            shell.getDisplay().syncExec(stopAction);
        }
    }
    
    private LogService getLog() {
        return (LogService) ciContext.getService(LogService.class.getName());
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
