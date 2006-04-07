/*
 * Created on May 12, 2005
 *
 */
package edu.iu.iv.common.property;

import edu.iu.iv.common.property.PropertyMap;

/**
 * Classes implementing this interface are required to provide a way by which
 * they can be assigned properties.
 * 
 * @author sprao Project: tv
 */
public interface PropertyAssignable {
	
    /**
     * @return the Properties for this class.
     */
	public PropertyMap getProperties() ;
	
}
