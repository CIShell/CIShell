/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 3, 2004 at Indiana University.
 */
 
package edu.iu.iv.common.property;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A class that holds mappings from Property to whatever
 * class of object is suitable for the PluginProperty.
 * 
 * @author Team IVC
 */
//Created by: Bruce Herr
public class PropertyMap implements Serializable {
    private static final long serialVersionUID = 5260017098294267006L;
    private Map propertyMap;

	public PropertyMap() {
		propertyMap = new HashMap();
	}
	
	public void put(Property property, Object value) {
		setPropertyValue(property, value);
	}
	
	/**
	 * Sets an associated value for the property.
	 * 
	 * @param property the property to set
	 * @param value its associated value
	 */
	public void setPropertyValue(Property property, Object value) {
		if (property.isPropertyValueAcceptable(value)) {
			propertyMap.put(property, value);
		} else {
		    String error;
		    if(value == null)
		        error = "Property value is null";
		    else
		        error = "Illegal property value type: " + value.getClass().getName() +
				 ". Expected type:" + property.getAcceptableClass().getName();
		    
			throw new IllegalArgumentException(error);
		}
	}
	
	/**
	 * Get the value associated with the given property.
	 * 
	 * @param property the property to get the value of
	 * @return the associated value or null if the property is not set.
	 */
	public Object getPropertyValue(Property property) {
		return propertyMap.get(property);
	}
	
	/**
	 * Get all the PluginProperties that have been set in this map.
	 * 
	 * @return a Set of all the properties that have been set
	 */
	public Set getAllPropertiesSet() {
		return Collections.unmodifiableSet(propertyMap.keySet());
	}
}
