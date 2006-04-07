/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on May 24, 2005 at Indiana University.
 */
package edu.iu.iv.common.guibuilder.swing.guicomponent;

import java.awt.Container;
import java.awt.FlowLayout;
import java.util.Arrays;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent;
import edu.iu.iv.common.guibuilder.swing.SwingPanelBuilder;
import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;

/**
 * Creates a GUI Component for InputType.MULTI_CHOICE_LIST
 * 
 * @author Bruce Herr
 */
public class MultiChoiceListSwingGUIComponent extends AbstractSwingGUIComponent {
    protected JList items;
    
    public MultiChoiceListSwingGUIComponent(Parameter parameter, SwingPanelBuilder builder) {
        super(parameter,builder);
        Object value = parameter.getPropertyValue(DEFAULT_VALUE);
        Object selection = parameter.getPropertyValue(DEFAULT_SELECTION);
        
        if (selection != null && !(selection instanceof int[])) {
            throw new IllegalArgumentException("InputType.MULTI_CHOICE_LIST: Default Selection MUST be an int[]");
        }
        
        if (!(value instanceof String[]) || value == null) {
            throw new IllegalArgumentException("InputType.MULTI_CHOICE_LIST: Default Value MUST be a String[]");
        }
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.SwingGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.MULTI_CHOICE_LIST;
    }
    
    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#addComponent(java.awt.Container, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Container group, String label, String description, Object defaultValue) {
        group.setLayout(new FlowLayout(FlowLayout.LEFT));
        String[] choices = (String[]) defaultValue;
        
        if (choices == null) {
            choices = new String[]{""};
        }
        
        items = new JList(choices);
        group.add(items);
        
        //this InputType uses an extra Propety called DEFAULT_SELECTION that says which
        //items are selected first.
        int[] selection = (int[]) parameter.getPropertyValue(DEFAULT_SELECTION);
        
        if (selection == null) {
            selection = new int[]{0};
        }
        
        items.setSelectedIndices(selection);
        
        items.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent arg0) {
                update();
            }});
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#getValue()
     */
    protected Object getValue() {
        return items.getSelectedIndices();
    }
    
    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#isEqual(java.lang.Object, java.lang.Object)
     */
    protected boolean isEqual(Object value1, Object value2) {
        //return false if one is null, true if they are both null
        if (value1 == null || value2 == null) {
            return value1 == value2;
        }
        
        int[] v1 = (int[]) value1;
        int[] v2 = (int[]) value2;
        
        if (v1.length != v2.length) return false;
        
        //put the selections in the same order.
        Arrays.sort(v1);
        Arrays.sort(v2);
        
        for (int i=0; i < v1.length; i++) {
            if (v1[i] != v2[i]) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        items.clearSelection();
        items.setSelectedIndices((int[]) value);
    }   
    
    /**
     * @see edu.iu.iv.common.guibuilder.swing.AbstractSwingGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return value instanceof int[];
    }
}
