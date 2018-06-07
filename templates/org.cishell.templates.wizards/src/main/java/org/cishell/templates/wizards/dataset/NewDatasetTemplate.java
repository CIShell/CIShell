/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 10, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.wizards.dataset;

import org.cishell.templates.wizards.BasicTemplate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class NewDatasetTemplate extends BasicTemplate {
    WizardNewProjectCreationPage projectPage;

    public NewDatasetTemplate() {
        super("dataset");
        
        setPageCount(1);
    }
    
    public void addPages(Wizard wizard) {
        WizardPage page = new WizardNewProjectCreationPage("createProjectPage");
        page.setTitle("Project Properties");
        page.setDescription("Enter the project name and location");
        wizard.addPage(page);
        projectPage = (WizardNewProjectCreationPage) page;
        
        markPagesAdded();
    }

    protected void updateModel(IProgressMonitor monitor) throws CoreException {
        
    }

    public void validateOptions(TemplateOption changed) {

    }

    public IProject getProjectHandle() {
        return projectPage.getProjectHandle();
    }
}
