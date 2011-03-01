package org.cishell.reference.gui.persistence.load;

import java.util.Dictionary;

import org.cishell.app.service.fileloader.FileLoadException;
import org.cishell.app.service.fileloader.FileLoaderService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class FileLoadAlgorithm implements Algorithm, ProgressTrackable {
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
	}

	public Data[] execute() throws AlgorithmExecutionException {
		try {
			return this.fileLoader.loadFilesFromUserSelection(
				this.bundleContext, this.ciShellContext, this.logger, this.progressMonitor, false);
		} catch (FileLoadException e) {
			throw new AlgorithmExecutionException(e.getMessage(), e);
		}
	}

	public ProgressMonitor getProgressMonitor() {
		return this.progressMonitor;
	}

	public void setProgressMonitor(ProgressMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}
}