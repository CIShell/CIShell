/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Mar 21, 2005 at Indiana University.
 */
package edu.iu.iv.core.messaging;

import edu.iu.iv.common.property.PropertyAssignable;
import edu.iu.iv.common.property.PropertyMap;


/**
 * A Message is an object representing communication between two
 * other objects, which contains a PropertyMap with various
 * information pertaining to the message.  The available Property
 * objects are defined in MessageProperty.
 *
 * @author Team IVC
 */
public interface Message extends PropertyAssignable {

    /**
     * Sets the PropertyMap associated with this Message
     *
     * @param propertyMap the PropertyMap for this Message to use
     */
    public void setPropertyMap(PropertyMap propertyMap);
}
