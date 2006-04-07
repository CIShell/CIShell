/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 3, 2004 at Indiana University.
 */
package edu.iu.iv.common.property;

import java.io.Serializable;

/**
 * A Property.
 * 
 * @author Team IVC 
 */
//Created by: Bruce Herr
public class Property implements Comparable, Serializable {
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String name;
	private Class acceptableClass;
	private int priority;

	public Property(String name) {
		this(name, Object.class);
	}
	
	public Property(String name, Class acceptableClass) {
		this(name,acceptableClass,100);
	}
	
	public Property (String name, Class acceptableClass, int priority) {
		this.name = name;
		this.acceptableClass = acceptableClass;
		this.priority = priority;
	}
	
	/**
	 * get the diaplayable name of the property.
	 * 
	 * @return name the property name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * get the class that is to be used to describe this property.
	 * 
	 * The PropertyMap will only accept objects of this type
	 * when setting a plugin property for a plugin.
	 * 
	 * @return the acceptable class for this Property
	 */
	public Class getAcceptableClass() {
		return acceptableClass;
	}
	
	/**
	 * get the priority of this property.
	 * 
	 * this value is used to weight (lower is better) how important 
	 * the property is and if properties are to be listed, what order 
	 * to go in.
	 * 
	 * 
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}
	
	/**
	 * Returns whether the given object is acceptable as a
	 * property value for this property.
	 * 
	 * @param value the proposed value for this property
	 * @return if the value is acceptable
	 */
	public boolean isPropertyValueAcceptable(Object value) {
		return acceptableClass.isInstance(value);
	}
	
	/**
	 * Takes a value for this property and returns its string representation.
	 * 
	 * Subclasses can override this if it needs to do more than just 
	 * value.toString() (eg. Lists, Maps, custom data...)
	 * 
	 * @param value the property value
	 * @return the string representation of that value.
	 */
	public String toString(Object value) {
		if (value == null) {
			return "null";
		} else {
			return value.toString();
		}
	}

	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object object) {
		if (object instanceof Property) {
			return getPriority() - ((Property) object).getPriority();
		}
		
		return 0;
	}
}
