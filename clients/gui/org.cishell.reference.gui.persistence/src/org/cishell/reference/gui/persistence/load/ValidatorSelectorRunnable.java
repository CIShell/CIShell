package org.cishell.reference.gui.persistence.load;

import java.io.File;

import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.utilities.FileUtilities;
import org.eclipse.ui.IWorkbenchWindow;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public final class ValidatorSelectorRunnable implements Runnable {
	private IWorkbenchWindow window;
	private BundleContext bundleContext;

	private File file;
	private AlgorithmFactory validator;

	public ValidatorSelectorRunnable(
			IWorkbenchWindow window, BundleContext bundleContext, File file) {
		this.window = window;
		this.bundleContext = bundleContext;
		this.file = file;
	}

	public AlgorithmFactory getValidator() {
		return this.validator;
	}

	public void run() {
		String fileExtension =
			FileUtilities.getFileExtension(this.file).toLowerCase().substring(1);

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