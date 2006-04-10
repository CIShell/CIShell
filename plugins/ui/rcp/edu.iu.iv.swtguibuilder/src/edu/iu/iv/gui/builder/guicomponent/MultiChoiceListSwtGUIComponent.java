/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 25, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.AbstractSwtGUIComponent;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * Creates a GUI Component for InputType.MULTI_CHOICE_LIST. Creates a List of 
 * choices that the user can choose which ones they want. 
 * 
 * @author Bruce Herr
 */
public class MultiChoiceListSwtGUIComponent extends AbstractSwtGUIComponent {
    protected List items;
    
    public MultiChoiceListSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
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
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.MULTI_CHOICE_LIST;
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#addComponent(org.eclipse.swt.widgets.Composite, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Composite group, String label, String description, Object defaultValue) {
        //allow more room vertically for this component since it is taller than most.
        
        items = new List(group, SWT.MULTI | SWT.BORDER);
        items.setItems((String[]) defaultValue);
        
        GridData gridData = new GridData(SWT.LEFT,SWT.TOP,false,false);
        //gridData.verticalSpan = 4;
        gridData.horizontalSpan = 2;
        items.setLayoutData(gridData);

        //this InputType uses an extra Propety called DEFAULT_SELECTION that says which
        //items are selected first.
        int[] selection = (int[]) parameter.getPropertyValue(DEFAULT_SELECTION);
        
        if (selection == null) {
            selection = new int[]{0};
        }
        
        items.select(selection);
        
        items.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                update();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);                
            }});
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#getValue()
     */
    protected Object getValue() {
        return items.getSelectionIndices();
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isEqual(java.lang.Object, java.lang.Object)
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
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        items.deselectAll();
        items.select((int[]) value);
    }   
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return value instanceof int[];
    }

    protected void setEnabled(boolean isEnabled) {
        items.setEnabled(isEnabled);
    }
}