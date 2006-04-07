/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 28, 2005 at Indiana University.
 */
package edu.iu.iv.core.algorithm;

import edu.iu.iv.common.property.Property;

/**
 * Defines Property constants that are used in the PropertyMaps
 * of Algorithms.
 *
 * @author Team IVC
 */
public interface AlgorithmProperty {
    
    /** Name of the Algorithm */
    public static final Property LABEL = new Property("Label", String.class, 1);

}
