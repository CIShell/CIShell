/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.common.parameter.Validator;

/**
 * A helper class to make creating new SwingGUIComponents easier. Sets up 
 * everything but the Specific widget used to get input for the Parameter. Draws
 * the label, tooltip, and validates with the validator. This also helps to make
 * each component more uniform.
 * 
 * @author Bruce Herr
 */
public abstract class AbstractSwingGUIComponent implements SwingGUIComponent {
    protected Parameter parameter;
    protected Container userArea;
    protected SwingPanelBuilder builder;
    protected Validator validator;
    
    protected boolean drawLabel;
    protected JLabel message;
    protected JPanel group;
    protected Object value;

    /**
     * Creates a new SwingGUIComponent given a parameter and builder
     * 
     * @param parameter the parameter
     * @param builder the builder
     */
    public AbstractSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        this(parameter,builder,true);
    }
    
    /**
     * Creates a new SwingGUIComponent given a parameter and builder. Can optionally
     * specify whether or not to draw the label.
     * 
     * @param parameter the parameter
     * @param builder the builder
     * @param drawLabel whether to draw the label or not.
     */
    public AbstractSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder, boolean drawLabel) {
        this.parameter = parameter;
        this.userArea = builder.getUserArea();
        this.builder = builder;
        this.drawLabel = drawLabel;
        
        String name = (String) parameter.getPropertyValue(NAME);
        String desc = (String) parameter.getPropertyValue(DESCRIPTION);
        Object value = parameter.getPropertyValue(DEFAULT_VALUE);
        validator = (Validator) parameter.getPropertyValue(VALIDATOR);
     
        //make sure has a legal validator
        if (validator == null || !(validator instanceof Validator)) {
            validator = Validator.NULL_VALIDATOR;
        }
                
        addComponent(name, desc, value);
    }
        
    /**
     * Adds the component to the GUI
     * 
     * @param label the label to use
     * @param description the description to use
     * @param defaultValue the default value to use
     */
    protected void addComponent(String label, String description, Object defaultValue) {
        group = new JPanel();
        
        group.setLayout(new BoxLayout(group,BoxLayout.X_AXIS));

        if (drawLabel) {
	        message = new JLabel();
	        if (label == null) label = "";
	        message.setText(label);
	        
	        group.add(message);
        }

        //The subclass will add the actual input widget
        addComponent(group, label, description, defaultValue);
        
        //add a description tooltip and add to the gui
        if (description != null && !"".equals(description)) {            
            JLabel toolTip = new JLabel(" ? ");
            toolTip.setBackground(Color.YELLOW);
            toolTip.setForeground(Color.WHITE);
            toolTip.setBorder(BorderFactory.createEtchedBorder());
            
            toolTip.setToolTipText(description);
            group.setToolTipText(description);
            
            group.add(new JLabel(" "));
            group.add(toolTip);
            group.add(new JLabel(" "));
        }
        
        userArea.add(group);
        
        //update the gui (error labels and ok button) and validate
        update();
    }
    

    /**
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#isValid()
     */
    public boolean isValid() {
        Object pValue = parameter.getValue();
        //see if the parameter has been changed externally
        //if it has and its valid then set that as the new value
        if (pValue != null && !isEqual(value,pValue) && 
                isValid(pValue) && validator.isValid(pValue)) {
            value = pValue;
            setValue(pValue);
        } else {
            value = getValue();
        }
        
        //enable/disable the component visually
        Component[] subWidgets = group.getComponents();
        for (int i=0; i < subWidgets.length-1; i++) {
            subWidgets[i].setEnabled(parameter.isEnabled());
        }
        
        parameter.setValue(value);
        //check to see if its valid by the component's standards and by
        //the validator's standards
        boolean isValid = !parameter.isEnabled() || (isValid(value) && validator.isValid(value)); 
        
        updateLabel(isValid);
        return isValid;
    }
    
    /**
     * Update the label (usually changing colors depending on what 
     * is passed in to it)
     * @param isValid if the component value is valid.
     */
    protected void updateLabel(boolean isValid) {
        if (drawLabel) {
            if (isValid) {
                message.setForeground(Color.BLACK);
            } else {
                message.setForeground(ERROR_COLOR);
            }    
        }
    }
    
    /**
     * Updates the gui by validating its input and setting up graphical notifiers
     * to the user if there is something invalid. And sets the parameter.
     */
    protected void update() {
        //update the label
        updateLabel(isValid());
        
        //update the parameter value
        value = getValue();
        parameter.setValue(value);

        //signal that a change has occured
        builder.changeOccurred();
    }

    
    /**
     * given two values see if they are the same. the default is by using
     * the .equals method. subclasses can override this if .equals is not
     * sufficient
     * 
     * @param value1 the first value
     * @param value2 the second value
     * @return if the two values are equal
     */
    protected boolean isEqual(Object value1, Object value2) {
        return (value1 == null && value2 == null) || value2.equals(value1);
    }
    
    /**
     * Add the specific input widget to the GUI for getting input. You do not
     * need to add a label or description.
     * 
     * @param group the group to add it to
     * @param label the label
     * @param description the description
     * @param defaultValue the default value
     */
    protected abstract void addComponent(Container group, String label, String description, Object defaultValue);
    
    /**
     * Parse the input and return the value in the right form for the parameter
     * to save.
     * 
     * @return the value of the input
     */
    protected abstract Object getValue();
    
    /**
     * set the value of the component. this is used when an outside object changes
     * the parameter value.
     * 
     * @param value the value to set the component to
     */
    protected abstract void setValue(Object value);
    
    /**
     * @param value the parsed value for the Parameter
     * @return if the given parsed value is correct
     */
    protected abstract boolean isValid(Object value);
    
    /**
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#getCorrespondingInputType()
     */
    public abstract InputType getCorrespondingInputType();

    /**
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#getParameter()
     */
    public Parameter getParameter() {
        return parameter;
    }
}
