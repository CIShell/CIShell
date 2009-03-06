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

/* 
 * @author Weixia(Bonnie) Huang (huangb@indiana.edu)
 */
public class FileLoad implements Algorithm {

	private final LogService logger;
	private final GUIBuilderService guiBuilder;

	private BundleContext bundleContext;
	private CIShellContext ciShellContext;
	private static String defaultLoadDirectory;

	private final static String FILTER_EXTENSION_ALL = "*";
	private final static String FILTER_AMBIGUOUS = "&(type=validator)(format_name=*)(in_data=file-ext:*)";
	private final static String FILTER_IN_DATA = "&(type=validator)(format_name=*)";

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
			File currentDir = new File(defaultLoadDirectory); // ? good way to
			// do this?
			String absolutePath = currentDir.getAbsolutePath();
			String name = currentDir.getName();
			dialog.setFilterPath(absolutePath);
			// dialog.setFilterPath(name);
			dialog.setText("Select a File");
			String fileName = dialog.open();
			if (fileName == null) {
				return;
			}

			File file = new File(fileName);
			if (file.isDirectory()) {
				defaultLoadDirectory = file.getAbsolutePath();
			} else {

				// File parentFile = file.getParentFile();
				// if (parentFile != null) {
				defaultLoadDirectory = file.getParentFile().getAbsolutePath();
				// }
			}

			String fileExtension = getFileExtension(file).toLowerCase();

			/*
			 * This filter is used to filter out all the services which are NOT,
			 * 1. validators 2. validators but for output file functionality
			 * Hence only input file functionality validators are allowed.
			 */

			try {

				// get all the service references of validators that can load
				// this type of file.

				ServiceReference[] selectedFileServiceReferences = null;
				
				if(fileExtension != null && fileExtension.length() > 0) {
					selectedFileServiceReferences = getApplicableServiceReferences(
							FILTER_IN_DATA, fileExtension);	
				}
				
				/*
				 * This use case is for input files selected that are, 1.
				 * without any file extensions 2. with file extensions that do
				 * not match any service "in_data" field.
				 */

				if ((selectedFileServiceReferences == null || selectedFileServiceReferences.length == 0)) {

					/*
					 * This filter is used to accept only those services which
					 * are, 1. type = validators and, 2. which have non-empty
					 * format_name and, 3. which have non-empty in_data field.
					 * or, 3. which have empty in_data field and non-empty
					 * ambiguous_extension field. This is used so that all the
					 * validators for output file functionality are filtered
					 * out.
					 */

					ServiceReference[] potentialValidators = null;

					/*
					 * This is used to find validators that support ambiguous
					 * extensions for the provided file extension. There are
					 * good chances that the file selected does not have any
					 * file extension, this is handled by below case.
					 */

					if (fileExtension != null && fileExtension.length() > 0) {

						potentialValidators = getApplicableServiceReferences(
								FILTER_AMBIGUOUS, fileExtension);

					}

					/*
					 * If no services are found then provide for all the
					 * validators list.
					 */

					if (potentialValidators == null
							|| potentialValidators.length == 0) {

						potentialValidators = getApplicableServiceReferences(
								FILTER_IN_DATA, FILTER_EXTENSION_ALL);

					}

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
							potentialValidators,
							selectedServicesForLoadedFileList).open();
				}

				/*
				 * This use case is for input files selected that have only one
				 * applicable service. Special case where the file extension
				 * belongs to ambiguous file extension group like csv is also
				 * handled. In the simple case system goes ahead and loads the
				 * file with that service.
				 */

				else if (selectedFileServiceReferences.length == 1) {

					/*
					 * To check for the files with ambiguous file extension a
					 * seperate list of service references is created.
					 */

					ServiceReference[] selectedFileAmbiguousValidators = getApplicableServiceReferences(
							FILTER_AMBIGUOUS, fileExtension);

					/*
					 * If allAmbiguousValidators is not empty then the system
					 * provides a dialog box to select from the available
					 * validators. In this case CSV, NSF & SCOPUS
					 */

					if ((selectedFileAmbiguousValidators != null && selectedFileAmbiguousValidators.length > 0)) {
						new SelectedFileServiceSelector("Load", file, window
								.getShell(), ciShellContext, bundleContext,
								selectedFileAmbiguousValidators,
								selectedServicesForLoadedFileList).open();
					}

					/*
					 * If allAmbiguousValidators is not empty we go forward with
					 * normal work flow of loading the file with selected file
					 * format.
					 */

					else {

						AlgorithmFactory selectedValidatorExecutor = (AlgorithmFactory) bundleContext
						.getService(selectedFileServiceReferences[0]);
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
								selectedServicesForLoadedFileList
								.add(outputDataAfterValidation[i]);
						}
					}

				}

				/*
				 * This use case is for input files selected that have more than
				 * one applicable services. For e.g. ".xml" can be xgmml,
				 * graphml, treeml. This now triggers the
				 * SelectedFileServiceSelector dialog & can be used to select
				 * any one of the available services for that particular file
				 * extension.
				 */

				else if (selectedFileServiceReferences.length > 1) {

					new SelectedFileServiceSelector("Load", file, window
							.getShell(), ciShellContext, bundleContext,
							selectedFileServiceReferences,
							selectedServicesForLoadedFileList).open();

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

		/**
		 * @param selectedFileServiceReferencesFilter
		 * @param fileExtensionManipulations
		 * @return
		 * @throws InvalidSyntaxException
		 */
		private ServiceReference[] getApplicableServiceReferences(
				String selectedFileServiceReferencesFilter,
				String fileExtensionManipulations)
		throws InvalidSyntaxException {

			String appliedValidatorFilter = getValidatorFilter(
					selectedFileServiceReferencesFilter,
					fileExtensionManipulations);

			ServiceReference[] selectedFileServiceReferences = bundleContext
			.getAllServiceReferences(AlgorithmFactory.class.getName(),
					appliedValidatorFilter);
			
			return selectedFileServiceReferences;

		}

		private String getValidatorFilter(
				String selectedFileServiceReferencesFilter,
				String fileExtensionManipulations) {

			if (selectedFileServiceReferencesFilter.equals(FILTER_IN_DATA)) {
				return "(" + selectedFileServiceReferencesFilter
				+ "(in_data=file-ext:" + fileExtensionManipulations
				+ ")" +
				")";

			} else if (selectedFileServiceReferencesFilter.equals(FILTER_AMBIGUOUS)) {
				return "(" + selectedFileServiceReferencesFilter
				+ "(ambiguous_extension=" + fileExtensionManipulations
				+ ")" +
				")";
			}

			return null;
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