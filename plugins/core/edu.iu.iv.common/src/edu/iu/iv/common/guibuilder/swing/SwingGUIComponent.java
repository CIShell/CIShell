/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing;

import java.awt.Color;

import javax.swing.Icon;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterProperty;

/**
 * A component for the SwingGUIBuilder. each component corresponds to one parameter.
 * And is associated with one InputType.
 * 
 * @author Bruce Herr
 */
public interface SwingGUIComponent extends ParameterProperty{
    /**
     * The Tooltip Image to use for hovering over and displaying a tooltip.
     * NOTE: NOT CURRENTLY IN USE!
     */
    public static final Icon TOOLTIP_IMAGE = null;
    
    /**
     * The Color to use for errors.
     */
    public static final Color ERROR_COLOR = Color.RED;
    
    /**
     * @return if the component's value is valid
     */
    public boolean isValid();
    
    /**
     * @return the InputType this Component corresponds to.
     */
    public InputType getCorrespondingInputType();
    
    /**
     * @return the Parameter this component is getting a value for.
     */
    public Parameter getParameter();
}
