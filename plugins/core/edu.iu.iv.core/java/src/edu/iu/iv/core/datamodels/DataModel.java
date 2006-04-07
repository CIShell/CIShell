/*
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 *
 * Created on Feb 19, 2005 at Indiana University.
 */
package edu.iu.iv.core.datamodels;

import edu.iu.iv.common.property.PropertyAssignable;
import edu.iu.iv.common.property.PropertyMap;


/**
 * Defines the interface for the data model wrapper object.  DataModels can
 * have any arbitrary Object as their data, but they also provide a PropertyMap
 * containing various other information. See DataModelProperty for properties.
 *
 * @author Team IVC
 */
public interface DataModel extends PropertyAssignable {
    /**
     * Returns the data associated with this DataModel.  This is the
     * action data model Object that is wrapped with this DataModel.
     *
     * @return Returns the data associated with this DataModel
     */
    public Object getData();

    /**
     * Sets the data for this DataModel to be the given Object.
     *
     * @param data the data for this DataModel to use
     */
    public void setData(Object data);

    /**
     * Sets the PropertyMap associated with this DataModel.
     *
     * @param propertyMap the PropertyMap for this DataModel to use
     */
    public void setPropertyMap(PropertyMap propertyMap);
}
