package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.cishell.service.guibuilder.GUIBuilderService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class FileLoad implements Algorithm {

	private final LogService logger;

	private BundleContext bundleContext;
	private CIShellContext ciShellContext;
	private static String defaultLoadDirectory;

	public FileLoad(CIShellContext ciContext, BundleContext bContext,
			Dictionary prefProperties) {
		this.ciShellContext = ciContext;
		this.bundleContext = bContext;
		logger = (LogService) ciContext.getService(LogService.class.getName());

		// unpack preference properties
		if (defaultLoadDirectory == null) {
			defaultLoadDirectory = (String) prefProperties.get("loadDir");
		}
	}

	public Data[] execute() throws AlgorithmExecutionException {
		// prepare to run load dialog in GUI thread

		IWorkbenchWindow window = getFirstWorkbenchWindow();
		Display display = PlatformUI.getWorkbench().getDisplay();
		FileLoadRunnable fileLoader = new FileLoadRunnable(window);

		// run load dialog in gui thread.

		if (Thread.currentThread() != display.getThread()) {
			display.syncExec(fileLoader);
		} else {
			fileLoader.run();
		}

		// return loaded file data

		Data[] loadedFileData = extractLoadedFileData(fileLoader);
		return loadedFileData;
	}

	final class FileLoadRunnable implements Runnable {
		boolean loadFileSuccess = false;
		IWorkbenchWindow window;
		
		// this is how we return values from the runnable
		public ArrayList loadedFiles_ReturnParameter = new ArrayList();

		FileLoadRunnable(IWorkbenchWindow window) {
			this.window = window;
		}

		/*
		 * Let the user chose which file to load,
		 * Let the user choose the file type (if it is ambiguous),
		 * and then actually load and validate the file.
		 */
		public void run() {
			try {
				// Prepare to ask the user which file to load.

				FileDialog dialog = new FileDialog(window.getShell(), SWT.OPEN);
				File currentDir = new File(defaultLoadDirectory);
				String absolutePath = currentDir.getAbsolutePath();
				dialog.setFilterPath(absolutePath);
				dialog.setText("Select a File");

				// Determine which file to load.

				String nameOfFileToLoad = dialog.open();
				if (nameOfFileToLoad == null) {
					return;
				}

				// Actually load the file.

				File file = new File(nameOfFileToLoad);

				if (file.isDirectory()) {
					defaultLoadDirectory = file.getAbsolutePath();
				} else {
					defaultLoadDirectory = file.getParentFile().getAbsolutePath();
				}

				//Validate the loaded file, "casting" it to a certain MIME type.
				
				// Extract the file's file extension.

				String fileExtension = getFileExtension(file).toLowerCase();

				// Get all the validators which support this file extension...

				ServiceReference[] supportingValidators = getSupportingValidators(fileExtension);

				// If there are no supporting validators...
				if (supportingValidators.length == 0) {
					// Let the user choose from all the validators available.

					ServiceReference[] allValidators = getAllValidators();

					new FileFormatSelector("Load", file, window.getShell(),
							ciShellContext, bundleContext, allValidators,
							loadedFiles_ReturnParameter).open();
				}

				// If there is just one supporting validator...
				if (supportingValidators.length == 1) {
					// Just use that validator to validate the file.
					
					ServiceReference onlyPossibleValidator = supportingValidators[0];
					AlgorithmFactory selectedValidatorExecutor = (AlgorithmFactory) bundleContext
					.getService(onlyPossibleValidator);
					Data[] outputDataAfterValidation;
					Data[] inputDataForValidation = new Data[] { new BasicData(
							file.getPath(), String.class.getName()) };
					outputDataAfterValidation = selectedValidatorExecutor
					.createAlgorithm(inputDataForValidation, null,
							ciShellContext).execute();

					/*
					 * outputDataAfterValidation = null implies that file
					 * was not loaded properly.
					 */

					if (outputDataAfterValidation != null) {
						loadFileSuccess = true;
						logger.log(LogService.LOG_INFO, "Loaded: "
								+ file.getPath());
						for (int i = 0; i < outputDataAfterValidation.length; i++)
							loadedFiles_ReturnParameter.
							add(outputDataAfterValidation[i]);
					}
				}

				// If there is more than one supporting validator...
				if (supportingValidators.length > 1) {
					// Let the user choose which validator they want to use.

					new FileFormatSelector("Load", file, window.getShell(),
							ciShellContext, bundleContext, supportingValidators,
							loadedFiles_ReturnParameter).open();
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		private ServiceReference[] getSupportingValidators(String fileExtension) {
			try {
				String ldapQuery = "(& (type=validator)" +
				"(|" +
				  "(in_data=file-ext:" + fileExtension + ")" +
				  "(also_validates=" + fileExtension + ")" + 
				 "))";
				 
				ServiceReference[] supportingValidators = 
					bundleContext.getAllServiceReferences(
							AlgorithmFactory.class.getName(),
							ldapQuery);
						
				
				if (supportingValidators == null) {
					//(better to return a list of length zero than null)
					supportingValidators = new ServiceReference[]{};
				}
				
				return supportingValidators;
				
				
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
				return new ServiceReference[]{};	
			}
		}
		
		private ServiceReference[] getAllValidators() {
			try {
				ServiceReference[] allValidators = 
					bundleContext.getAllServiceReferences(
						AlgorithmFactory.class.getName(),
						"(&(type=validator)(in_data=file-ext:*))");
				
				if (allValidators == null) {
					//(better to return a list of length zero than null)
					allValidators = new ServiceReference[]{};
				}
				
				return allValidators;
				
			} catch (InvalidSyntaxException e) {
				e.printStackTrace();
				return new ServiceReference[]{};	
			}
		}
		
		public String getFileExtension(File theFile) {
			String fileName = theFile.getName();
			String extension;
			if (fileName.lastIndexOf(".") != -1)
				extension = fileName.substring(fileName.lastIndexOf(".") + 1);
			else
				extension = "";
			return extension;
		}
	} 

	private IWorkbenchWindow getFirstWorkbenchWindow()
	throws AlgorithmExecutionException {
		final IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
		.getWorkbenchWindows();
		if (windows.length == 0) {
			throw new AlgorithmExecutionException(
					"Cannot obtain workbench window needed to open dialog.");
		} else {
			return windows[0];
		}
	}

	private Data[] extractLoadedFileData(FileLoadRunnable dataUpdater)
	throws AlgorithmExecutionException {
		Data[] loadedFileData;
		try {
			if (!dataUpdater.loadedFiles_ReturnParameter.isEmpty()) {
				int size = dataUpdater.loadedFiles_ReturnParameter.size();
				loadedFileData = new Data[size];
				for (int index = 0; index < size; index++) {
					loadedFileData[index] = (Data) dataUpdater.loadedFiles_ReturnParameter
					.get(index);
				}
				return loadedFileData;
			} else {
				this.logger
				.log(LogService.LOG_WARNING, "File loading canceled");
				return new Data[0];
			}
		} catch (Throwable e2) {
			throw new AlgorithmExecutionException(e2);
		}
	}
}