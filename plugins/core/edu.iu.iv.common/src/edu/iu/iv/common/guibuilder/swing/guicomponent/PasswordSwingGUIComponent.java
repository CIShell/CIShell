/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 25, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import javax.swing.JPasswordField;
import javax.swing.JTextField;

import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.PASSWORD
 * 
 * @author Bruce Herr
 */
public class PasswordSwingGUIComponent extends TextSwingGUIComponent {

    /**
     * @param parameter
     * @param builder
     */
    public PasswordSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        super(parameter, builder);
    }
    
    /**
     * @see edu.iu.iv.common.guibuilder.swing.guicomponent.TextSwingGUIComponent#createTextField()
     */
    protected JTextField createTextField() {
        return new JPasswordField();
    }
}
