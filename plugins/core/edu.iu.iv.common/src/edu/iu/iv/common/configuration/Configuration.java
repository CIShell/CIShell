/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 20, 2005 at Indiana University.
 */
package edu.iu.iv.common.configuration;


/**
 * A configuration interface. This class allows for getting
 * settings for a program from a data store.
 * 
 * @author Bruce Herr
 */
public interface Configuration {
    
    /**
     * 
     * @param property the property
     * @return if this property is set in the configuration
     */
    public boolean contains(String property);
    
    /**
     * get the associated value for the property. 
     * 
     * @param property the property to get its value from.
     * @return the value of the given property
     */
	public boolean getBoolean(String property);
	
    /**
     * get the associated value for the property. 
     * 
     * @param property the property to get its value from.
     * @return the value of the given property
     */
	public double getDouble(String property);
	
    /**
     * get the associated value for the property. 
     * 
     * @param property the property to get its value from.
     * @return the value of the given property
     */
	public float getFloat(String property);
	
    /**
     * get the associated value for the property. 
     * 
     * @param property the property to get its value from.
     * @return the value of the given property
     */
	public int getInt(String property);
	
    /**
     * get the associated value for the property. 
     * 
     * @param property the property to get its value from.
     * @return the value of the given property
     */
	public long getLong(String property);
    
    /**
     * get the associated value for the property. 
     * 
     * @param property the property to get its value from.
     * @return the value of the given property
     */
    public String getString(String property);
    
    /**
     * Sets a property value for the configuration.
     * 
     * @param property the property
     * @param value the property's associated value.
     */
	public void setValue(String property, boolean value);
	
	/**
     * Sets a property value for the configuration.
     * 
     * @param property the property
     * @param value the property's associated value.
     */
	public void setValue(String property, double value);
	
	/**
     * Sets a property value for the configuration.
     * 
     * @param property the property
     * @param value the property's associated value.
     */
	public void setValue(String property, float value);
	
	/**
     * Sets a property value for the configuration.
     * 
     * @param property the property
     * @param value the property's associated value.
     */
	public void setValue(String property, int value);
	
	/**
     * Sets a property value for the configuration.
     * 
     * @param property the property
     * @param value the property's associated value.
     */
	public void setValue(String property, long value);
	
	/**
     * Sets a property value for the configuration.
     * 
     * @param property the property
     * @param value the property's associated value.
     */
    public void setValue(String property, String value);
}