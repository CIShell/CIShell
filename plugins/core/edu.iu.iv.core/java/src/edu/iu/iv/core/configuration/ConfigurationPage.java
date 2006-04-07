/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 20, 2005 at Indiana University.
 */
package edu.iu.iv.core.configuration;

import edu.iu.iv.common.parameter.Parameterizable;
import edu.iu.iv.common.property.PropertyAssignable;

/**
 * The interface for creating a Configuration Page for getting
 * and setting preferences in the IVC framework. Please
 * extend AbstractConfigurationPage if you wish to use this feature.
 * 
 * Note: Validation of the values are done through the validators on the
 * Parameters that are set in the ParameterMap
 * 
 * @author Bruce Herr
 */
public interface ConfigurationPage extends Parameterizable, PropertyAssignable {
    
    /**
     * Sets the values in the gui to their default values
     * 
     */
    public void performDefaults();
    
    /**
     * reacts to the user pressing Ok to save and close the 
     * Configuration page.
     * 
     * @return whether or not to allow the Ok to happen
     */
    public boolean save();
}
