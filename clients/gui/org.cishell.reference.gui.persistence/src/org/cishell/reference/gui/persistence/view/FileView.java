package org.cishell.reference.gui.persistence.view;

import java.util.Dictionary;

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
			Data[] data, Dictionary parameters, CIShellContext context) {
		this.dataToView = data;
		this.ciShellContext = context;

		this.conversionManager = (DataConversionService)context.getService(
			DataConversionService.class.getName());
		this.logger = (LogService)context.getService(LogService.class.getName());
	}

	public Data[] execute() throws AlgorithmExecutionException {
		for (int ii = 0; ii < this.dataToView.length; ii++) {
			try {
				FileViewer.viewDataFile(this.dataToView[ii],
										this.ciShellContext,
										this.conversionManager,
										this.logger);
			} catch (FileViewingException fileViewingException) {
				String logMessage =
    				"Error: Unable to view data \"" +
    				this.dataToView[ii].getMetadata().get(DataProperty.LABEL) +
    				"\".";
    			
    			this.logger.log(LogService.LOG_ERROR, logMessage);
			}
		}
		
		return new Data[0];
	}
}