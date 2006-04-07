/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Jan 7, 2005 at Indiana University.
 */
package edu.iu.iv.core;

import edu.iu.iv.core.datamodels.DataModel;

import java.util.Set;


/**
 * Class to manage the DataModels that are loaded into IVC.
 *
 * @author Team IVC
 */
//Created by: Bruce Herr
public interface ModelManager {
    /**
     * Adds the given DataModel to IVC.
     *
     * @param model the DataModel to add to IVC
     */
    public void addModel(DataModel model);

    /**
     * Removes the given DataModel from IVC.
     *
     * @param model the DataModel to remove from IVC
     */
    public void removeModel(DataModel model);

    /**
     * Returns the Set of selected DataModels in IVC.  These are
     * the models that the user has currently selected in the DataModels GUI.
     *
     * @return the Set of selected DataModels in IVC.
     */
    public Set getSelectedModels();

    /**
     * Sets the Set of selected DataModels in IVC.  These are
     * the models that the user has currently selected in the DataModels GUI.
     *
     * @param models the new Set of selected DataModels
     */
    public void setSelectedModels(Set models);
    
    /**
     * Returns all the models held in this ModelManager
     * 
     * @return all the models held in this ModelManager
     */
    public Set getModels();
}
