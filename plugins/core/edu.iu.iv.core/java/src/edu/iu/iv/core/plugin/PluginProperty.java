/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Jun 7, 2004 at Indiana University.
 */
package edu.iu.iv.core.plugin;

import java.net.URL;

import edu.iu.iv.common.property.Property;
import edu.iu.iv.common.property.URLProperty;

/**
 * A class representing a key to a PluginProperty. These are to be used by a plugin's
 * property map for specifiying new optional meta-data that can be used by IVC.
 * 
 * Public instances are available for standard properties used.
 * 
 * @author Team IVC
 * @version 0.1
 * 
 */
//Created by: Shashikant
public interface PluginProperty {
	/**
	 * Represents the property for a Documentation Link. The value to use in the 
	 * property map is an URL.
	 */
	public static final Property DOCUMENTATION_LINK = 
		new URLProperty("Documentation Link", URL.class, 7) ;
	
	/**
	 * Represents the property for a citation string. The value to use in the property map is a String.
	 */
	public static final Property CITATION_STRING = 
		new Property("Citation String", String.class, 4) ;
	
	/**
	 * Represents the property for the Author of the algorithm the plugin is written around. The value
	 * to use in the property map is a String. 
	 */
	public static final Property AUTHOR = 
		new Property("Author", String.class, 1) ;
}
