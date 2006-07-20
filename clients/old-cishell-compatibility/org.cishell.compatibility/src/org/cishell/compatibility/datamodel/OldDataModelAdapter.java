/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Jun 19, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.compatibility.datamodel;

import java.util.Dictionary;
import java.util.Enumeration;

import org.cishell.framework.datamodel.DataModelProperty;

import edu.iu.iv.common.property.Property;
import edu.iu.iv.core.datamodels.BasicDataModel;
import edu.iu.iv.core.datamodels.DataModelType;


public class OldDataModelAdapter extends BasicDataModel 
                    implements edu.iu.iv.core.datamodels.DataModelProperty {

    public OldDataModelAdapter(org.cishell.framework.datamodel.DataModel dataModel) {
        super(dataModel.getData());
    
        Dictionary properties = dataModel.getMetaData();
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Property newKey = null;
            Object newValue = properties.get(key);
            
            if (newValue == null) {
                continue;
            } else if (key.equals(DataModelProperty.LABEL)) {
                newKey = LABEL;
                newValue = newValue.toString();
            } else if (key.equals(DataModelProperty.MODIFIED)) {
                newKey = MODIFIED;
            } else if (key.equals(DataModelProperty.TYPE)) {
                newKey = TYPE;
                String type = (String) newValue;
                newValue = getDataModelType(type);
            } else if (key.equals(DataModelProperty.PARENT)) {
                newKey = PARENT;
                newValue = new OldDataModelAdapter((org.cishell.framework.datamodel.DataModel) newValue);
            }
            
            if (newKey != null) {
                this.getProperties().put(newKey, newValue);
            }
        }
    }

    private DataModelType getDataModelType(String type) {
        if (type.equals(DataModelType.MATRIX)) {
            return DataModelType.MATRIX;
        } else if (type.equals(DataModelType.NETWORK)) {
            return DataModelType.NETWORK;
        } else if (type.equals(DataModelType.TREE)) {
            return DataModelType.TREE;
        } else {
            return DataModelType.OTHER;
        }
    }
}
