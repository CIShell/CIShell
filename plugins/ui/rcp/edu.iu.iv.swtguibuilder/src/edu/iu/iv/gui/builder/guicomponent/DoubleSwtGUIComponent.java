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
 * Creates a GUI Component for InputType.DOUBLE
 * 
 * @author Bruce Herr
 */
public class DoubleSwtGUIComponent extends TextSwtGUIComponent {

    public DoubleSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        super(parameter, builder);
    }

    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.DOUBLE;
    }
    
    /**
     * @see edu.iu.iv.gui.builder.guicomponent.TextSwtGUIComponent#getValue(java.lang.String)
     */
    protected Object getValue(String text) {
        try {
            return new Double(text);        
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        //make sure its a Double
        return value != null && value instanceof Double;
    }
}
