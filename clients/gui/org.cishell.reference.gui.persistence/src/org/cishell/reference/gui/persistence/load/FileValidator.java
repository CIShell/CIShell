package org.cishell.reference.gui.persistence.load;

import java.io.File;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.BasicData;
import org.cishell.framework.data.Data;
import org.osgi.service.log.LogService;

public final class FileValidator {
	public static Data[] validateFile(
			File file,
			AlgorithmFactory validator,
			CIShellContext ciShellContext,
			LogService logger) throws AlgorithmExecutionException {
		Data[] validationData =
			new Data[] { new BasicData(file.getPath(), String.class.getName()) };
		Data[] validatedData = validator.createAlgorithm(
			validationData, null, ciShellContext).execute();

		if (validatedData != null) {
			logger.log(LogService.LOG_INFO, "Loaded: " + file.getPath());
		}

		return validatedData;
//		} catch (AlgorithmExecutionException e) {
//			String logMessage =
//				"An error occurred while attempting to load your file " +
//				"with the format you chose.";
//			this.logger.log(LogService.LOG_ERROR, logMessage, e);
//			this.thrownException = e;
//		}
	}
} 