package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.data.Data;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class FileLoaderAlgorithm implements Algorithm {
	private BundleContext bundleContext;
	private File[] filesToLoad;
	private CIShellContext ciShellContext;
	private LogService logger;
	private ProgressMonitor progressMonitor;

	public FileLoaderAlgorithm(
			BundleContext bundleContext,
			File[] filesToLoad,
			CIShellContext ciShellContext,
			LogService logger,
			ProgressMonitor progressMonitor) {
		this.bundleContext = bundleContext;
		this.filesToLoad = filesToLoad;
		this.ciShellContext = ciShellContext;
		this.logger = logger;
		this.progressMonitor = progressMonitor;
	}

	public Data[] execute() throws AlgorithmExecutionException {
		IWorkbenchWindow window = getFirstWorkbenchWindow();
		Display display = PlatformUI.getWorkbench().getDisplay();

		if ((this.filesToLoad != null) && (this.filesToLoad.length != 0)) {
			Collection<Data> finalLabeledFileData = new ArrayList<Data>();

			for (File file : this.filesToLoad) {
				try {
					Data[] validatedFileData = validateFile(window, display, file);
					Data[] labeledFileData = labelFileData(file, validatedFileData);

					for (Data data : labeledFileData) {
						finalLabeledFileData.add(data);
					}
				} catch (Throwable e) {
					String format =
						"The chosen file is not compatible with this format.  " +
						"Check that your file is correctly formatted or try another validator.  " +
						"The reason is: %s";
					String logMessage = String.format(format, e.getMessage());
					this.logger.log(LogService.LOG_ERROR, logMessage, e);
				}
			}

			return finalLabeledFileData.toArray(new Data[0]);
		} else {
			return null;
		}
	}

	private IWorkbenchWindow getFirstWorkbenchWindow() throws AlgorithmExecutionException {
		final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();

		if (windows.length == 0) {
			throw new AlgorithmExecutionException(
				"Cannot obtain workbench window needed to open dialog.");
		} else {
			return windows[0];
		}
	}

	private Data[] validateFile(IWorkbenchWindow window, Display display, File file)
			throws AlgorithmExecutionException {
		AlgorithmFactory validator = null;
		validator = getValidatorFromUser(window, display, file);

		if ((file == null) || (validator == null)) {
			String logMessage = "File loading canceled";
			this.logger.log(LogService.LOG_WARNING, logMessage);
		} else {
			try {
				return FileValidator.validateFile(
					file, validator, this.progressMonitor, this.ciShellContext, this.logger);
			} catch (AlgorithmExecutionException e) {
				if ((e.getCause() != null)
						&& (e.getCause() instanceof UnsupportedEncodingException)) {
					String format =
						"This file cannot be loaded; it uses the unsupported character " +
						"encoding %s.";
					String logMessage = String.format(format, e.getCause().getMessage());
					this.logger.log(LogService.LOG_ERROR, logMessage);
				} else {						
					throw e;
				}
			}
		}

		return new Data[0];
	}

	private Data[] labelFileData(File file, Data[] validatedFileData) {
		Data[] labeledFileData =
			PrettyLabeler.relabelWithFileNameHierarchy(validatedFileData, file);

		return labeledFileData;
	}

	private AlgorithmFactory getValidatorFromUser(
			IWorkbenchWindow window, Display display, File file) {
		ValidatorSelectorRunnable validatorSelector =
			new ValidatorSelectorRunnable(window, this.bundleContext, file);

		if (Thread.currentThread() != display.getThread()) {
			display.syncExec(validatorSelector);
		} else {
			validatorSelector.run();
		}

		return validatorSelector.getValidator();
	}
}