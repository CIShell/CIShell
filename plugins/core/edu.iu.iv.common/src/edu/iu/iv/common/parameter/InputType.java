/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 11, 2005 at Indiana University.
 */
package edu.iu.iv.common.parameter;

import java.awt.Color;
import java.io.File;

import edu.iu.iv.common.property.Property;

/**
 * InputTypes are methods of input for Parameters that should be provided by
 * all GUIBuilder implementations. You must add one of these to each parameter
 * using Parameter.setPropertyValue(INPUT_TYPE,InputType.*); Note that each
 * InputType has required Properties that should also be set on the particular
 * Parameter.
 * 
 * @author Bruce Herr
 */
public class InputType extends Property {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * A TEXT Input type. A parameter that uses this type will need to provide a
     * String-based DEFAULT_VALUE and upon exiting from the created GUI, its
     * parameter will be set as the text typed in. 
     * 
     * DEFAULT_VALUE: String, VALIDATOR input: String, Parameter value: String
     */
    public static final InputType TEXT = new InputType("Text Input",String.class);
    
    /**
     * A Password Input type. A parameter that uses this type will need to provide a
     * String-based DEFAULT_VALUE and upon exiting from the created GUI, its
     * parameter will be set as the text typed in. 
     * 
     * DEFAULT_VALUE: String, VALIDATOR input: String, Parameter value: String
     */
    public static final InputType PASSWORD = new InputType("Password Input",String.class);
    
    /**
     * An Integer Input type. A parameter that uses this type will need to provide a
     * Integer-based DEFAULT_VALUE and upon exiting from the created GUI, its
     * parameter will be set as the Integer typed in. 
     * 
     * DEFAULT_VALUE: Integer, VALIDATOR input: Integer, Parameter value: Integer
     */
    public static final InputType INTEGER = new InputType("Integer Input",Integer.class);

    /**
     * A Double Input type. A parameter that uses this type will need to provide a
     * Double-based DEFAULT_VALUE and upon exiting from the created GUI, its
     * parameter will be set as the Double typed in. 
     * 
     * DEFAULT_VALUE: Double, VALIDATOR input: Double, Parameter value: Double
     */
    public static final InputType DOUBLE = new InputType("Double Input",Double.class);
    
    /**
     * A Float Input type. A parameter that uses this type will need to provide a
     * Float-based DEFAULT_VALUE and upon exiting from the created GUI, its
     * parameter will be set as the Float typed in. 
     * 
     * DEFAULT_VALUE: Float, VALIDATOR input: Float, Parameter value: Float
     */
    public static final InputType FLOAT = new InputType("Float Input",Float.class);
    
    /**
     * A Boolean Input type. A parameter that uses this type will need to provide a
     * Boolean-based DEFAULT_VALUE and upon exiting from the created GUI, its
     * parameter will be set as the Boolean chosen. 
     * 
     * DEFAULT_VALUE: Boolean, VALIDATOR input: Boolean, Parameter value: Boolean
     */
    public static final InputType BOOLEAN = new InputType("Boolean Input",Boolean.class);
    

    /**
     * A Single Choice List (eg Drop-Down Box) Input type. A parameter that uses this 
     * type will need to provide a DEFAULT_VALUE of String[] of all the possible 
     * choices the user can choose. The corresponding Parameter will be set as an
     * Integer that is an Index to the chosen element of the array. Note: The first
     * element in the array will be the default choice if the DEFAULT_SELECTION
     * property is not set. the DEFAULT_SELECTION property should be an Integer
     * and would indicate the index of the element to be selected first.
     * 
     * DEFAULT_VALUE: String[], VALIDATOR input: Integer (index), Parameter value: Integer (index)
     * Optional: DEFAULT_SELECTION: Integer (index)
     */    
    public static final InputType SINGLE_CHOICE_LIST = new InputType("Single-Choice List Input",Object.class);
    
    /**
     * A Multi Choice List (eg Multi-select Box) Input type. A parameter that uses this 
     * type will need to provide a DEFAULT_VALUE of String[] of all the possible 
     * choices the user can choose. The corresponding Parameter will be set as an
     * int[] that are the Indices to the chosen element of the array. Note: The first
     * element in the String[] will be the default choice if the DEFAULT_SELECTION
     * property is not set. the DEFAULT_SELECTION property should be an int[]
     * and would indicate the indices of the element(s) to be selected first. 
     * 
     * DEFAULT_VALUE: String[], VALIDATOR input: int[] (indices), Parameter value: int[] (indices)
     * Optional: DEFAULT_SELECTION: int[] (indices)
     */
    public static final InputType MULTI_CHOICE_LIST = new InputType("Multi-Choice List Input",Object.class);
    
    /**
     * A File Input type. A parameter that uses this type will need to provide a
     * File-based DEFAULT_VALUE and upon exiting from the created GUI, its
     * parameter will be set as the File chosen. 
     * Note: the internal validator will ensure that the File is a file and not a directory.
     * 
     * DEFAULT_VALUE: File, VALIDATOR input: File, Parameter value: File
     */
    public static final InputType FILE = new InputType("File Input",File.class);

    /**
     * A Directory Input type. A parameter that uses this type will need to provide a
     * File-based-Directory DEFAULT_VALUE and upon exiting from the created GUI, its
     * parameter will be set as the File (directory) chosen. 
     * Note: the internal validator will ensure that the File is a directory.
     * 
     * DEFAULT_VALUE: File, VALIDATOR input: File, Parameter value: File
     */
    public static final InputType DIRECTORY = new InputType("Directory Input",File.class);
    
    /**
     * A java.awt.Color Input type. A parameter that uses this type will need to provide a
     * Color-based DEFAULT_VALUE and upon exiting from the created GUI, its
     * parameter will be set as the Color chosen. 
     * 
     * DEFAULT_VALUE: Color, VALIDATOR input: Color, Parameter value: Color
     */
    public static final InputType COLOR = new InputType("Color Input",Color.class);
    
    /**
     * This InputType is used when the Parameter either doesn't provide an InputType or
     * the InputType given is not supported by the current GUIBuilder. The DEFAULT_VALUE
     * will be set as the Parameter value and no further editing will be allowed through
     * the GUI.
     */
    public static final InputType UNSUPPORTED = new InputType("Unsupported type of Input",Object.class);
    
    public InputType (String name, Class acceptableClass) {
        super(name, acceptableClass);
    }
}
