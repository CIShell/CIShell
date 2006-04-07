/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 11, 2005 at Indiana University.
 */
package edu.iu.iv.common.parameter;

import edu.iu.iv.common.property.Property;

/**
 * Some default Parameter properties used by the IVC.
 * 
 * @author Bruce Herr
 */
public interface ParameterProperty {
    /** the name of the parameter 
     *  Associated property value is of type String.
     */
    public static final Property NAME = new Property("Name",String.class,1);
    
    /** a description of the parameter, usually including valid values 
     *  Associated property value is of type String.
     */
    public static final Property DESCRIPTION = new Property("Description",String.class,3);
    
    /** the type of input method to use to get the parameter value 
     *  Associated property value is of type InputType. 
     */
    public static final Property INPUT_TYPE = new Property("Input Type",InputType.class,5);
    
    /** The default value for the parameter 
     * Associated property value is of type Object. 
     */
    public static final Property DEFAULT_VALUE = new Property("Default Value",Object.class,7);
    
    /** The default selection for the parameter. 
     * This property is only used with SINGLE_SELECTION_LISTs and MULTI_SELECTION_LISTs
     */
    public static final Property DEFAULT_SELECTION = new Property("Default Selection",Object.class,8);
    
    /** A validator for the parameter that will determine if a given value is valid for this parameter 
     *  Associated property value is of type Validator.
     */
    public static final Property VALIDATOR = new Property("Validator",Validator.class,9);
}
