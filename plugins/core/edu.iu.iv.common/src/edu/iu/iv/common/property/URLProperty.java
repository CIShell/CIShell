/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 3, 2004 at Indiana University.
 */
package edu.iu.iv.common.property;

import java.net.URL;

/**
 * 
 * @author Team IVC 
 */
//Created by: Bruce Herr
public class URLProperty extends Property {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
	 * @param name
	 */
	public URLProperty(String name) {
		super(name);
	}

	/**
	 * @param name
	 * @param acceptableClass
	 */
	public URLProperty(String name, Class acceptableClass) {
		super(name, acceptableClass);
	}

	/**
	 * @param name
	 * @param acceptableClass
	 * @param priority
	 */
	public URLProperty(String name, Class acceptableClass, int priority) {
		super(name, acceptableClass, priority);
	}
	
	/**
	 * Overrides standard toString(value) from property to deal with URLs
	 * 
	 * @see edu.iu.iv.common.property.Property#toString(java.lang.Object)
	 */
	public String toString(Object value) {
		if (value instanceof URL && value != null) {
			return ((URL) value).toExternalForm();
		} else {
			return super.toString(value);
		}
	}
}
