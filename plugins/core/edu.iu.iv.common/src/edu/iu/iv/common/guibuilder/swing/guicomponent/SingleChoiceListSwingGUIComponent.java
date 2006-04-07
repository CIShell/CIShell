/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.SINGLE_CHOICE_LIST
 * 
 * @author Bruce Herr
 */
public class SingleChoiceListSwingGUIComponent extends AbstractSwingGUIComponent {
    protected JComboBox items;
    
    public SingleChoiceListSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        super(parameter,builder);

        Object value = parameter.getPropertyValue(DEFAULT_VALUE);
        
        if (!(value instanceof String[]) || value == null) {
            throw new IllegalArgumentException("InputType.SINGLE_CHOICE_LIST: Default Value MUST be a String[]");
        }
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.SINGLE_CHOICE_LIST;
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#addComponent(java.awt.Container, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Container group, String label, String description, Object value) {
        group.setLayout(new FlowLayout(FlowLayout.LEFT));
        String[] defaultValue = (String[]) value;
        
        //this InputType uses an extra Property called DEFAULT_SELECTION that 
        //specifies which index is chosen first
        Object defaultSelection = parameter.getPropertyValue(DEFAULT_SELECTION); 
        
        items = new JComboBox(defaultValue);
        group.add(items);
        
        items.setMaximumSize(new Dimension(items.getWidth()+2000,items.getHeight()+50));
        
        int selection = 0;
        
        if (defaultSelection != null && defaultSelection instanceof Integer) {
            selection = ((Integer) defaultSelection).intValue();
        }
        
        //default selection is the first element
        if (selection < 0 || selection >= defaultValue.length) {
            selection = 0;
        }
        
        items.setSelectedIndex(selection);
        
        items.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                update();
            }});        
    }

    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#getValue()
     */
    protected Object getValue() {
        return new Integer(items.getSelectedIndex());
    }
    
    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        items.setSelectedIndex(((Integer) value).intValue());
    }   

    /**
     * 
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return value instanceof Integer;
    }
}
