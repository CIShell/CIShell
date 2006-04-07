/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 11, 2005 at Indiana University.
 */
package edu.iu.iv.common.parameter;

import edu.iu.iv.common.property.PropertyMap;

/**
 * A parameter that has certain properties that can be attached. There is
 * also an associated value to this parameter. This will usually be used
 * by algorithms for determining the value of specific parameters needed
 * for the algorithm. 
 * 
 * @author Bruce Herr
 */
public class Parameter extends PropertyMap {
    private static final long serialVersionUID = 1L;
    private Object value;
    private boolean enabled;

    /** create a parameter with no properties */
    public Parameter() { setEnabled(true); }
    
    /**
     * Create a parameter with the given properties
     * 
     * @param name the name of the parameter (uses ParameterProperty.NAME)
     * @param description the description of the parameter (uses ParameterProperty.DESCRIPTION)
     * @param inputType the type input to use to obtain the data (uses ParameterProperty.INPUT_TYPE)
     * @param defaultValue a default value for the data (uses ParameterProperty.DEFAULT_VALUE)
     */
    public Parameter(String name, String description, InputType inputType, Object defaultValue) {
        this(name,description,inputType,defaultValue,null);
    }

    /**
     * Create a parameter with the given properties
     * 
     * @param name the name of the parameter (uses ParameterProperty.NAME)
     * @param description the description of the parameter (uses ParameterProperty.DESCRIPTION)
     * @param inputType the type input to use to obtain the data (uses ParameterProperty.INPUT_TYPE)
     * @param defaultValue a default value for the data (uses ParameterProperty.DEFAULT_VALUE)     
     * @param validator a validator for validating potential values (uses ParameterProperty.VALIDATOR)
     */
    public Parameter(String name, String description, InputType inputType, Object defaultValue, Validator validator) {
        this(name,description,inputType,defaultValue,null,validator);
    }

    /**
     * Create a parameter with the given properties
     * 
     * @param name the name of the parameter (uses ParameterProperty.NAME)
     * @param description the description of the parameter (uses ParameterProperty.DESCRIPTION)
     * @param inputType the type input to use to obtain the data (uses ParameterProperty.INPUT_TYPE)
     * @param defaultValue a default value for the data (uses ParameterProperty.DEFAULT_VALUE)
     * @param defaultSelection if the InputType supports it, a default selection for the data (uses ParameterProperty.DEFAULT_SELECTION);     
     * @param validator a validator for validating potential values (uses ParameterProperty.VALIDATOR)
     */    
    public Parameter(String name, String description, InputType inputType, Object defaultValue, Object defaultSelection, Validator validator) {
        this();
        if (inputType == null) {
            inputType = InputType.UNSUPPORTED;
        }
        
        if (!inputType.isPropertyValueAcceptable(defaultValue) ) {
            throw new IllegalArgumentException("Default value is not of the defined data type acceptable!");
        }
        
        setPropertyValue(ParameterProperty.NAME,name);
        setPropertyValue(ParameterProperty.DESCRIPTION,description);
        setPropertyValue(ParameterProperty.INPUT_TYPE,inputType);
        setPropertyValue(ParameterProperty.DEFAULT_VALUE,defaultValue);
        
        if (defaultSelection != null) {
            setPropertyValue(ParameterProperty.DEFAULT_SELECTION,defaultSelection);
            
            setValue(defaultSelection);
        } else {
            setValue(defaultValue);
        }
        
        //use a null validator if none provided
        if (validator == null) {
            validator = Validator.NULL_VALIDATOR;
        }
        
        setPropertyValue(ParameterProperty.VALIDATOR,validator);
    }
    
    /**
     * @return the associated value for this parameter
     */
    public Object getValue() {
        return value;
    }
    
    /**
     * sets the value for this parameter
     * @param value the value
     */
    public void setValue(Object value) {
        this.value = value;
    }
    
    /**
     * set if this Parameter is enabled or not
     * 
     * @param enabled if its enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * @return if this Parameter is enabled or not
     */
    public boolean isEnabled() {
        return enabled;
    }
}
