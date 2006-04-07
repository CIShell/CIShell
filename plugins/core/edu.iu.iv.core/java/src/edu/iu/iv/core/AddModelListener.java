/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 13, 2004 at Indiana University.
 */
package edu.iu.iv.core;

import edu.iu.iv.core.datamodels.DataModel;

/**
 * Interface for objects that need to be notified when a model is
 * to be added. AddModelListeners are usually added to the IVC singleton
 * by IVC.getInstance().addAddModelListener(listener);
 * 
 * @author Team IVC
 */
//Created by: Josh Bonner
public interface AddModelListener {
	
	/**
	 * Adds a model to this listener.
	 * 
	 * @param model the model to add
	 */
	public void addModel( DataModel model );
}
