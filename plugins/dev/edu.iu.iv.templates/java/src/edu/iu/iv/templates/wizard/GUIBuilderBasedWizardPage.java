/* 
 * InfoVis CyberInfrastructure: A Data-Code-Compute Resource for Research
 * and Education in Information Visualization (http://iv.slis.indiana.edu/).
 * 
 * Created on Sep 27, 2005 at Indiana University.
 */
package edu.iu.iv.templates.wizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.iu.iv.common.parameter.ParameterMap;
import edu.iu.iv.common.parameter.Parameterizable;
import edu.iu.iv.gui.builder.ChangeListener;
import edu.iu.iv.gui.builder.SwtCompositeBuilder;

/**
 * 
 * @author Bruce Herr
 */
public class GUIBuilderBasedWizardPage extends WizardPage implements Parameterizable, ChangeListener {
    private ParameterMap parameterMap;
    private SwtCompositeBuilder gui;

    /**
     * @param pageName
     */
    public GUIBuilderBasedWizardPage(String pageName) {
        super(pageName);
    }

    /**
     * @param pageName
     * @param title
     * @param titleImage
     */
    public GUIBuilderBasedWizardPage(String pageName, String title,
            ImageDescriptor titleImage) {
        super(pageName, title, titleImage);
    }
    
    public ParameterMap getParameters() {
        return parameterMap;
    }
    
    public void setParameters(ParameterMap parameterMap) {
        this.parameterMap = parameterMap;
    }

    /**
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        gui = new SwtCompositeBuilder(parent, SWT.NONE, getParameters());
        
        gui.addChangeListener(this);
        setControl(gui.getComposite());
        changeOccured();
    }

    public void changeOccured() {
        if (!gui.isValid()) {
            setPageComplete(false);
            setErrorMessage("Please input correct values.");
        } else {
            setPageComplete(true);
            setErrorMessage(null);
        }
    }
}
