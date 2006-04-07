/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package edu.iu.iv.core.datamodels;

import edu.iu.iv.common.property.PropertyMap;


/**
 * Basic implementation of the DataModel interface.
 *
 * @author Team IVC
 */
public class BasicDataModel implements DataModel {
    private Object data;
    private PropertyMap propertyMap;

    /**
     * Creates a new BasicDataModel object.
     *
     * @param data the data this BasicDataModel is based on
     */
    public BasicDataModel(Object data) {
        this.data = data;
        propertyMap = new PropertyMap();
    }

    /**
     * Returns the data associated with this DataModel.  This is the
     * action data model Object that is wrapped with this DataModel.
     *
     * @return Returns the data associated with this DataModel
     */
    public Object getData() {
        return data;
    }

    /**
     * Sets the data for this DataModel to be the given Object.
     *
     * @param data the data for this DataModel to use
     */
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * Returns the PropertyMap associated with this DataModel.
     *
     * @return Returns the PropertyMap associated with this DataModel
     */
    public PropertyMap getProperties() {
        return propertyMap;
    }

    /**
     * Sets the PropertyMap associated with this DataModel.
     *
     * @param propertyMap the PropertyMap for this DataModel to use
     */
    public void setPropertyMap(PropertyMap propertyMap) {
        this.propertyMap = propertyMap;
    }
}
