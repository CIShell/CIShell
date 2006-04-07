/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.INTEGER
 * 
 * @author Bruce Herr
 */
public class IntegerSwingGUIComponent extends TextSwingGUIComponent {

    /**
     * @param parameter
     * @param builder
     */
    public IntegerSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        super(parameter, builder);
    }
    
    /**
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.INTEGER;
    }
    
    /**
     * @see edu.iu.iv.common.guibuilder.swing.guicomponent.TextSwingGUIComponent#getValue(java.lang.String)
     */
    protected Object getValue(String text) {
        try {
            return new Integer(text);        
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        //make sure its an Integer
        return value != null && value instanceof Integer;
    }

}
