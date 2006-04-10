/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 23, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import edu.iu.iv.SwtGUIBuilderImageLoader;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.ParameterProperty;

/**
 * A component for the SwtGUIBuilder. each component corresponds to one parameter.
 * And is associated with one InputType.
 * 
 * @author Bruce Herr
 */
public interface SwtGUIComponent extends ParameterProperty {
    /**
     * The Tooltip Image to use for hovering over and displaying a tooltip
     */
    public static final Image TOOLTIP_IMAGE = SwtGUIBuilderImageLoader.createImage("question.png");
    
    /**
     * The Color to use for errors.
     */
    public static final Color ERROR_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_RED);
    
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
