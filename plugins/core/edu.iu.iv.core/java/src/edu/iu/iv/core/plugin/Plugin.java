/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 13, 2004 at Indiana University.
 * modified sprao May 19 2004
 */
package edu.iu.iv.core.plugin;

import edu.iu.iv.common.property.PropertyAssignable;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.datamodels.DataModel;

/**
 * Interface for an IVC plugin.  It contains the plugin's view and info
 * about where and how it is to appear in the menu, as well as providing
 * a list of models it supports and reasons why it doesn't support others.
 *  
 * @author Team IVC
 */
//Created by: Josh Bonner
//Modified by: James Ellis
public interface Plugin extends PropertyAssignable {
	
	/**
	 * Gets the property map for this plugin which specifies additional properties of the
	 * plugin that can be gotten. 
	 * 
	 * @return a plugin property map from PluginProperty->Value
	 */
	public PropertyMap getProperties();
		
	/**
	 * @return a description of this plugin
	 */
	public String getDescription();
	
	/**
	 * Lauches this Plugin, typially consists of loading some associated
	 * GUI and working with the currently selected model(s) in IVC
	 * 
	 * Note: model may actually be a Set of models in some more rare
	 * instances where multiple selection is desired.  It was decided to
	 * stick with this interface and only pass a single model unless multiple
	 * items are selected, at which point the implementor can iterate through
	 * the set and work with the multiple selection as needed.
	 *
	 */
	public void launch(DataModel model);
	
	/**
	 * Determines whether a model is supported by this plugin or not.
	 * 
	 * Note: model may actually be a Set of models in some more rare
	 * instances where multiple selection is desired.  It was decided to
	 * stick with this interface and only pass a single model unless multiple
	 * items are selected, at which point the implementor can iterate through
	 * the set and work with the multiple selection as needed.
	 * 
	 * @param model the model to check for support
	 * 
	 * @return true if the given model is supported, false otherwise
	 */
	public boolean supports( DataModel model );
	
	/**
	 * Retrieves the reason why a particular model is unsupported by
	 * this plugin.
	 * 
	 * Note: model may actually be a Set of models in some more rare
	 * instances where multiple selection is desired.  It was decided to
	 * stick with this interface and only pass a single model unless multiple
	 * items are selected, at which point the implementor can iterate through
	 * the set and work with the multiple selection as needed.
	 * 
	 * @param model the model to look up a reason for
	 * 
	 * @return the reason the given model is unsupported
	 */
	public String unsupportedReason( DataModel model );
}
