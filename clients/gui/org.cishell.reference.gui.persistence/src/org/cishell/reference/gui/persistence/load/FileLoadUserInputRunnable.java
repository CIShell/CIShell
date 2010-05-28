package org.cishell.reference.gui.persistence.load;

import java.io.File;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.utilities.FileUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public final class FileLoadUserInputRunnable implements Runnable {
	private IWorkbenchWindow window;
	private BundleContext bundleContext;
	private CIShellContext ciShellContext;
	private File file;
	private AlgorithmFactory validator;

	public FileLoadUserInputRunnable(
			IWorkbenchWindow window, BundleContext bundleContext, CIShellContext ciShellContext) {
		this.window = window;
		this.bundleContext = bundleContext;
		this.ciShellContext = ciShellContext;
	}

	public File getFile() {
		return this.file;
	}

	public AlgorithmFactory getValidator() {
		return this.validator;
	}

	public void run() {
		this.file = getFileFromUser();

		if (file == null) {
			return;
		} else if (this.file.isDirectory()) {
			FileLoadAlgorithm.defaultLoadDirectory = this.file.getAbsolutePath();
		} else {
			FileLoadAlgorithm.defaultLoadDirectory = this.file.getParentFile().getAbsolutePath();
		}

		// Validate the loaded file, "casting" it to a certain MIME type.

		// Extract the file's file extension.

		String fileExtension =
			FileUtilities.getFileExtension(this.file).toLowerCase().substring(1);

		// TODO split here?
		
		// Get all the validators which support this file extension...

		ServiceReference[] supportingValidators = getSupportingValidators(fileExtension);

		// If there are no supporting validators...
		if (supportingValidators.length == 0) {
			// Let the user choose from all the validators available.

			ServiceReference[] allValidators = getAllValidators();

			FileFormatSelector validatorSelector = new FileFormatSelector(
				"Load", window.getShell(), this.bundleContext, allValidators);
			validatorSelector.open();
			this.validator = validatorSelector.getValidator();
		} else if (supportingValidators.length == 1) {
			ServiceReference onlyPossibleValidator = supportingValidators[0];
			this.validator =
				(AlgorithmFactory)this.bundleContext.getService(onlyPossibleValidator);
		}

		if (supportingValidators.length > 1) {
			FileFormatSelector validatorSelector = new FileFormatSelector(
				"Load", window.getShell(), this.bundleContext, supportingValidators);
			validatorSelector.open();
			this.validator = validatorSelector.getValidator();
		}
	}

	private File getFileFromUser() {
		FileDialog fileDialog = createFileDialog();
		String fileName = fileDialog.open();

		if (fileName == null) {
			return null;
		} else {
			return new File(fileName);
		}
	}

	private FileDialog createFileDialog() {
		File currentDirectory = new File(FileLoadAlgorithm.defaultLoadDirectory);
		String absolutePath = currentDirectory.getAbsolutePath();
		FileDialog fileDialog = new FileDialog(this.window.getShell(), SWT.OPEN);
		fileDialog.setFilterPath(absolutePath);
		fileDialog.setText("Select a File");

		return fileDialog;
	}

	private ServiceReference[] getSupportingValidators(String fileExtension) {
		try {
			String validatorsQuery =
				"(& (type=validator)" +
				"(|" +
					"(in_data=file-ext:" + fileExtension + ")" +
					"(also_validates=" + fileExtension + ")" + 
				"))";
			 
			ServiceReference[] supportingValidators = this.bundleContext.getAllServiceReferences(
				AlgorithmFactory.class.getName(), validatorsQuery);
			
			if (supportingValidators == null) {
				return new ServiceReference[0];
			} else {
				return supportingValidators;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();

			return new ServiceReference[]{};	
		}
	}

	private ServiceReference[] getAllValidators() {
		try {
			String validatorsQuery = "(&(type=validator)(in_data=file-ext:*))";
			ServiceReference[] allValidators = this.bundleContext.getAllServiceReferences(
				AlgorithmFactory.class.getName(), validatorsQuery);
			
			if (allValidators == null) {
				return new ServiceReference[0];
			} else {
				return allValidators;
			}
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();

			return new ServiceReference[0];	
		}
	}
}