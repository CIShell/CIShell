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
package org.cishell.reference.remoting.client.service.conversion;

import java.util.Vector;

import org.cishell.reference.remoting.RemotingClient;
import org.cishell.remoting.service.conversion.RemoteDataConversionService;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class RemoteDataConversionServiceClient extends RemotingClient implements
        RemoteDataConversionService {

    public RemoteDataConversionServiceClient() {
        super("/soap/services/RemoteDataConversionService");
    }

    /**
     * @see org.cishell.remoting.service.conversion.RemoteDataConversionService#convert(java.lang.String, java.lang.String)
     */
    public String convert(String dataModelID, String outFormat) {
        return (String) doCall("convert", new Object[]{dataModelID,outFormat});
    }

    /**
     * @see org.cishell.remoting.service.conversion.RemoteDataConversionService#findConverter(java.util.Vector, java.util.Vector)
     */
    public Vector findConverter(Vector inFormats, Vector outFormats) {
        return (Vector) doCall("findConverter", new Object[]{inFormats,outFormats});
    }

    public Vector getConversions(String dataModelID, String outFormat) {
        return (Vector) doCall("getConversions", new Object[]{dataModelID,outFormat});
    }
}
