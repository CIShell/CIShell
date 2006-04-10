/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 23, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * Creates a GUI Component for InputType.FLOAT
 * 
 * @author Bruce Herr
 */
public class FloatSwtGUIComponent extends TextSwtGUIComponent {
    
    public FloatSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        super(parameter, builder);
    }

    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.FLOAT;
    }
    
    /**
     * @see edu.iu.iv.gui.builder.guicomponent.TextSwtGUIComponent#getValue(java.lang.String)
     */
    protected Object getValue(String text) {
        try {
            return new Float(text);        
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        //make sure its a Float
        return value != null && value instanceof Float;
    }
}
