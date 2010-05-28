package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.Collection;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;
import org.cishell.utilities.StringUtilities;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class FileLoadAlgorithm implements Algorithm, ProgressTrackable {
	public static final String LOAD_DIRECTORY_PREFERENCE_KEY = "loadDir";

	public static String defaultLoadDirectory;

	private final LogService logger;
	private BundleContext bundleContext;
	private CIShellContext ciShellContext;
	private ProgressMonitor progressMonitor = ProgressMonitor.NULL_MONITOR;

	public FileLoadAlgorithm(
			CIShellContext ciShellContext,
			BundleContext bundleContext,
			Dictionary<String, Object> preferences) {
		this.logger = (LogService)ciShellContext.getService(LogService.class.getName());
		this.ciShellContext = ciShellContext;
		this.bundleContext = bundleContext;

		// This is not done upon declaration because the preference service may not have started.
		if (FileLoadAlgorithm.defaultLoadDirectory == null) {
			FileLoadAlgorithm.defaultLoadDirectory = determineDefaultLoadDirectory(preferences);
		}
	}

	public Data[] execute() throws AlgorithmExecutionException {
		// Prepare to run load dialog in GUI thread.

		IWorkbenchWindow window = getFirstWorkbenchWindow();
		Display display = PlatformUI.getWorkbench().getDisplay();
		FileLoadUserInputRunnable userInputGetter = new FileLoadUserInputRunnable(
			window, this.bundleContext, this.ciShellContext);

		// Run load dialog in GUI thread.

		if (Thread.currentThread() != display.getThread()) {
			display.syncExec(userInputGetter);
		} else {
			userInputGetter.run();
		}

		// Return loaded file data.

		File file = userInputGetter.getFile();
		AlgorithmFactory validator = userInputGetter.getValidator();

		if ((file == null) || (validator == null)) {
			String logMessage = "File loading canceled";
			this.logger.log(LogService.LOG_WARNING, logMessage);

			return null;
		} else {
			return FileValidator.validateFile(file, validator, this.ciShellContext, this.logger);
		}
	}

	public ProgressMonitor getProgressMonitor() {
		return this.progressMonitor;
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}

	private static String determineDefaultLoadDirectory(Dictionary<String, Object> preferences) {
		return StringUtilities.emptyStringIfNull(preferences.get(LOAD_DIRECTORY_PREFERENCE_KEY));
	}

	private static IWorkbenchWindow getFirstWorkbenchWindow() throws AlgorithmExecutionException {
		final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();

		if (windows.length == 0) {
			throw new AlgorithmExecutionException(
				"Cannot obtain workbench window needed to open dialog.");
		} else {
			return windows[0];
		}
	}
}