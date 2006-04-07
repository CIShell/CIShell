/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 20, 2005 at Indiana University.
 */
package edu.iu.iv.core.configuration;

import edu.iu.iv.common.property.Property;

/**
 * 
 * @author Bruce Herr
 */
public interface ConfigurationPageProperty {
    /** The title of this configuration page **/
    public static final Property TITLE = new Property("Title",String.class);
    
    /**
     * Set this property to show an Error Message or set it to null
     * to get rid of it when there is no longer an error 
     **/
    public static final Property ERROR_MESSAGE = new Property("Error",String.class);   
}
