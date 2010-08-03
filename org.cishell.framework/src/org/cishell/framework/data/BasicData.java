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
package org.cishell.framework.data;

import java.util.Dictionary;
import java.util.Hashtable;

/**
 * A basic implementation of {@link Data}. This class was included since 
 * a simple implementation of Data will be used quite often in both application
 * and algorithm code.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class BasicData implements Data {
    private Dictionary<String, Object> properties;
    private Object data;
    private String format;
    
    /**
     * Creates a Data object with the given data and an empty metadata
     * {@link Dictionary}
     * 
     * @param data The data being wrapped
     */
    public BasicData(Object data, String format) {
        this(new Hashtable<String, Object>(), data, format);
    }
    
    /**
     * Creates a Data object with the given data and metadata {@link Dictionary}
     * 
     * @param properties The metadata about the data
     * @param data       The data being wrapped
     */
    public BasicData(Dictionary<String, Object> properties, Object data, String format) {
        this.properties = properties;
        this.data = data;
        this.format = format;
    }
    
    /**
     * @see org.cishell.framework.data.Data#getData()
     */
    public Object getData() {
        return data;
    }

    /**
     * @see org.cishell.framework.data.Data#getMetadata()
     */
    public Dictionary<String, Object> getMetadata() {
        return properties;
    }

    /**
     * @see org.cishell.framework.data.Data#getFormat()
     */
    public String getFormat() {
        return format;
    }
}
