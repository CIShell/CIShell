/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 15, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.framework.datamodel;

import java.util.Dictionary;
import java.util.Hashtable;


public class BasicDataModel implements DataModel {
    private Dictionary properties;
    private Object data;
    
    public BasicDataModel(Object data) {
        this(new Hashtable(), data);
    }
    
    public BasicDataModel(Dictionary properties, Object data) {
        this.properties = properties;
        this.data = data;
    }
    
    /**
     * @see org.cishell.framework.datamodel.DataModel#getData()
     */
    public Object getData() {
        return data;
    }

    /**
     * @see org.cishell.framework.datamodel.DataModel#getProperties()
     */
    public Dictionary getProperties() {
        return properties;
    }
}
