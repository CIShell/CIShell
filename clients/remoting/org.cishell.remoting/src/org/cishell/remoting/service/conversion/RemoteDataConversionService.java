/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 21, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.remoting.service.conversion;

import java.util.Vector;


public interface RemoteDataConversionService {
    public static final String SERVICE_NAME = "RemoteDataConversionService";
    
    public Vector findConverter(Vector inFormats, Vector outFormats);
    
    public String convert(String dataModelID, String outFormat);
    
    public Vector getConversions(String dataModelID, String outFormat);
}
