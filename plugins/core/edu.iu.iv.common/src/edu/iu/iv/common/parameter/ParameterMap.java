/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 11, 2005 at Indiana University.
 */
package edu.iu.iv.common.parameter;

import java.awt.Color;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Map of Parameters. Also provides
 * several convenience methods for getting values
 * set in the parameters and for easily adding new
 * parameters.
 * 
 * @author Bruce Herr
 */
public class ParameterMap {
	private Map parameterMap;

	public ParameterMap() {
		parameterMap = new LinkedHashMap();
	}

	/**
	 * adds a new Parameter to the map
	 * 
	 * @param key
	 *            a unique string key
	 * @param parameter
	 *            the parameter
	 */
	public void put(String key, Parameter parameter) {
		parameterMap.put(key, parameter);
	}

	/**
	 * get the Parameter corresponding to the given key
	 * 
	 * @param key
	 *            the key
	 * @return the Parameter
	 */
	public Parameter get(String key) {
		return (Parameter) parameterMap.get(key);
	}

	/**
	 * remove the parameter with given key
	 * 
	 * @param key
	 *            the key to the param
	 */
	public void remove(String key) {
		parameterMap.remove(key);
	}

	/**
	 * @return an Iterator of all the parameters in this map. Note: the order is
	 *         in insertion order of the parameterMap.
	 */
	public Iterator getAllParameters() {
		return parameterMap.values().iterator();
	}

	/**
	 * @return an Iterator of all the keys in this map.
	 */
	public Iterator getAllKeys() {
		return parameterMap.keySet().iterator();
	}
    
    /**
     * @return the size of the parameter map
     */
    public int size() {
        return parameterMap.size();
    }

	/**
	 * get a value from the key-associated parameter.
	 * 
	 * @param key
	 *            the key to the parameter
	 * @return the value
	 */
	public int getIntValue(String key) {
		return getNumberValue(key).intValue();
	}

	/**
	 * get a value from the key-associated parameter.
	 * 
	 * @param key
	 *            the key to the parameter
	 * @return the value
	 */
	public double getDoubleValue(String key) {
		return getNumberValue(key).doubleValue();
	}

	/**
	 * get a value from the key-associated parameter.
	 * 
	 * @param key
	 *            the key to the parameter
	 * @return the value
	 */
	public float getFloatValue(String key) {
		return getNumberValue(key).floatValue();
	}

	/**
	 * get a value from the key-associated parameter.
	 * 
	 * @param key
	 *            the key to the parameter
	 * @return the value
	 */
	public Number getNumberValue(String key) {
		if (!(get(key).getValue() instanceof Number)) {
			throw new IllegalArgumentException(key
					+ " does not contain a number.");
		}

		return (Number) get(key).getValue();
	}

	/**
	 * get a value from the key-associated parameter.
	 * 
	 * @param key
	 *            the key to the parameter
	 * @return the value
	 */
	public boolean getBooleanValue(String key) {
		if (!(get(key).getValue() instanceof Boolean)) {
			throw new IllegalArgumentException(key
					+ " does not contain a boolean.");
		}

		return ((Boolean) get(key).getValue()).booleanValue();
	}

	/**
	 * get a value from the key-associated parameter.
	 * 
	 * @param key
	 *            the key to the parameter
	 * @return the value
	 */
	public File getFileValue(String key) {
		if (!(get(key).getValue() instanceof File)) {
			throw new IllegalArgumentException(key
					+ " does not contain a File.");
		}

		return (File) get(key).getValue();
	}

	/**
	 * get a value from the key-associated parameter.
	 * 
	 * @param key
	 *            the key to the parameter
	 * @return the value
	 */
	public File getDirectoryValue(String key) {
		return getFileValue(key);
	}

	/**
	 * get a value from the key-associated parameter.
	 * 
	 * @param key
	 *            the key to the parameter
	 * @return the value
	 */
	public String getTextValue(String key) {
		if (get(key).getValue() == null) {
			return null;
		} else {
			return get(key).getValue().toString();
		}
	}

	/**
	 * get a value from the key-associated parameter.
	 * 
	 * @param key
	 *            the key to the parameter
	 * @return the value
	 */
	public int[] getIntArrayValue(String key) {
		if (!(get(key).getValue() instanceof int[])) {
			throw new IllegalArgumentException(key
					+ " does not contain an int[].");
		}

		return (int[]) get(key).getValue();
	}

	public Color getColorValue(String key) {
		Object o = get(key).getValue();
		if (o instanceof Color)
			return (Color) o;
		else
			throw new IllegalArgumentException(key + " does not contain a "
					+ Color.class.getName());
	}
	
	public void putColorOption(String key, String name, String description, Color defaultValue, Validator validator) {
	    put(key, new Parameter(name, description, InputType.COLOR, defaultValue, validator));
	}
	
	public void putDirectoryOption(String key, String name, String description, File defaultValue, Validator validator) {
	    put(key, new Parameter(name, description, InputType.DIRECTORY, defaultValue, validator));
	}
	
	public void putFileOption(String key, String name, String description, File defaultValue, Validator validator) {
	    put(key, new Parameter(name, description, InputType.FILE, defaultValue, validator));
	}
	
	public void putBooleanOption(String key, String name, String description, boolean defaultValue, Validator validator) {
	    put(key, new Parameter(name, description, InputType.BOOLEAN, Boolean.valueOf(defaultValue), validator));
	}
	
	public void putIntOption(String key, String name, String description, int defaultValue, Validator validator) {
	    put(key, new Parameter(name, description, InputType.INTEGER, new Integer(defaultValue), validator));
	}
	
	public void putDoubleOption(String key, String name, String description, double defaultValue, Validator validator) {
	    put(key, new Parameter(name, description, InputType.DOUBLE, new Double(defaultValue), validator));
	}
	
	public void putFloatOption(String key, String name, String description, float defaultValue, Validator validator) {
	    put(key, new Parameter(name, description, InputType.FLOAT, new Float(defaultValue), validator));
	}
	
	public void putTextOption(String key, String name, String description, String defaultValue, Validator validator) {
	    put(key, new Parameter(name, description, InputType.TEXT, defaultValue, validator));
	}
	
	public void putSingleChoiceListOption(String key, String name, String description, String[] defaultValue, int defaultSelection, Validator validator) {
	    put(key, new Parameter(name, description, InputType.SINGLE_CHOICE_LIST, defaultValue, new Integer(defaultSelection), validator));
	}
	
	public void putMultiChoiceListOption(String key, String name, String description, String[] defaultValue, int[] defaultSelection, Validator validator) {
	    put(key, new Parameter(name, description, InputType.MULTI_CHOICE_LIST, defaultValue, defaultSelection, validator));
	}
}
