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

/**
 * A basic implementation of {@link DataModel}. This class was included since 
 * a simple implementation of DataModel will be used quite often in both client
 * and algorithm code.
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class BasicDataModel implements DataModel {
    private Dictionary properties;
    private Object data;
    private String format;
    
    /**
     * Creates a DataModel with the given data and an empty meta-data
     * {@link Dictionary}
     * 
     * @param data The data being wrapped
     */
    public BasicDataModel(Object data, String format) {
        this(new Hashtable(), data, format);
    }
    
    /**
     * Creates a DataModel with the given data and meta-data {@link Dictionary}
     * 
     * @param properties The meta-data about the data
     * @param data       The data being wrapped
     */
    public BasicDataModel(Dictionary properties, Object data, String format) {
        this.properties = properties;
        this.data = data;
        this.format = format;
    }
    
    /**
     * @see org.cishell.framework.datamodel.DataModel#getData()
     */
    public Object getData() {
        return data;
    }

    /**
     * @see org.cishell.framework.datamodel.DataModel#getMetaData()
     */
    public Dictionary getMetaData() {
        return properties;
    }

    public String getFormat() {
        return format;
    }
}
