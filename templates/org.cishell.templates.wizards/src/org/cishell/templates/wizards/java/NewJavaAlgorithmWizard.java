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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.pde.internal.ui.wizards.IProjectProvider;
import org.eclipse.pde.internal.ui.wizards.plugin.AbstractFieldData;
import org.eclipse.pde.internal.ui.wizards.plugin.NewProjectCreationOperation;
import org.eclipse.pde.ui.IFieldData;
import org.eclipse.pde.ui.IPluginFieldData;
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

public class NewJavaAlgorithmWizard extends NewPluginTemplateWizard implements IWorkbenchWizard {
    NewJavaAlgorithmTemplate template;

    /**
     * @see org.eclipse.pde.ui.templates.NewPluginTemplateWizard#createTemplateSections()
     */
    public ITemplateSection[] createTemplateSections() {
        template = new NewJavaAlgorithmTemplate();
        return new ITemplateSection[]{template};
    }
    
    public boolean performFinish() {
        final IFieldData data = new AlgorithmData();
        final IProjectProvider provider = new AlgorithmProjectProvider();
        final WorkspaceModifyOperation op = new NewProjectCreationOperation(data, provider, this);
        
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    monitor.beginTask("Creating Algorithm Project...", 150);
                    
                    try {
                        op.run(new SubProgressMonitor(monitor, 100));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    
                    File outFile = provider.getProject().getLocation().append(
                    "META-INF/MANIFEST.MF").toFile();
                    try {
                        PrintWriter out = new PrintWriter(new FileWriter(outFile,true));
                        out.println("X-AutoStart: true");
                        out.println("Service-Component: OSGI-INF/component.xml");
                        out.close();
                        
                        provider.getProject().refreshLocal(IResource.DEPTH_INFINITE, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                    monitor.worked(50);
                    monitor.done();
                }
            });            
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        final IFile file = provider.getProject().getFile("META-INF/MANIFEST.MF");
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
     * @see org.eclipse.pde.ui.IBundleContentWizard#getImportPackages()
     */
    public String[] getImportPackages() {
        return new String[]{
                "org.cishell.framework;version=\"1.0.0\"",
                "org.cishell.framework.algorithm;version=\"1.0.0\"",
                "org.cishell.framework.data;version=\"1.0.0\"",
                "org.cishell.framework.userprefs;version=\"1.0.0\"",
                "org.osgi.framework;version=\"1.3.0\"",
                "org.osgi.service.component;version=\"1.0.0\"",
                "org.osgi.service.log;version=\"1.3.0\"",
                "org.osgi.service.metatype;version=\"1.1.0\"",
                "org.osgi.service.prefs;version=\"1.1.0\"",
                "org.osgi.service.cm;version=\"1.2.0\""
        };
    }

    /**
     * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }
    
    private class AlgorithmProjectProvider implements IProjectProvider {
        IProject project = template.getProjectHandle();
        
        public IPath getLocationPath() {
            return project.getLocation();
        }

        public IProject getProject() {
            return project;
        }

        public String getProjectName() {
            return project.getName();
        }
    }
    
    private class AlgorithmData extends AbstractFieldData implements IPluginFieldData {

        public boolean doGenerateClass() {
            return false;
        }

        public String getClassname() {
            return get("algFullClass");
        }

        public boolean isUIPlugin() {
            return false;
        }

        public String getId() {
            return get("bundleSymbolicName");
        }

        public String getLibraryName() {
            return ".";
        }

        public String getName() {
            return get("bundleName");
        }

        public String getOutputFolderName() {
            return "build";
        }

        public String getProvider() {
            return "";
        }

        public String getSourceFolderName() {
            return "src";
        }

        public String getVersion() {
            return get("bundleVersion");
        }
        
        public String getOSGiFramework() {
            return "standard";
        }
        
        public String getTargetVersion() {
            return "3.2";
        }

        public boolean hasBundleStructure() {
            return true;
        }

        public boolean isLegacy() {
            return false;
        }

        public boolean isSimple() {
            return false;
        }
        
        protected String get(String key) {
            return (String)template.getValue(key);
        }
    }
}
