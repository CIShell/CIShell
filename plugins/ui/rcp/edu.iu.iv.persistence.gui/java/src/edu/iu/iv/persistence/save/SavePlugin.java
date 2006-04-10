/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 7, 2005 at Indiana University.
 */
package edu.iu.iv.persistence.save;

import java.util.List;

import edu.iu.iv.core.IVC;
import edu.iu.iv.core.datamodels.DataModel;
import edu.iu.iv.core.persistence.Persister;
import edu.iu.iv.core.plugin.AbstractPlugin;

/**
 *
 * @author Team IVC (James Ellis)
 */
public class SavePlugin extends AbstractPlugin {

    private static SavePlugin defaultPlugin;
    
    private String DESCRIPTION = "Save a model";
    private String NULL_MODEL_UNSUPPORTED_REASON = "Nothing to save";
    private String NO_PERSISTER_UNSUPPORTED_REASON = "Unable to save this model";    
    
    private boolean lastSaveSuccessful;
    
    public SavePlugin(){
        defaultPlugin = this;
    }
    
    public static SavePlugin getDefault(){
        return defaultPlugin;
    }
    
    //special addition so context menu invocation causes info to be printed as well
    public void printInformation(){
        printPluginInformation();
    }
    
    public String getDescription() {
        return DESCRIPTION;
    }
    
    /**
     * Determines whether the last save attempted was successful
     * 
     * @return true if the last save was successful, false if not
     */
    public boolean getSuccess(){
        return lastSaveSuccessful;
    }

    /**
     * Runs this SaveAction.  This method handles the job of ensuring the
     * correct Persisters are registered, finding the supported Persister(s)
     * for the selected model, and either saving the model with the Persister
     * found, or allowing the user to select from a list of possible Persisters
     * if more than one is found.
     *
     * @param models the currently selected Set of models in IVC
     */
    public void launch(DataModel model) {
        lastSaveSuccessful = false;

        // get a list of persisters which can persist this model        
        List persisterList = IVC.getInstance().getPersistenceRegistry().getSupportingPersisters(model.getData());

        // if the list is empty, log the occurence.
        if (persisterList.isEmpty()) {
            String message = "No supporting persisters found!";
            IVC.showError("Error!", message, "");
            IVC.getInstance().getErrorLogger().error(message);
            return;
        }
        
        // else check to see if there is only one persister available.
        // if yes, then just go ahead and save the model.
        else if (persisterList.size() == 1) {
            final Persister persister = (Persister) persisterList.iterator().next();
            final FileSaver saver = new FileSaver(window.getShell());            
            lastSaveSuccessful = saver.save(persister, model);
            return;
        }
        
        // else there is more than one persister that can persist this file
        // show a persister chooser and let user select from
        // available persisters.
        else {
            Object[] tmpPersisters = persisterList.toArray();
            Persister[] persisters = new Persister[tmpPersisters.length];
            for(int i = 0; i < persisters.length; i++)
                persisters[i] = (Persister)tmpPersisters[i];
            lastSaveSuccessful = new SavePersisterChooser("Select Format", model, window.getShell(), persisters).open();
        }
    }

    public boolean supports(DataModel model) {
        return model.getData() != null && 
        	!(IVC.getInstance().getPersistenceRegistry().getSupportingPersisters(model.getData()).isEmpty());
    }

    public String unsupportedReason(DataModel model) {
        if (model.getData() == null)
            return NULL_MODEL_UNSUPPORTED_REASON;
        else
            return NO_PERSISTER_UNSUPPORTED_REASON;
    }

}
