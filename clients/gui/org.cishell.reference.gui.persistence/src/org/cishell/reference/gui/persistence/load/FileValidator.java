package org.cishell.reference.gui.persistence.load;

import java.io.File;
import java.util.Hashtable;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.algorithm.ProgressMonitor;
import org.cishell.framework.algorithm.ProgressTrackable;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.osgi.service.log.LogService;

public final class FileValidator {
	public static Data[] validateFile(
			File file,
			AlgorithmFactory validator,
			ProgressMonitor progressMonitor,
			CIShellContext ciShellContext,
			LogService logger) throws AlgorithmExecutionException {
		Data[] validationData =
			new Data[] { new BasicData(file.getPath(), String.class.getName()) };
		Algorithm algorithm = validator.createAlgorithm(
			validationData, new Hashtable<String, Object>(), ciShellContext);

		if ((progressMonitor != null) && (algorithm instanceof ProgressTrackable)) {
			ProgressTrackable progressTrackable = (ProgressTrackable)algorithm;
			progressTrackable.setProgressMonitor(progressMonitor);
		}

		Data[] validatedData = algorithm.execute();

		if (validatedData != null) {
			logger.log(LogService.LOG_INFO, "Loaded: " + file.getPath());
		}

		return validatedData;
	}
} 