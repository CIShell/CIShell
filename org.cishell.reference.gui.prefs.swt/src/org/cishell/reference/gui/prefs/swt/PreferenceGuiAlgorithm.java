package org.cishell.reference.gui.prefs.swt;

import java.util.Arrays;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.data.Data;
import org.cishell.gui.prefgui.preferencepages.BlankPreferencePage;
import org.cishell.gui.prefgui.preferencepages.CIShellPreferencePage;
import org.cishell.reference.prefs.admin.PrefAdmin;
import org.cishell.reference.prefs.admin.PrefPage;
import org.cishell.reference.prefs.admin.PrefPageComparator;
import org.cishell.reference.prefs.admin.PreferenceOCD;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.cm.Configuration;
import org.osgi.service.log.LogService;


public class PreferenceGuiAlgorithm implements Algorithm {
    Data[] data;
    Dictionary parameters;
    CIShellContext context;
    
    PrefAdmin prefAdmin;
    
    LogService log;
    
    public PreferenceGuiAlgorithm(Data[] data, Dictionary parameters, CIShellContext context,
    		PrefAdmin prefAdmin, LogService log) {
        this.data = data;
        this.parameters = parameters;
        this.context = context;
        
        this.prefAdmin = prefAdmin;    
        this.log = log;
    }

    public Data[] execute() {
    	PreferenceManager prefManager = new PreferenceManager();
    	
    	addGlobalPreferences(prefManager);
    	addLocalPreferences(prefManager);
    	addParamPreferences(prefManager);
    	
    	Shell parentShell = getParentShell();
		PreferenceGUIRunnable prefGUIRunnable = new PreferenceGUIRunnable(parentShell, prefManager);
		Thread preferenceGUIThread = new Thread(prefGUIRunnable);
    
		//We must tell SWT to run the preference dialog, instead of running it directly ourselves
		parentShell.getDisplay().asyncExec(preferenceGUIThread);
    	
    	return null;
    }
    
    private void addGlobalPreferences(PreferenceManager prefManager) {
    	PrefPage[] globalPrefPages = prefAdmin.getGlobalPrefPages();
    	
    	BlankPreferencePage globalPrefPageRoot = new BlankPreferencePage(1, "General Preferences", "Contains preferences that change the workbench's functionality.");
    	PreferenceNode rootNode = new PreferenceNode("General Preferences Root", globalPrefPageRoot);
    	prefManager.addToRoot(rootNode);
    	
    	addPrefPages(globalPrefPages, rootNode);
    }
    
    private void addLocalPreferences(PreferenceManager prefManager) {
    	PrefPage[] localPrefPages = prefAdmin.getLocalPrefPages();
    	
    	BlankPreferencePage localPrefPageRoot = new BlankPreferencePage(1,
    			"Algorithm Preferences", "Contains preferences that modify how particular algorithms work.");
    	PreferenceNode rootNode = new PreferenceNode("Algorithm Preferences Root", localPrefPageRoot);
    	prefManager.addToRoot(rootNode);
    	
    	addPrefPages(localPrefPages, rootNode);
    }
 
    private void addParamPreferences(PreferenceManager prefManager) {
    	PrefPage[] paramPrefPages = prefAdmin.getParamPrefPages();
    	
    	BlankPreferencePage paramPrefPageRoot = new BlankPreferencePage(1, "Algorithm Parameter Preferences",
    			"Contains preferences that specify the default values for algorithm menus");
    	PreferenceNode rootNode = new PreferenceNode("General Preferences Root", paramPrefPageRoot);
    	prefManager.addToRoot(rootNode);
    	
    	addPrefPages(paramPrefPages, rootNode);
    }
    
    private Shell getParentShell() {
    	IWorkbench workbench = PlatformUI.getWorkbench();
    	IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
   
    	//possibly a better, less seamingly arbitrary way to do this
    	IWorkbenchWindow window = windows[0];
    	Shell parentShell = window.getShell();
    	return parentShell;
    }
    
    private void addPrefPages(PrefPage[] prefPages, PreferenceNode rootNode) {
    	Arrays.sort(prefPages, new PrefPageComparator());
    	for (int ii = 0; ii < prefPages.length; ii++) {
    		PreferenceNode prefNode = makePreferenceNode(prefPages[ii]);
    		rootNode.add(prefNode);
    	}
    }
    
    private PreferenceNode makePreferenceNode(PrefPage prefPage) {
		PreferenceOCD prefOCD = prefPage.getPrefOCD();
		Configuration prefConf = prefPage.getPrefConf();
		
		CIShellPreferenceStore prefStore = new CIShellPreferenceStore(this.log, prefOCD, prefConf);
		CIShellPreferencePage guiPrefPage = new CIShellPreferencePage(this.log,
				prefOCD, prefStore);
		return new PreferenceNode(prefConf.getPid(), guiPrefPage);
    }
    
    private class PreferenceGUIRunnable implements Runnable {

    	private Shell parentShell;
    	private PreferenceManager prefManager;
    	
    	public PreferenceGUIRunnable(Shell parentShell, PreferenceManager prefManager) {
    		this.parentShell = parentShell;
    		this.prefManager = prefManager;
    	}
    	
		public void run() {
			PreferenceDialog prefDialog = new PreferenceDialog(parentShell, prefManager);
			prefDialog.open();
		}
    }
}
