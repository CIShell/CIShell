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

import java.util.HashMap;
import java.util.Map;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.AlgorithmProperty;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;


public class MenuAdapter implements AlgorithmProperty {
    private IMenuManager menuBar;
    private Shell shell;
    private BundleContext bContext;
    private CIShellContext ciContext;
    private Map algorithmToItemMap;
    private Map itemToParentMap;
    private ContextListener listener;

    public MenuAdapter(IMenuManager menu, Shell shell, 
            BundleContext bContext,CIShellContext ciContext) {
        this.menuBar = menu;
        this.shell = shell;
        this.bContext = bContext;
        this.ciContext = ciContext;
        this.algorithmToItemMap = new HashMap();
        this.itemToParentMap = new HashMap();
                
        String filter = "(" + Constants.OBJECTCLASS + 
                        "=" + AlgorithmFactory.class.getName() + ")";
        
        try {
            listener = new ContextListener();
            bContext.addServiceListener(listener, filter);
            initializeMenu();
        } catch (InvalidSyntaxException e) {
            getLog().log(LogService.LOG_ERROR, "Invalid Syntax", e);
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
        
        if (items != null && items.length > 1) {
            AlgorithmAction action = new AlgorithmAction(ref, bContext, ciContext);
            action.setId(getItemID(ref));
            
            IMenuManager targetMenu = menuBar;
            String group = items[items.length-1];
            
            for (int i=0; i < items.length-1; i++) {
                IMenuManager menu = targetMenu.findMenuUsingPath(items[i]);
                
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
            algorithmToItemMap.put(getItemID(ref), action);
            itemToParentMap.put(action, targetMenu);
            
            Display.getDefault().asyncExec(updateAction);
        } else {
//            getLog().log(LogService.LOG_WARNING, 
//                    "Bad menu path for Algorithm: " + ref.getProperty(LABEL));
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
}
