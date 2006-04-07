/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;

import edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.COLOR
 * @author Bruce Herr
 */
public class ColorSwingGUIComponent extends AbstractSwingGUIComponent {
    protected JButton button;
    protected Color color;

    /**
     * @param parameter
     * @param builder
     */
    public ColorSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        super(parameter, builder);
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#addComponent(java.awt.Container, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(final Container group, String label,String description, Object defaultValue) {
        group.setLayout(new FlowLayout(FlowLayout.LEFT));
        button = new JButton();
        group.add(button);
        
        color = (Color) defaultValue;
        
        button.setText("Choose Color");
        button.setBackground(color);
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                color = JColorChooser.showDialog(group, "Choose a Color", color);
                button.setBackground(color);
            }});   
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#getValue()
     */
    protected Object getValue() {
        return color;
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        color = (Color) value;
        button.setBackground(color);
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return value instanceof Color && value != null;
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.COLOR;
    }
}
