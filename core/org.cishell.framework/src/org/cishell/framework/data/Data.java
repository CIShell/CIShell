/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 7, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - Initial API
 * ***************************************************************************/
package org.cishell.framework.data;

import java.io.File;
import java.util.Dictionary;

/**
 * A class that contains data, its format, and its metadata. This class is used 
 * to pass data between algorithms and is what algorithms optionally create when 
 * executed.
 *
 */
public interface Data {
	/**
	 * Returns the metadata associated with the data stored in this Data object.
	 * Standard keys and values are in the {@link DataProperty} interface.
	 * 
	 * @return The data's metadata
	 */
    public Dictionary<String, Object> getMetadata();
    
    /**
     * Returns the data stored in this Data object
     * 
     * @return The data (a Java object)
     */
    public Object getData();
    
    
    /**
     * Returns the format of the encapsulated data. If the data is a {@link File}, 
     * then this method returns what MIME type it is with "file:" prepended 
     * (eg. file:text/plain). Otherwise, the string returned should be the Java 
     * class it represents. For algorithms this format should be the same as 
     * their OUT_DATA property. 
     * 
     * @return The main format of the data
     */
    public String getFormat();
}
