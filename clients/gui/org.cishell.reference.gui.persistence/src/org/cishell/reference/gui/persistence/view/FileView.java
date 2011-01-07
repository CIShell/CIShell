package org.cishell.reference.gui.persistence.view;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.framework.data.DataProperty;
import org.cishell.reference.gui.persistence.view.core.FileViewer;
import org.cishell.reference.gui.persistence.view.core.exceptiontypes.FileViewingException;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.service.log.LogService;

public class FileView implements Algorithm {
	private Data[] dataToView;
	private CIShellContext ciShellContext;
	private DataConversionService conversionManager;
	private LogService logger;

	public FileView(
			Data[] data,
			CIShellContext ciShellContext,
			DataConversionService conversionManager,
			LogService logger) {
		this.dataToView = data;
		this.ciShellContext = ciShellContext;
		this.conversionManager = conversionManager;
		this.logger = logger;
	}

	public Data[] execute() throws AlgorithmExecutionException {
		for (Data data : this.dataToView) {
			try {
				FileViewer.viewDataFile(
					data, this.ciShellContext, this.conversionManager, this.logger);
			} catch (FileViewingException fileViewingException) {
				String logMessage = String.format(
					"Error: Unable to view data \"%s\".",
					data.getMetadata().get(DataProperty.LABEL));
    			
    			this.logger.log(LogService.LOG_ERROR, logMessage);
			}
		}
		
		return new Data[0];
	}
}