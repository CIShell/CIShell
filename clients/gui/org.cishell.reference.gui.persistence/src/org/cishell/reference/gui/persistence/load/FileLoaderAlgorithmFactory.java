package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.data.Data;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

public class FileLoaderAlgorithmFactory implements AlgorithmFactory {
	private BundleContext bundleContext;
	private LogService logger;

	protected void activate(ComponentContext componentContext) {
        this.bundleContext = componentContext.getBundleContext();
        this.logger = (LogService) this.bundleContext.getService(
        	this.bundleContext.getServiceReference(LogService.class.getName()));
    }

	public Algorithm createAlgorithm(
			Data[] data, Dictionary<String, Object> parameters, CIShellContext ciShellContext) {
		File[] filesToLoad = new File[data.length];

		for (int ii = 0; ii < data.length; ii++) {
			filesToLoad[ii] = (File) data[ii].getData();
		}

		return new FileLoaderAlgorithm(
			this.bundleContext,
			filesToLoad,
			ciShellContext,
			this.logger,
			ProgressMonitor.NULL_MONITOR);
	}
}