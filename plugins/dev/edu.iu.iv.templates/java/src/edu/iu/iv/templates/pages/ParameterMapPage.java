/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Nov 8, 2005 at Indiana University.
 */
package edu.iu.iv.templates.pages;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.gui.builder.SwtGUIBuilder;
import edu.iu.iv.gui.builder.builder.ParameterMapBuilder;

/**
 * 
 * @author Bruce Herr
 */
public class ParameterMapPage extends WizardPage {
    private ParameterMapBuilder builder;

    /**
     * @param pageName
     */
    public ParameterMapPage(String pageName) {
        super(pageName);
    }

    /**
     * @param pageName
     * @param title
     * @param titleImage
     */
    public ParameterMapPage(String pageName, String title,
            ImageDescriptor titleImage) {
        super(pageName, title, titleImage);
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        SwtGUIBuilder.setParent(getShell());
        builder = new ParameterMapBuilder(parent);
        
        setControl(builder.getComposite());
    }
    
    public ParameterMap getParameterMap() {
        return builder.getParameterMap();
    }
}
