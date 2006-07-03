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

import org.cishell.framework.datamodel.BasicDataModel;
import org.cishell.framework.datamodel.DataModelType;

import edu.iu.iv.common.property.Property;
import edu.iu.iv.common.property.PropertyMap;
import edu.iu.iv.core.datamodels.DataModelProperty;

public class NewDataModelAdapter extends BasicDataModel implements
        org.cishell.framework.datamodel.DataModelProperty {

    public NewDataModelAdapter(
            edu.iu.iv.core.datamodels.DataModel dataModel) {
        super(dataModel.getData());

        PropertyMap properties = dataModel.getProperties();
        Iterator keys = properties.getAllPropertiesSet().iterator();
        while (keys.hasNext()) {
            Property key = (Property) keys.next();
            Object newKey = null;
            Object newValue = properties.getPropertyValue(key);

            if (newValue == null) {
                continue;
            } else if (key == DataModelProperty.LABEL) {
                newKey = LABEL;
                newValue = newValue.toString();
            } else if (key == DataModelProperty.MODIFIED) {
                newKey = MODIFIED;
            } else if (key == DataModelProperty.TYPE) {
                newKey = TYPE;
                String type = ((edu.iu.iv.core.datamodels.DataModelType) newValue)
                        .getName();
                newValue = getDataModelType(type);
            } else if (key == DataModelProperty.PARENT) {
                newKey = PARENT;
                newValue = new NewDataModelAdapter(
                        (edu.iu.iv.core.datamodels.DataModel) newValue);
            }

            if (newKey != null) {
                this.getProperties().put(newKey, newValue);
            }
        }
    }

    private String getDataModelType(String type) {
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
