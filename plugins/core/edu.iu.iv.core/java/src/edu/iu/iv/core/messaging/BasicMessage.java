/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 21, 2005 at Indiana University.
 */
package edu.iu.iv.core.messaging;

import edu.iu.iv.common.property.PropertyMap;

/**
 * Basic implementation of Message interface.
 *
 * @author Team IVC
 */
public class BasicMessage implements Message {
    
    private PropertyMap propertyMap = new PropertyMap();

    /**
     * Returns the PropertyMap associated with this Message
     *
     * @return the PropertyMap associated with this Message
     */
    public PropertyMap getProperties() {
        return propertyMap;
    }

    /**
     * Sets the PropertyMap associated with this Message
     *
     * @param propertyMap the PropertyMap for this Message to use
     */
    public void setPropertyMap(PropertyMap propertyMap) {
        this.propertyMap = propertyMap;
    }

}
