/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 21, 2005 at Indiana University.
 */
package edu.iu.iv.core.messaging;

import edu.iu.iv.common.property.Property;

/**
 * Defines common Properties of Messages.
 *
 * @author Team IVC
 */
public interface MessageProperty {
    
    /** The body text of this Message */
    public static final Property MESSAGE = new Property("Message", String.class, 1);
    
    /** The title of this Message */
    public static final Property TITLE = new Property("Title", String.class, 2);
    
    /** Any extra details about this message that may be useful */
    public static final Property DETAILS = new Property("Details", String.class, 3);
    
}

