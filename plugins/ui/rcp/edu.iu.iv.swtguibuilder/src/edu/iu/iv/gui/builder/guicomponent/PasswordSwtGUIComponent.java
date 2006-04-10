/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Aug 27, 2005 at Indiana University.
 */
package edu.iu.iv.gui.builder.guicomponent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.iu.iv.common.parameter.Parameter;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * 
 * @author Bruce Herr
 */
public class PasswordSwtGUIComponent extends TextSwtGUIComponent {

    /**
     * @param parameter
     * @param builder
     */
    public PasswordSwtGUIComponent(Parameter parameter, SwtCompositeBuilder builder) {
        super(parameter, builder);
    }
    
    /**
     * @see edu.iu.iv.gui.builder.guicomponent.TextSwtGUIComponent#createTextField(org.eclipse.swt.widgets.Composite)
     */
    protected Text createTextField(Composite group) {
        return new Text(group, SWT.PASSWORD | SWT.BORDER);
    }
}
