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

import java.util.Iterator;

import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.DataProperty;

import edu.iu.iv.common.property.Property;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.datamodels.DataModelProperty;

public class NewDataModelAdapter extends BasicData implements
        org.cishell.framework.data.DataProperty {

    public NewDataModelAdapter(
            edu.iu.iv.core.datamodels.DataModel dataModel) {
        super(dataModel.getData(), dataModel.getData().getClass().getName());

        PropertyMap properties = dataModel.getProperties();
        Iterator keys = properties.getAllPropertiesSet().iterator();
        while (keys.hasNext()) {
            Property key = (Property) keys.next();
            Object newKey = null;
            Object newValue = properties.getPropertyValue(key);

            if (newValue == null) {
                continue;
            } else if (key.equals(DataModelProperty.LABEL)) {
                newKey = LABEL;
                newValue = newValue.toString();
            } else if (key.equals(DataModelProperty.MODIFIED)) {
                newKey = MODIFIED;
            } else if (key.equals(DataModelProperty.TYPE)) {
                newKey = TYPE;
                String type = ((edu.iu.iv.core.datamodels.DataModelType) newValue)
                        .getName();
                newValue = getDataModelType(type);
            } else if (key.equals(DataModelProperty.PARENT)) {
                newKey = PARENT;
                newValue = new NewDataModelAdapter(
                        (edu.iu.iv.core.datamodels.DataModel) newValue);
            }

            if (newKey != null) {
                this.getMetaData().put(newKey, newValue);
            }
        }
    }

    private String getDataModelType(String type) {
        if (type.equals(DataProperty.MATRIX_TYPE)) {
            return DataProperty.MATRIX_TYPE;
        } else if (type.equals(DataProperty.NETWORK_TYPE)) {
            return DataProperty.NETWORK_TYPE;
        } else if (type.equals(DataProperty.TREE_TYPE)) {
            return DataProperty.TREE_TYPE;
        } else {
            return DataProperty.OTHER_TYPE;
        }
    }
}
