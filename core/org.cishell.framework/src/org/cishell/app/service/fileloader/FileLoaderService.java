package org.cishell.app.service.fileloader;

import java.io.File;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public interface FileLoaderService {
	public void registerListener(FileLoadListener listener);
	public void unregisterListener(FileLoadListener listener);

	public Data[] loadFilesFromUserSelection(
			BundleContext bundleContext,
			CIShellContext ciShellContext,
			LogService logger,
			ProgressMonitor progressMonitor) throws FileLoadException;
	public Data[] loadFiles(
			BundleContext bundleContext,
			CIShellContext ciShellContext,
			LogService logger,
			ProgressMonitor progressMonitor,
			File[] files) throws FileLoadException;
	public Data[] loadFile(
			BundleContext bundleContext,
			CIShellContext ciShellContext,
			LogService logger,
			ProgressMonitor progressMonitor,
			File file) throws FileLoadException;
}