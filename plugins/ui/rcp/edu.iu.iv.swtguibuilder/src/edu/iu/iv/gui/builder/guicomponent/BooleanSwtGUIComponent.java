/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 23, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.AbstractSwtGUIComponent;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * Creates a GUI Component for InputType.BOOLEAN
 * 
 * @author Bruce Herr
 */
public class BooleanSwtGUIComponent extends AbstractSwtGUIComponent {
    protected Button checkbox;
    
    public BooleanSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        //this component uses its own label, so doesn't use the abstract class's label drawing
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
    protected void addComponent(Composite group, String label, String description, Object defaultValue) {
        checkbox = new Button(group, SWT.CHECK);
        
        GridData gridData = new GridData(SWT.LEFT,SWT.CENTER,false,false);
        gridData.horizontalSpan = 3;
        checkbox.setLayoutData(gridData);

        //figure out and set its default value
        if (defaultValue != null && defaultValue instanceof Boolean) {
            checkbox.setSelection(((Boolean) defaultValue).booleanValue());
        } else {
            checkbox.setSelection(true);
        }
        
        if(label != null)
            checkbox.setText(label);
        else	
            checkbox.setText("");

        checkbox.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {                    
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
        return Boolean.valueOf(checkbox.getSelection());
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
        checkbox.setSelection(value == Boolean.TRUE);
    }
    
    protected void setEnabled(boolean isEnabled) {
        checkbox.setEnabled(isEnabled);
    }
}