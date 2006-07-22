/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jul 3, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.remoting.service.framework;

import java.util.Hashtable;
import java.util.Vector;

import org.cishell.framework.datamodel.DataModel;

public interface DataModelRegistry {
    public static String SERVICE_NAME = "DataModelRegistry";
    public static String SERVICE_METHODS = "*";
    
    public Hashtable getProperties(String dataModelID);
    
    public byte[] getData(String dataModelID, String format);
    public Vector getDataFormats(String dataModelID);

    public String createDataModel(Hashtable properties, String format, byte[] data);
    public void unregisterDataModel(String dataModelID);
    public String registerDataModel(DataModel dataModel);
    public Vector registerDataModels(DataModel[] dataModel);
    
    public DataModel getDataModel(String dataModelID);
    public DataModel[] getDataModels(Vector dataModelIDs);
}
