package org.cishell.reference.app.service.fileloader;

import java.io.File;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public final class ValidatorSelectorRunnable implements Runnable {
	private IWorkbenchWindow window;
	private BundleContext bundleContext;

	private File file;
	private AlgorithmFactory chosenValidator;

	public ValidatorSelectorRunnable(
			IWorkbenchWindow window, BundleContext bundleContext, File file) {
		this.window = window;
		this.bundleContext = bundleContext;
		this.file = file;
		
	}

	public AlgorithmFactory getValidator() {
		return this.chosenValidator;
	}

	public void run() {
		String fileExtension =
			getFileExtension(this.file.getAbsolutePath()).toLowerCase().substring(1);

		ServiceReference[] supportingValidators =
			getSupportingValidators(this.bundleContext, fileExtension);

		// If there are no supporting validators...
		if (supportingValidators.length == 0) {
			// Let the user choose from all the validators available.

			ServiceReference[] allValidators = getAllValidators(this.bundleContext);

			FileFormatSelector validatorSelector = new FileFormatSelector(
				"Load", window.getShell(), this.bundleContext, allValidators, this.file);
			validatorSelector.open();
			this.chosenValidator = validatorSelector.getValidator();
		} else if (supportingValidators.length == 1) {
			ServiceReference onlyPossibleValidator = supportingValidators[0];
			this.chosenValidator =
				(AlgorithmFactory)this.bundleContext.getService(onlyPossibleValidator);
		}

		if (supportingValidators.length > 1) {
			FileFormatSelector validatorSelector = new FileFormatSelector(
				"Load", window.getShell(), this.bundleContext, supportingValidators, this.file);
			validatorSelector.open();
			this.chosenValidator = validatorSelector.getValidator();
		}
	}

	public static ServiceReference[] getAllValidators(BundleContext bundleContext) {
		try {
			String validatorsQuery = "(&(type=validator)(in_data=file-ext:*))";
			ServiceReference[] allValidators = bundleContext.getAllServiceReferences(
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

	public static ServiceReference[] getSupportingValidators(
			BundleContext bundleContext, String fileExtension) {
		try {
			String validatorsQuery =
				"(& (type=validator)" +
				"(|" +
					"(in_data=file-ext:" + fileExtension + ")" +
					"(also_validates=" + fileExtension + ")" + 
				"))";
			 
			ServiceReference[] supportingValidators = bundleContext.getAllServiceReferences(
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

	// TODO: Copied out of org.cishell.utilities.FileUtilities because of circular dependencies.
	private static String getFileExtension(String filePath) {
    	int periodPosition = filePath.lastIndexOf(".");
    	
    	if ((periodPosition != -1) && ((periodPosition + 1) < filePath.length())) {
    		return filePath.substring(periodPosition);
    	} else {
    		return "";
    	}
    }
}