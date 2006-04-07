/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 21, 2005 at Indiana University.
 */
package edu.iu.iv.core.datamodels;

import java.util.Iterator;

/**
 * Provides for a composition if DataModels into a single type that is
 * also an implementation of the DataModel interface.  This is used when
 * there is multiple-selection of DataModels in the IVC GUI and the group
 * needs to be passed along to some Plugin rather than just one individual
 * DataModel.
 *
 * @author Team IVC
 */
public interface CompositeDataModel extends DataModel {

    /**
     * Adds the given DataModel to this CompositeDataModel
     * 
     * @param model the DataModel to add
     */
    public void add(DataModel model);
    
    /**
     * Removes the given DataModel from this CompositeDataModel
     * 
     * @param model the model to remove from this CompositeDataModel
     */
    public void remove(DataModel model);
    
    /**
     * Returns an Iterator over the elements (DataModels) in this
     * CompositeDataModel.
     * 
     * @return an Iterator over the elements (DataModels) in this
     * CompositeDataModel
     */
    public Iterator iterator();
    
}
