/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 22, 2005 at Indiana University.
 */
package edu.iu.iv.core.messaging;

import junit.framework.TestCase;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.messaging.BasicMessage;
import edu.iu.iv.core.messaging.MessageProperty;

/**
 *
 * @author Team IVC
 */
public class BasicMessageTester extends TestCase {

    private BasicMessage message;
    
    protected void setUp() {
        message = new BasicMessage();
    }

    public void testGetAndSetPropertyMap() {
        PropertyMap map = message.getProperties();
        assertNotNull(map);
        assertNull(map.getPropertyValue(MessageProperty.TITLE));
        assertNull(map.getPropertyValue(MessageProperty.MESSAGE));
        assertNull(map.getPropertyValue(MessageProperty.DETAILS));
        
        String title = "title";
        String messageString = "message";
        String details = "details";
        
        map = new PropertyMap();
        map.setPropertyValue(MessageProperty.TITLE, title);
        map.setPropertyValue(MessageProperty.MESSAGE, messageString);
        map.setPropertyValue(MessageProperty.DETAILS, details);
        
        message.setPropertyMap(map);
        assertEquals(map.getPropertyValue(MessageProperty.TITLE), title);
        assertEquals(map.getPropertyValue(MessageProperty.MESSAGE), messageString);
        assertEquals(map.getPropertyValue(MessageProperty.DETAILS), details);        
        
    }

}
