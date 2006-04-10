/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Jan 28, 2005 at Indiana University.
 */
package edu.iu.iv.persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.ModelManager;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.datamodels.DataModelProperty;
import edu.iu.iv.datamodelview.DataModelTreeView;
import edu.iu.iv.gui.IVCCloseAction;
import edu.iu.iv.gui.IVCWorkbenchAdvisor;
import edu.iu.iv.persistence.save.SaveDataModelDialog;
import edu.iu.iv.persistence.save.SavePlugin;


/**
 * PersistencePlugin defines the class to represent the Persistence
 * Plugin for the Eclipse framework. This is the class that is invoked when the
 * plugin is loaded, and thus can be used to things like initialization
 * as well as setup the Properties associated with this Plugin.  The Persistence
 * Plugin does the job of scanning the specified Persister directory and
 * registering Persisters with IVC
 * 
 * @author Team IVC
 * 
 */
public class PersistencePlugin extends AbstractUIPlugin implements IStartup {
    
    public static final String ID_PLUGIN = "edu.iu.iv.persistence";
    
	//The shared instance.
	private static PersistencePlugin plugin;
	
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * Creates a new PersistencePlugin
	 */
	public PersistencePlugin() {
		super();
		if(plugin == null)
		    plugin = this;	
		try {
			resourceBundle = ResourceBundle.getBundle("edu.iu.iv.persistence.PersistencePlugin");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation.  It handles the job
	 * of requesting the scanning of the Persister directory and 
	 * registering all found Persisters with IVC
	 * 
	 * @param context The BundleContext for this Plugin
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);

	}

	/**
	 * This method is called when the plug-in is stopped. Currently it
	 * simply clears out the registry of Persisters if the PersistencePlugin
	 * is stopped.
	 * 
	 * @param context The BundleContext for this Plugin
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		IVC.getInstance().getPersistenceRegistry().clear();
	}

	/**
	 * Returns the shared instance of PersistencePlugin (Singleton)
	 * 
	 * @return the shared instance of PersistencePlugin (Singleton)
	 */
	public static PersistencePlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 * 
	 * @param key The key of the item to retrieve from the ResourceBundle
	 * @return the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = PersistencePlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle.
	 * 
	 * @return the Plugin's ResourceBundle
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
	
    public void earlyStartup() {
		//this handles some items that the Persistence plugin contributes to the main
        //gui.  such as: adding functionality to the 'save' item in the context menu of
        //data models view, adding functionality to save unsaved models (or at least prompt and
        //ask) when the user tries to discard an unsaved model.  Add functionality to 'exit'
        //item in the main menu to ask if the user wants to save unsaved datamodels.
		getWorkbench().getDisplay().asyncExec(new Runnable(){
		   public void run(){
		       setupSaveMenuItem();
		       setupDiscardMenuItem();
		       setupExitConfirmation();
		   }
		});
    }
    
    /*
     * contribute functionality to the context menu in datamodels view that allows
     * the user to save a model with a right click on it
     */
    private void setupSaveMenuItem(){
       	DataModelTreeView datamodels = DataModelTreeView.getDefault();
       	if(datamodels == null)
           return;
		Menu menu = datamodels.getContextMenu();
		MenuItem actionItem = menu.getItem(0); //save is the first item
		//give it a new listener to plug into the persistence plugin
        actionItem.addListener(SWT.Selection, new Listener(){
            public void handleEvent(Event event) {
               Set models = IVC.getInstance().getModelManager().getSelectedModels();
               Iterator iterator = models.iterator();
               while(iterator.hasNext()){
                   DataModel model = (DataModel)iterator.next();
                   SavePlugin.getDefault().printInformation();
                   SavePlugin.getDefault().launch(model);	                   
               }

            }
        });
        actionItem.setEnabled(true);        
    }
    
    /*
     * contribute functionality to the discard menut item in the datamodels context 
     * menu so the user is prompted before they discard an unsaved model
     */
    private void setupDiscardMenuItem(){
       	DataModelTreeView datamodels = DataModelTreeView.getDefault();
       	if(datamodels == null)
           return;
		Menu menu = datamodels.getContextMenu();
		MenuItem actionItem = menu.getItem(2); //discard is the third item
		//give it a new listener to plug into the persistence plugin
		Listener listener = datamodels.getDiscardListener();
		actionItem.removeListener(SWT.Selection, listener);
		actionItem.addListener(SWT.Selection, new DiscardListener());
    }
    
    /*
     * contribute prompt about unsaved models before the user exits and give the choice
     * to save them
     */
    private void setupExitConfirmation(){
        IVCWorkbenchAdvisor.getDefault().addCloseAction(new IVCCloseAction(){
            public boolean run() {
                return handleUnsaved();                   
            }            
        });
    }
    
    /*
     * handles unsaved models in IVC as the application is trying to close, prompts the user
     * if they want to save them or not before they are lost
     */
    private boolean handleUnsaved(){
        //find all unsaved datamodels and alert the user
        Set models = IVC.getInstance().getModelManager().getModels();
        List unsavedModels = new ArrayList();
        Iterator modelIterator = models.iterator();
        while(modelIterator.hasNext()){
            DataModel model = (DataModel)modelIterator.next();
            Boolean modified = (Boolean)model.getProperties().getPropertyValue(DataModelProperty.MODIFIED);
            if(modified != null && modified.booleanValue()){
                unsavedModels.add(model);
            }                
        }
        
        if(!unsavedModels.isEmpty()){
            SaveDataModelDialog dialog = new SaveDataModelDialog(DataModelTreeView.getDefault().getSite().getShell(), unsavedModels);
            return dialog.open();            
        }
        else{
            return true; //no unsaved models, go ahead and exit
        }
    }
    
//    private boolean confirmExit(){
//	    final Configuration cfg = IVC.getInstance().getConfiguration();
//	    boolean confirm = cfg.getBoolean(IVCPreferences.EXIT_WITHOUT_PROMPT);
//	    if(!confirm)
//	        return true;
//	    
//        IVCDialog dialog = new IVCDialog(IVCApplication.getShell(), "Exit?", IVCDialog.QUESTION){
//            public void createDialogButtons(Composite parent) {
//                Button confirm = new Button(parent, SWT.PUSH);
//                confirm.setText("Yes");
//                confirm.addSelectionListener(new SelectionAdapter(){
//                    public void widgetSelected(SelectionEvent e) {
//                        close(true);
//                    }
//                });
//                Button deny = new Button(parent, SWT.PUSH);
//                deny.setText("No");
//                deny.addSelectionListener(new SelectionAdapter(){
//                    public void widgetSelected(SelectionEvent e) {
//                        close(false);
//                    }
//                });
//            }
//
//            public Composite createContent(Composite parent) {
//                Composite content = new Composite(parent, SWT.NONE);
//                content.setLayout(new RowLayout());
//                final Button checkbox = new Button(content, SWT.CHECK);
//                checkbox.setText("Always prompt for confirmation before exiting");
//                checkbox.setSelection(true);
//                checkbox.addSelectionListener(new SelectionAdapter(){
//                   public void widgetSelected(SelectionEvent e){
//                       cfg.setValue(IVCPreferences.EXIT_WITHOUT_PROMPT, checkbox.getSelection());
//                   }                    
//                });
//                return content;
//            }            
//        };
//        dialog.setDescription("Are you sure you want to exit?");
//        dialog.setDetails("By deselecting the checkbox above, this confirmation" +
//        		" dialog will be avoided in the future if you do not wish to be" +
//        		" asked for confirmation of exit.  This preference is toggleable" +
//        		" in the preferences window under the \"General IVC\" section.");
//        return dialog.open();
//    }
    
    private class DiscardListener implements Listener {
        public void handleEvent(Event event) {
            ModelManager manager = IVC.getInstance().getModelManager();
            Object[] selection = manager.getSelectedModels().toArray();
            
            //find unsaved items
            List unsaved = new ArrayList();
            List saved = new ArrayList();
            for(int i = 0; i < selection.length; i++){                
                DataModel model = (DataModel)selection[i];
                Boolean modified = (Boolean)model.getProperties().getPropertyValue(DataModelProperty.MODIFIED);
                if(modified != null && modified.booleanValue()){
                    unsaved.add(model);
                }
                else {
                    saved.add(model);
                }
            }
            
            //remove all saved items that are selected
            Iterator savedIterator = saved.iterator();
            while(savedIterator.hasNext()){
                DataModel currentModel = (DataModel)savedIterator.next();
                DataModelTreeView.getDefault().remove(currentModel);
                IVC.getInstance().getModelManager().removeModel(currentModel);
            }
            
            //prompt user about unsaved ones
            if(unsaved.size() > 0){
                handleUnsavedModels(unsaved);
            }            

            //set the selection to whatever was left
            Set newSelection = new HashSet();
            Set allModels = manager.getModels();
            for(int i = 0; i < selection.length; i++){
                if(allModels.contains(selection[i])){
                    //its still around so the user chose not to discard it
                    newSelection.add(selection[i]);
                }
            }           
            manager.setSelectedModels(newSelection);
            DataModelTreeView.getDefault().refresh();
        }
        
        private boolean handleUnsavedModels(List models){
            SaveDataModelDialog dialog = new SaveDataModelDialog(DataModelTreeView.getDefault().getSite().getShell(), models);
            boolean success = dialog.open();
            return success;
        }
        
    }
}
