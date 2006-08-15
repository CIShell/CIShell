/* **************************************************************************** 
 * CIShell: Cyberinfrastructure Shell, An Algorithm Integration Framework.
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Apache License v2.0 which accompanies
 * this distribution, and is available at:
 * http://www.apache.org/licenses/LICENSE-2.0.html
 * 
 * Created on Aug 8, 2006 at Indiana University.
 * 
 * Contributors:
 *     Indiana University - 
 * ***************************************************************************/
package org.cishell.templates.wizards.java;

import java.io.File;

import org.cishell.templates.wizards.BasicTemplate;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class NewJavaAlgorithmTemplate extends BasicTemplate {
    WizardNewProjectCreationPage projectPage;

    public NewJavaAlgorithmTemplate() {
        super("java_algorithm");
        
        setPageCount(5);
        //this should go on page 0
        addOption("bundleName", "Bundle Name", "My Algorithm Bundle", 1).setRequired(true);
        addOption("bundleSymbolicName","Bundle Symbolic Name", "org.my.bundle.name", 1).setRequired(true);
        addOption("bundleVersion", "Bundle Version", "0.0.1", 1).setRequired(true);
        
        addOption("algName", "Algorithm Name", "My Algorithm", 2).setRequired(true);
        addOption("algDesc", "Algorithm Description", "This algorithm does this and this...", 2);
        addOption("algClass", "Algorithm Class Name", "MyAlgorithm", 2).setRequired(true);
        addOption("algPkg", "Algorithm Package", "org.my.algorithm", 2).setRequired(true);
        
        addOption("in_data", "Data the algorithm will take in", "file:mime/type or java.lang.ClassName or null", 3).setRequired(true);
        addOption("out_data", "Data the algorithm will produce", "file:mime/type or java.lang.ClassName or null", 3).setRequired(true);
        addOption("remoteable", "Remoteable Algorithm", false, 3);
        addOption("onMenu", "On The Menu", false, 3);
        addOption("menu_path", "Menu Path", "visualization/SubMenu/additions", 3).setEnabled(false);
        
        addOption("useParams", "Requires parameters in addition to the given data", false, 4);
        addOption("shouldHaveParameters", "", "GUI for parameter creation not implemented yet", 4).setEnabled(false);
    }
    
    public void addPages(Wizard wizard) {
        WizardPage page = new WizardNewProjectCreationPage("projectPage");
        page.setTitle("Project Properties");
        page.setDescription("Enter the project name and location");
        wizard.addPage(page);
        projectPage = (WizardNewProjectCreationPage) page;

        page = createPage(1);
        page.setTitle("Bundle Properties");
        page.setDescription("Enter properties for the project as a whole");
        //page.setPageComplete(false);
        wizard.addPage(page);
        
        page = createPage(2);
        page.setTitle("Algorithm Properties");
        page.setDescription("Enter some algorithm information");
        //page.setPageComplete(false);
        wizard.addPage(page);
        
        page = createPage(3);
        page.setTitle("Algorithm Properties");
        page.setDescription("Enter properties of how the algorithm will work");
        //page.setPageComplete(false);
        wizard.addPage(page);
        
        page = createPage(4);
        page.setTitle("Algorithm Parameters");
        page.setDescription("Enter what parameters are needed for the algorithm");
        //page.setPageComplete(false);
        wizard.addPage(page);
        
        markPagesAdded();
    }

    public void execute(IProject project, IPluginModelBase model, IProgressMonitor monitor) throws CoreException {
        setValue("packageName", getValue("algPkg"));
        setValue("algFullClass", getValue("algPkg")+"."+getValue("algClass"));
        setValue("algFactoryFullClass", getValue("algFullClass")+"Factory");

        if (getOption("onMenu").getValue() != Boolean.TRUE) {
            setValue("isOnMenu", "#");
        } else {
            setValue("isOnMenu", "");
        }
        
        super.execute(project, model, monitor);
    }

    protected void updateModel(IProgressMonitor monitor) throws CoreException {}
    
    public void validateOptions(TemplateOption changed) {
        int page = getPageIndex(changed);
        
        switch (page) {
        case 1:
            break;
        case 2:
            break;
        case 3:
            if (changed.getName().equals("onMenu")) {
                if (Boolean.TRUE == changed.getValue()) {
                    getOption("menu_path").setEnabled(true);
                }else {
                    getOption("menu_path").setEnabled(false);
                }
            }
            
            break;
        default:
            break;
        }
    }
    
    /**
     * @see org.eclipse.pde.ui.templates.AbstractTemplateSection#isOkToCreateFolder(java.io.File)
     */
    protected boolean isOkToCreateFolder(File sourceFolder) {
        String name = sourceFolder.getName();
        if ((name.equals("l10n") || name.equals("metatype")) && 
                Boolean.FALSE == getOption("useParams").getValue()) {
            return false;
        } else {
            return super.isOkToCreateFolder(sourceFolder);
        }
    }

    public IProject getProjectHandle() {
        return projectPage.getProjectHandle();
    }
}
