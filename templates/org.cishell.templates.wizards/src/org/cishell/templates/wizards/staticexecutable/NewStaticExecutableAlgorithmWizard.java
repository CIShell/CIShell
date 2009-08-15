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
package org.cishell.templates.wizards.staticexecutable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;

import org.cishell.templates.staticexecutable.optiontypes.PlatformOption;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
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
import org.eclipse.pde.ui.templates.TemplateOption;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
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


public class NewStaticExecutableAlgorithmWizard extends NewPluginTemplateWizard
        implements IWorkbenchWizard {
	public static final String DEFAULT_LABEL = "Common to All";
	public static final String DEFAULT_PATH = "/default/";
	
	public static final String LINUX_X86_32_LABEL = "Linux x86 (32 bit)";
	public static final String LINUX_X86_32_PATH = "/linux.x86/";
	
	public static final String LINUX_X86_64_LABEL = "Linux x86 (64 bit)";
	public static final String LINUX_X86_64_PATH = "/linux.x86_64/";
	
	public static final String MAC_OSX_PPC_LABEL = "Mac OSX PPC";
	public static final String MAC_OSX_PPC_PATH = "/macosx.ppc/";
	
	public static final String MAC_OSX_X86_LABEL = "Mac OSX x86";
	public static final String MAC_OSX_X86_PATH = "/macosx.x86/";
	
	public static final String SOLARIS_SPARC_LABEL = "Solaris Sparc";
	public static final String SOLARIS_SPARC_PATH = "/solaris.sparc/";
	
	public static final String WIN_32_LABEL = "Windows (32 bit)";
	public static final String WIN_32_PATH = "/win32/";
	
	public static final String[] PLATFORM_LABELS = new String[] {
		DEFAULT_LABEL,
		WIN_32_LABEL,
		MAC_OSX_X86_LABEL,
		MAC_OSX_PPC_LABEL,
		LINUX_X86_32_LABEL,
		LINUX_X86_64_LABEL,
		SOLARIS_SPARC_LABEL
	};
	
	public static final String[] PLATFORM_PATHS = new String[] {
		DEFAULT_PATH,
		WIN_32_PATH,
		MAC_OSX_X86_PATH,
		MAC_OSX_PPC_PATH,
		LINUX_X86_32_PATH,
		LINUX_X86_64_PATH,
		SOLARIS_SPARC_PATH
	};

    NewStaticExecutableAlgorithmTemplate template;
    
    /**
     * @see org.eclipse.pde.ui.templates.NewPluginTemplateWizard#createTemplateSections()
     */
    public ITemplateSection[] createTemplateSections() {
        template = new NewStaticExecutableAlgorithmTemplate();
        
        return new ITemplateSection[] { template };
    }

    public boolean performFinish() {
    	
    	/*
    	 * Prepare all the files necessary to call the 3-argument version of
    	 * performFinish, which executes each of the templates we provided in
    	 * the "createTemplateSections()" method above.
    	 */
    	
        final IProject project = template.getProjectHandle();
        final IProjectDescription description =
        	ResourcesPlugin.getWorkspace().newProjectDescription(
        		project.getName());
        
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            protected void execute(IProgressMonitor monitor)
            		throws CoreException,
            			   InvocationTargetException,
            			   InterruptedException {
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
                
                for (int ii = 0; ii < PLATFORM_LABELS.length; ii++) {
                	String directoryPath = "ALGORITHM" + PLATFORM_PATHS[ii];
                	
                	if (ii != 0) {
                		PlatformOption executableFileOption =
                			template.getExecutableFileOption(
                				PLATFORM_LABELS[ii]);
                		copyTemplateOptionFile(
                			executableFileOption, directoryPath, project);
                	}
                	
                	PlatformOption[] relatedFileOptions =
                		template.getRelatedFileOptions(PLATFORM_LABELS[ii]);
                	
                	for (int jj = 0; jj < relatedFileOptions.length; jj++) {
                		copyTemplateOptionFile(
                			relatedFileOptions[jj], directoryPath, project);
                	}
                }
                
                String sourceCodeDirectoryPath = "src/";
                TemplateOption sourceCodeFilesTemplateOption =
                	template.getSourceCodeFilesTemplateOption();
                copyTemplateOptionFile(sourceCodeFilesTemplateOption,
                					   sourceCodeDirectoryPath,
                					   project);
                
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
        
        //display the README file to the user in Eclipse
        
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
    public void init(IWorkbench workbench, IStructuredSelection selection) {}
    
    private void copyTemplateOptionFile(TemplateOption templateOption,
    									String directoryPath,
    									IProject project)
    		throws CoreException {
    	String sourceFilePath = templateOption.getValue().toString();
    	
    	if (sourceFilePath == null || "".equals(sourceFilePath)) {
    		return;
    	}
    	                	
    	File sourceFile = new File(sourceFilePath);
    	String targetFilePath = directoryPath + sourceFile.getName();
    	File targetFile =
    		project.getLocation().append(targetFilePath).toFile();
    	
    	copyFile(sourceFile, targetFile);
    	
    	project.refreshLocal(IResource.DEPTH_INFINITE, null);
    }
    
    private void copyFile(File sourceFile, File targetFile) {
    	FileInputStream sourceFileStream;
    	FileOutputStream targetFileStream;
    	
    	try {
    		sourceFileStream = new FileInputStream(sourceFile);
    		targetFileStream = new FileOutputStream(targetFile);
    		byte[] buffer = new byte[4096];
    		int bytesRead = sourceFileStream.read(buffer);
    		
    		while (bytesRead != -1) {
    			targetFileStream.write(buffer, 0, bytesRead);
    			bytesRead = sourceFileStream.read(buffer);
    		}
    		
    		sourceFileStream.close();
    		targetFileStream.close();
    	} catch (Exception exception) {
    		MessageBox messageBox = new MessageBox(new Shell(new Display()), SWT.OK);
    		messageBox.setMessage(exception.toString());
    		messageBox.open();
    		
    		throw new RuntimeException(exception);
    	}
    }
}