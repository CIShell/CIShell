/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 27, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import java.awt.Color;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;

import edu.iu.iv.common.parameter.InputType;
import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.AbstractSwtGUIComponent;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * 
 * @author Bruce Herr
 */
public class ColorSwtGUIComponent extends AbstractSwtGUIComponent {
    protected Button button;
    protected Color color = Color.WHITE;

    /**
     * @param parameter
     * @param builder
     */
    public ColorSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        super(parameter, builder);
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#addComponent(org.eclipse.swt.widgets.Composite, java.lang.String, java.lang.String, java.lang.Object)
     */
    protected void addComponent(final Composite group, String label,String description, Object defaultValue) {
        button = new Button(group, SWT.NONE);
        
        GridData gd = new GridData(SWT.LEFT,SWT.CENTER,false,false);
        gd.horizontalSpan = 2;
        button.setLayoutData(gd);
        
        button.setText("Choose a Color");
        
        button.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                ColorDialog dialog = new ColorDialog(group.getShell());
                
                dialog.setRGB(new RGB(color.getRed(), color.getGreen(), color.getBlue()));
                
                RGB rgb = dialog.open();
                
                if (rgb != null) {
                    setValue(new Color(rgb.red, rgb.green, rgb.blue));
                    
                    update();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }});
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#getValue()
     */
    protected Object getValue() {
        return color;
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#setValue(java.lang.Object)
     */
    protected void setValue(Object value) {
        color = (Color) value;
        
        if (button != null && value != null && value instanceof Color) {
	        button.setBackground(new org.eclipse.swt.graphics.Color
	                (button.getDisplay(), color.getRed(), color.getGreen(), color.getBlue()));
	        
        }
    }

    /**
     * @see edu.iu.iv.gui.builder.AbstractSwtGUIComponent#isValid(java.lang.Object)
     */
    protected boolean isValid(Object value) {
        return value instanceof Color && value != null;
    }


    /**
     * @see edu.iu.iv.gui.builder.SwtGUIComponent#getCorrespondingInputType()
     */
    public InputType getCorrespondingInputType() {
        return InputType.COLOR;
    }

    protected void setEnabled(boolean isEnabled) {
        button.setEnabled(isEnabled);
    }
}
