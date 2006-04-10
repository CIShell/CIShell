/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Mar 24, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.AbstractSwtGUIComponent;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * Creates a GUI Component for InputType.UNSUPPORTED
 * 
 * @author Bruce Herr
 */
public class UnsupportedSwtGUIComponent extends AbstractSwtGUIComponent {
    
    public UnsupportedSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        super(parameter,builder);
    }

    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.UNSUPPORTED;
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#addComponent(org.eclipse.swt.widgets.Composite, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(Composite group, String label, String description, Object defaultValue) {
        final Text text = new Text(group, SWT.BORDER);
        
        GridData gridData = new GridData(SWT.LEFT,SWT.CENTER,false,false);
        gridData.horizontalSpan = 2;
        text.setLayoutData(gridData);
        
        if(defaultValue != null) {
            text.setText("Unsupported: " + defaultValue);
        } else {
            text.setText("Unsupported");
        }

        text.setEnabled(false);
        text.setEditable(false);
        parameter.setValue(defaultValue);
        parameter.setEnabled(false);
    }
    
    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#isValid()
     */
    public boolean isValid() {
        return true;
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#getValue()
     */
    protected Object getValue() {
        return parameter.getPropertyValue(DEFAULT_VALUE);
    }
    
    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        
    }   

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return true;
    } 
    
    protected void setEnabled(boolean isEnabled) {
        //Always disabled, don't need to do anything.
    }
}
