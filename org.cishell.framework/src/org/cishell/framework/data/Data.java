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
 * A class that encapsulates data and its meta-data. This class is used to pass
 * data between algorithms and is what algorithms optionally create when executed. 
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public interface Data {
	/**
	 * Returns the meta-data associated with the data stored in this Data object.
	 * Some standard keys are in the {@link DataProperty} interface.
	 * 
	 * @return The data's meta-data
	 */
    public Dictionary getMetaData();
    
    /**
     * Returns the data stored in this Data object.
     * 
     * @return The data (a java object)
     */
    public Object getData();
    
    
    /**
     * Get the format of the encapsulated data. If the data is a {@link File}, 
     * then this method returns what MIME type it is with "file:" prepended 
     * (eg. file:text/plain). Otherwise, the string returned should be the java 
     * class it represents. For algorithms this format should be the same as 
     * their OUT_DATA property. 
     * 
     * @return The main format of the data
     */
    public String getFormat();
    
    //TODO: Consider these methods for inclusion in Data    
    //public String getUID(); //may be better for specifying parent datamodels
    //public Object getDataAsFormat(String format);
}
