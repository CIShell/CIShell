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

import org.cishell.templates.wizards.BasicTemplate;
import org.cishell.templates.wizards.pages.ParameterListBuilderPage;
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
    ParameterListBuilderPage builderPage;
    String[][] groupChoices = new String[][]{
            {"start", "Beginning of the menu"},
            {"additions", "Anywhere"},
            {"end", "End of the menu"}};

    public NewJavaAlgorithmTemplate() {
        super("java_algorithm");
        
        setPageCount(5);
        //this should go on page 0
        addOption("bundleName", "Bundle Name", "My Algorithm Bundle", 1).setRequired(true);
        addOption("bundleSymbolicName","Bundle Symbolic Name", "org.my.algorithm", 1).setRequired(true);
        addOption("bundleVersion", "Bundle Version", "0.0.1", 1).setRequired(true);
        
        addOption("algName", "Algorithm Name", "My Algorithm", 2).setRequired(true);
        addOption("algDesc", "Algorithm Description", "This algorithm does this and this...", 2);
        addOption("algClass", "Algorithm Class Name", "MyAlgorithm", 2).setRequired(true);
        addOption("algPkg", "Algorithm Package", "org.my.algorithm", 2).setRequired(true);
        
        addOption("in_data", "Data the algorithm will take in", "file:mime/type or java.lang.ClassName or null", 3).setRequired(true);
        addOption("out_data", "Data the algorithm will produce", "file:mime/type or java.lang.ClassName or null", 3).setRequired(true);
        addOption("remoteable", "Remoteable Algorithm", false, 3);
        addOption("onMenu", "On the menu", false, 3);
        addOption("menu_path", "Menu path", "Visualization/SubMenu", 3).setEnabled(false);
        addOption("menu_group", "Menu item placement", groupChoices, "Anywhere", 3).setEnabled(false);
    }
    
    public void addPages(Wizard wizard) {
        projectPage = new WizardNewProjectCreationPage("createProjectPage");
        WizardPage page = projectPage;
        page.setTitle("Project Properties");
        page.setDescription("Enter the project name and location");
        wizard.addPage(page);

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
        
        builderPage = new ParameterListBuilderPage("builderPage");
        page = builderPage;
        page.setTitle("Algorithm Parameters");
        page.setDescription("Enter what extra parameters are needed for the algorithm");
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
        
        String menuPath = (String)getValue("menu_path");
        if (!menuPath.endsWith("/")) {
            menuPath += "/";
        }
        
        String choice = (String)getOption("menu_group").getValue();

        for (int i=0; i < groupChoices.length; i++) {
            if (groupChoices[i][1].equals(choice)) {
                menuPath += groupChoices[i][0];
                break;
            }
            if (groupChoices[i][0].equals(choice)) {
                menuPath += groupChoices[i][0];
                break;
            }
            
        }
        
        setValue("full_menu_path", menuPath);
        setValue("useParams", Boolean.TRUE);
        setValue("attributeDefinitions", builderPage.toOutputString());
        
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
                    getOption("menu_group").setEnabled(true);
                } else {
                    getOption("menu_path").setEnabled(false);
                    getOption("menu_group").setEnabled(false);
                }
            }
            
            break;
        default:
            break;
        }
    }

    public IProject getProjectHandle() {
        return projectPage.getProjectHandle();
    }
}
