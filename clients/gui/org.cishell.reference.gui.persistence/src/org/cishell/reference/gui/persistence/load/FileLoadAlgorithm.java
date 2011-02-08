package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.Dictionary;

import org.cishell.app.service.fileloader.FileLoadException;
import org.cishell.app.service.fileloader.FileLoaderService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
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
	private FileLoaderService fileLoader;
	private BundleContext bundleContext;
	private CIShellContext ciShellContext;
	private ProgressMonitor progressMonitor = ProgressMonitor.NULL_MONITOR;

	public FileLoadAlgorithm(
			CIShellContext ciShellContext,
			BundleContext bundleContext,
			Dictionary<String, Object> preferences) {
		this.logger = (LogService) ciShellContext.getService(LogService.class.getName());
		this.fileLoader =
			(FileLoaderService) ciShellContext.getService(FileLoaderService.class.getName());
		this.ciShellContext = ciShellContext;
		this.bundleContext = bundleContext;

		// This is not done upon declaration because the preference service may not have started.
		if (FileLoadAlgorithm.defaultLoadDirectory == null) {
			FileLoadAlgorithm.defaultLoadDirectory = determineDefaultLoadDirectory(preferences);
		}
	}

	public Data[] execute() throws AlgorithmExecutionException {
		try {
			return this.fileLoader.loadFilesFromUserSelection(
				this.bundleContext, this.ciShellContext, this.logger, this.progressMonitor);
		} catch (FileLoadException e) {
			throw new AlgorithmExecutionException(e.getMessage(), e);
		}
//		IWorkbenchWindow window = getFirstWorkbenchWindow();
//		Display display = PlatformUI.getWorkbench().getDisplay();
//		File[] files = getFilesToLoadFromUser(window, display);
//
//		if (files != null) {
////			try {
//				return new FileLoaderAlgorithm(
//					this.bundleContext,
//					files,
//					this.ciShellContext,
//					this.logger,
//					this.progressMonitor).execute();
////			} catch (Throwable e) {
////				String format =
////					"The chosen file is not compatible with this format.  " +
////					"Check that your file is correctly formatted or try another validator.  " +
////					"The reason is: %s";
////				String logMessage = String.format(format, e.getMessage());
////				this.logger.log(LogService.LOG_ERROR, logMessage, e);
////
////				return null;
////			}
//		} else {
//			return null;
//		}
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

	private IWorkbenchWindow getFirstWorkbenchWindow() throws AlgorithmExecutionException {
		final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();

		if (windows.length == 0) {
			throw new AlgorithmExecutionException(
				"Cannot obtain workbench window needed to open dialog.");
		} else {
			return windows[0];
		}
	}

	private File[] getFilesToLoadFromUser(IWorkbenchWindow window, Display display) {
		FileSelectorRunnable fileSelector = new FileSelectorRunnable(window);

		if (Thread.currentThread() != display.getThread()) {
			display.syncExec(fileSelector);
		} else {
			fileSelector.run();
		}

		return fileSelector.getFiles();
	}
}