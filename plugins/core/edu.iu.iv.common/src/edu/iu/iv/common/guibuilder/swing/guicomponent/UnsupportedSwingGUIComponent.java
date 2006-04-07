/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JTextField;

import edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.UNSUPPORTED
 * 
 * @author Bruce Herr
 */
public class UnsupportedSwingGUIComponent extends AbstractSwingGUIComponent {

    /**
     * @param parameter
     * @param builder
     */
    public UnsupportedSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        super(parameter, builder);
    }

    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.UNSUPPORTED;
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#addComponent(java.awt.Container, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Container group, String label, String description, Object defaultValue) {
        final JTextField text = new JTextField();
        
        group.add(new JLabel(" "));
        group.add(text);
        
        if(defaultValue != null) {
            text.setText("Unsupported: " + defaultValue);
        } else {
            text.setText("Unsupported");
        }
        
        text.setMaximumSize(new Dimension(text.getWidth()+2000,text.getHeight()+50));

        text.setEnabled(false);
        parameter.setValue(defaultValue);
        parameter.setEnabled(false);
    }
    
    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#isValid()
     */
    public boolean isValid() {
        return true;
    }

    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#getValue()
     */
    protected Object getValue() {
        return parameter.getPropertyValue(DEFAULT_VALUE);
    }
    
    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        
    }   

    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return true;
    } 
}
