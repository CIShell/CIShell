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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.pde.core.plugin.IPluginBase;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.internal.core.bundle.BundlePluginBase;
import org.eclipse.pde.internal.core.bundle.BundlePluginModelBase;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ISetSelectionTarget;

/**
 * 
 * @author Bruce Herr (bh2@bh2.net)
 */
public class NewDatasetWizard extends NewPluginTemplateWizard
        implements IWorkbenchWizard {
    NewDatasetTemplate template;
    
    /**
     * @see org.eclipse.pde.ui.templates.NewPluginTemplateWizard#createTemplateSections()
     */
    public ITemplateSection[] createTemplateSections() {
        template = new NewDatasetTemplate();
        return new ITemplateSection[]{template};
    }

    public boolean performFinish() {
        final IProject project = template.getProjectHandle();
        final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());
        
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            protected void execute(IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
                monitor.beginTask("", 2500);
                project.create(description, monitor);
                project.open(monitor);
                
                IPluginModelBase model = new BundlePluginModelBase(){
                    private static final long serialVersionUID = 1L;

                    public IPluginBase createPluginBase() {
                        return new BundlePluginBase();
                    }

                    public boolean isFragmentModel() {
                        return false;
                    }};
                
                performFinish(project, model, monitor);
                
                monitor.done();
            }
        };
        
        try {
            getContainer().run(true, true, op);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        final IFile file = template.getProjectHandle().getFile("README.txt");
        final IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        final IWorkbenchPage page = ww.getActivePage();
        if (page != null) {
            final IWorkbenchPart focusPart = page.getActivePart();
            ww.getShell().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    if (focusPart instanceof ISetSelectionTarget) {
                        ISelection selection = new StructuredSelection(file);
                        ((ISetSelectionTarget) focusPart).selectReveal(selection);
                    }
                    try {
                        IDE.openEditor(page, file, true);
                    } catch (PartInitException e) {
                    }
                }
            });
        }
        
        return true;
    }
    
    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        
    }
}
