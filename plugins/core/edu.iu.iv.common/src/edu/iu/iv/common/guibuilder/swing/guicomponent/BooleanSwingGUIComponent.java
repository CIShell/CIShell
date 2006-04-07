/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.BOOLEAN
 * 
 * @author Bruce Herr
 */
public class BooleanSwingGUIComponent extends AbstractSwingGUIComponent {
    JCheckBox checkbox;

    /**
     * @param parameter
     * @param builder
     */
    public BooleanSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        super(parameter, builder, false);
    }

    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.BOOLEAN;
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#addComponent(org.eclipse.swt.widgets.Composite, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Container group, String label, String description, Object defaultValue) {
        checkbox = new JCheckBox();
        group.add(checkbox);
        group.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        //figure out and set its default value
        if (defaultValue != null && defaultValue instanceof Boolean) {
            checkbox.setSelected(((Boolean) defaultValue).booleanValue());
        } else {
            checkbox.setSelected(true);
        }
        
        if(label != null)
            checkbox.setText(label);
        else	
            checkbox.setText("");
        
        checkbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                update();
            }});
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#updateLabel(boolean)
     */
    protected void updateLabel(boolean isValid) {
        if (isValid) {
            checkbox.setForeground(null);
        } else {
            checkbox.setForeground(ERROR_COLOR);
        }
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#getValue()
     */
    protected Object getValue() {
        return Boolean.valueOf(checkbox.isSelected());
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return value instanceof Boolean;
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        checkbox.setSelected(value == Boolean.TRUE);
    }

}
