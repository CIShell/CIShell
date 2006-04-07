/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Feb 21, 2005 at Indiana University.
 */
package edu.iu.iv.core.datamodels;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.iu.iv.common.property.PropertyMap;

/**
 *
 * @author Team IVC
 */
public class BasicCompositeDataModel implements CompositeDataModel {
    
    private Set models = new HashSet();
    
    /**
     * Adds the given DataModel to this CompositeDataModel
     * 
     * @param model the DataModel to add
     */
    public void add(DataModel model) {
        models.add(model);
    }

    /**
     * Removes the given DataModel from this CompositeDataModel
     * 
     * @param model the model to remove from this CompositeDataModel
     */
    public void remove(DataModel model) {
        models.remove(model);
    }

    /**
     * Returns an Iterator over the elements (DataModels) in this
     * CompositeDataModel.
     * 
     * @return an Iterator over the elements (DataModels) in this
     * CompositeDataModel
     */
    public Iterator iterator() {
        return models.iterator();
    }

    /**
     * Returns null in this implementation of BasicCompositeDataModel.
     * The composite has no data itself, it just contains other DataModels
     * with Data.
     */
    public Object getData() {
        return null;
    }

    /**
     * Does nothing, required for implementation of the DataModel interface.
     */
    public void setData(Object data) {
        //does nothing
    }

    /**
     * Returns null in this implementation of BasicCompositeDataModel.
     * The composite has no PropertyMap itself, it just contains other DataModels
     * with PropertyMaps.
     */
    public PropertyMap getProperties() {
        return null;
    }

    /**
     * Does nothing, required for implementation of the DataModel interface.
     */
    public void setPropertyMap(PropertyMap propertyMap) {
        //does nothing
    }

}
