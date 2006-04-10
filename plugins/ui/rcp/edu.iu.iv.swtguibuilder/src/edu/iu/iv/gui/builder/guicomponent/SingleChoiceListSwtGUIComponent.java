/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 24, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.AbstractSwtGUIComponent;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * Creates a GUI Component for InputType.SINGLE_CHOICE_LIST
 *
 * @author Bruce Herr
 */
public class SingleChoiceListSwtGUIComponent extends AbstractSwtGUIComponent {
    protected Combo items;
    
    public SingleChoiceListSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        super(parameter,builder);

        Object value = parameter.getPropertyValue(DEFAULT_VALUE);
        
        if (!(value instanceof String[]) || value == null) {
            throw new IllegalArgumentException("InputType.SINGLE_CHOICE_LIST: Default Value MUST be a String[]");
        }
    }

    public InputType getCorrespondingInputType() {
        return InputType.SINGLE_CHOICE_LIST;
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#addComponent(org.eclipse.swt.widgets.Composite, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Composite group, String label, String description, Object value) {
        String[] defaultValue = (String[]) value;
        
        //this InputType uses an extra Property called DEFAULT_SELECTION that 
        //specifies which index is chosen first
        Object defaultSelection = parameter.getPropertyValue(DEFAULT_SELECTION); 
        
        items = new Combo(group, SWT.READ_ONLY | SWT.DROP_DOWN);
        items.setItems(defaultValue);
        
        GridData gridData = new GridData(SWT.LEFT,SWT.TOP,false,false);
        gridData.horizontalSpan = 2;
        items.setLayoutData(gridData);
        
        int selection = 0;
        
        if (defaultSelection != null && defaultSelection instanceof Integer) {
            selection = ((Integer) defaultSelection).intValue();
        }
        
        //default selection is the first element
        if (selection < 0 || selection >= defaultValue.length) {
            selection = 0;
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
        return new Integer(items.getSelectionIndex());
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        items.select(((Integer) value).intValue());
    }   

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return value instanceof Integer;
    }
    
    protected void setEnabled(boolean isEnabled) {
        items.setEnabled(isEnabled);
    }
}