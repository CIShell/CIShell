/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 23, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.AbstractSwtGUIComponent;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * Creates a GUI Component for InputType.TEXT
 * 
 * @author Bruce Herr
 */
public class TextSwtGUIComponent extends AbstractSwtGUIComponent {
    private Text text;
    
    public TextSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        super(parameter,builder);
    }

    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.TEXT;
    }
    
    /**
     * Given the text in the textbox, return the parsed Object value depending
     * on the subclass of this class (or just the text for this class in 
     * particular)
     * 
     * @param text the text of the textbox
     * @return the parsed value
     */
    protected Object getValue(String text) {
        return text;
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        text.setText(value.toString());
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#addComponent(org.eclipse.swt.widgets.Composite, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Composite group, String label, String description, Object defaultValue) {              
        text = createTextField(group);
        
        GridData gd = new GridData(SWT.FILL,SWT.CENTER,true,false);
        gd.horizontalSpan = 2;
        text.setLayoutData(gd);
        
        if(defaultValue != null) {
            text.setText(defaultValue.toString());
        } else {
            text.setText("");
        }

        text.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                update();
            }
        });        
    }
    
    /**
     * Create a text field that is used by this object
     * 
     * @param group
     * @return
     */
    protected Text createTextField(Composite group) {
        return new Text(group, SWT.BORDER);
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#getValue()
     */
    protected Object getValue() {
        return getValue(text.getText());
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return value instanceof String;
    }
    
    protected void setEnabled(boolean isEnabled) {
        text.setEnabled(isEnabled);
    }
}
