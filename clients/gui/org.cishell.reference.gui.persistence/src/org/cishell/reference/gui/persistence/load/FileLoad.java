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
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/* 
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu)
 */
public class FileLoad implements Algorithm {

	private final LogService logger;
	private final GUIBuilderService guiBuilder;

	private BundleContext bundleContext;
	private CIShellContext ciShellContext;
	private static String defaultLoadDirectory;

	public FileLoad(CIShellContext ciContext, BundleContext bContext,
			Dictionary prefProperties) {
		this.ciShellContext = ciContext;
		this.bundleContext = bContext;
		logger = (LogService) ciContext.getService(LogService.class.getName());
		guiBuilder = (GUIBuilderService) ciContext
				.getService(GUIBuilderService.class.getName());

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
		public ArrayList selectedServicesForLoadedFileList = new ArrayList();

		FileLoadRunnable(IWorkbenchWindow window) {
			this.window = window;
		}

		public void run() {
			FileDialog dialog = new FileDialog(window.getShell(), SWT.OPEN);
			// if (currentDir == null) {
			// currentDir = new
			// File(System.getProperty("osgi.install.area").replace("file:","")
			// + "sampledata");
			//                    
			// if (!currentDir.exists()) {
			// currentDir = new
			// File(System.getProperty("osgi.install.area").replace("file:","")
			// + "sampledata" +File.separator + "anything");
			// }
			// }
			System.err.println("defaultLoadDirectory is "
					+ defaultLoadDirectory);
			File currentDir = new File(defaultLoadDirectory); // ? good way to
																// do this?
			String absolutePath = currentDir.getAbsolutePath();
			System.err.println("absolutePath:" + absolutePath);
			String name = currentDir.getName();
			System.err.println("name:" + name);
			dialog.setFilterPath(absolutePath);
			// dialog.setFilterPath(name);
			dialog.setText("Select a File");
			String fileName = dialog.open();
			System.out.println("Resulting file name!:" + fileName);
			if (fileName == null) {
				return;
			}

			File file = new File(fileName);
			if (file.isDirectory()) {
				System.out.println("directory");
				defaultLoadDirectory = file.getAbsolutePath();
			} else {

				// File parentFile = file.getParentFile();
				// if (parentFile != null) {
				System.out.println("file");
				defaultLoadDirectory = file.getParentFile().getAbsolutePath();
				// }
			}

			String fileExtension = getFileExtension(file).toLowerCase();

			/*
			 * This filter is used to filter out all the services which are not,
			 * 1. validators 2. validators but for output file functionality
			 * Hence only input file functionality validators are allowed.
			 */

			String preLoadValidatorFilter = "(&(type=validator)(in_data=file-ext:"
					+ fileExtension + "))";
			try {

				// get all the service references of validators that can load
				// this type of file.
				ServiceReference[] serviceReferences = bundleContext
						.getAllServiceReferences(AlgorithmFactory.class
								.getName(), preLoadValidatorFilter);

				/*
				 * This use case is for input files selected that are, 1.
				 * without any file extensions 2. with file extensions that do
				 * not match any service "in_data" field.
				 */

				if (serviceReferences == null || serviceReferences.length == 0) {

					/*
					 * This filter is used to accept only those services which
					 * are, 1. type = validators and, 2. which have non-empty
					 * format_name and, 3. which have non-empty in_data field.
					 * this is used so that all the validators for output file
					 * functionality are filtered out.
					 */

					String validatorFilter = "(&(type=validator)(format_name=*)(in_data=file-ext:*))";

					ServiceReference[] allValidators = bundleContext
							.getAllServiceReferences(AlgorithmFactory.class
									.getName(), validatorFilter);

					/*
					 * SelectedFileServiceSelector is used to create a GUI for
					 * selecting a service for the selected file from a list of
					 * applicable services. On selection of a service it calls
					 * the validator for that service. If the validator passes
					 * it it goes ahead and loads the file appropriately else it
					 * throws error message asking the user to select other
					 * service.
					 * 
					 * This modifies the selectedServicesForLoadedFileList,
					 * which is the placeholder for all the verified/ applicable
					 * services for a selected/loaded file.
					 */

					new SelectedFileServiceSelector("Load", file, window
							.getShell(), ciShellContext, bundleContext,
							allValidators, selectedServicesForLoadedFileList)
							.open();
				}

				/*
				 * This use case is for input files selected that have only one
				 * applicable service. In this case the system goes ahead and
				 * loads the file with that service.
				 */

				else if (serviceReferences.length == 1) {
					AlgorithmFactory selectedValidatorExecutor = (AlgorithmFactory) bundleContext
							.getService(serviceReferences[0]);
					Data[] outputDataAfterValidation;
					Data[] inputDataForValidation = new Data[] { new BasicData(
							file.getPath(), String.class.getName()) };
					outputDataAfterValidation = selectedValidatorExecutor
							.createAlgorithm(inputDataForValidation, null,
									ciShellContext).execute();

					/*
					 * outputDataAfterValidation = null implies that file was
					 * not loaded properly.
					 */

					if (outputDataAfterValidation != null) {
						loadFileSuccess = true;
						logger.log(LogService.LOG_INFO, "Loaded: "
								+ file.getPath());
						for (int i = 0; i < outputDataAfterValidation.length; i++)
							selectedServicesForLoadedFileList
									.add(outputDataAfterValidation[i]);
					}

				}

				/*
				 * This use case is for input files selected that have more than
				 * one applicable services. For e.g. ".xml" can be xgmml,
				 * graphml, treeml. TODO: Right now this is handled by going
				 * over all the selected services and loading the file with a
				 * service that did not fail the validation test. For e.g. for a
				 * ".xml" file which is treeml it will try out first graphml
				 * then treeml. This will throw error on graphml which can be
				 * resolved. A better approach will be to display
				 * SelectedFileServiceSelector with graphml, treeml & xgmml
				 * options.
				 */

				else if (serviceReferences.length > 1) {
					for (int index = 0; index < serviceReferences.length; index++) {
						Data[] outputDataAfterValidation;
						AlgorithmFactory selectedValidatorExecutor = (AlgorithmFactory) bundleContext
								.getService(serviceReferences[index]);
						Data[] inputDataForValidation = new Data[] { new BasicData(
								file.getPath(), String.class.getName()) };
						outputDataAfterValidation = selectedValidatorExecutor
								.createAlgorithm(inputDataForValidation, null,
										ciShellContext).execute();
						if (outputDataAfterValidation != null) {
							loadFileSuccess = true;
							logger.log(LogService.LOG_INFO, "Loaded: "
									+ file.getPath());
							for (int i = 0; i < outputDataAfterValidation.length; i++) {
								selectedServicesForLoadedFileList
										.add(outputDataAfterValidation[i]);
							}
							break;
						}
					}

				}
				/*
				 * Bonnie: I commented out the following functions since when
				 * the application failed to load an nwb file, etc, the reader
				 * has report the error. It does not need this second error
				 * display. But maybe not all file readers will generate the
				 * error display if a failure occurs...
				 */
				/*
				 * if (serviceRefList != null){ if(serviceRefList.length >0 &&
				 * !loadFileSuccess){
				 * guiBuilder.showError("Can Not Load The File",
				 * "Sorry, it's very possible that you have a wrong file format,"
				 * + "since the file can not be loaded to the application.",
				 * 
				 * "Please check Data Formats that this application can support at "
				 * +
				 * "https://nwb.slis.indiana.edu/community/?n=Algorithms.HomePage."
				 * + "And send your requests or report the problem to "+
				 * "cishell-developers@lists.sourceforge.net. \n"+"Thank you.");
				 * }
				 * 
				 * }
				 */

			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}// end run()

		public String getFileExtension(File theFile) {
			String fileName = theFile.getName();
			String extension;
			if (fileName.lastIndexOf(".") != -1)
				extension = fileName.substring(fileName.lastIndexOf(".") + 1);
			else
				extension = "";
			return extension;
		}
	} // end class

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
			if (!dataUpdater.selectedServicesForLoadedFileList.isEmpty()) {
				int size = dataUpdater.selectedServicesForLoadedFileList.size();
				loadedFileData = new Data[size];
				for (int index = 0; index < size; index++) {
					loadedFileData[index] = (Data) dataUpdater.selectedServicesForLoadedFileList
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