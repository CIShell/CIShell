package org.cishell.reference.gui.persistence.view;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmExecutionException;
import org.cishell.framework.data.Data;
import org.cishell.reference.gui.persistence.view.core.FileViewer;
import org.cishell.service.conversion.ConversionException;
import org.cishell.service.conversion.DataConversionService;

public class FileView implements Algorithm {
	private Data[] dataToView;
	private CIShellContext ciShellContext;
	private DataConversionService conversionManager;

	public FileView(Data[] data, Dictionary parameters, CIShellContext context) {
		this.dataToView = data;
		this.ciShellContext = context;

		this.conversionManager = (DataConversionService) context
				.getService(DataConversionService.class.getName());
	}

	
	// Show the contents of a file to the user.
	public Data[] execute() throws AlgorithmExecutionException {
		try {
			for (int ii = 0; ii < this.dataToView.length; ii++) {
				FileViewer.viewDataFile(this.dataToView[ii],
										this.ciShellContext,
										this.conversionManager);
			}
			
			return null;
		} catch (ConversionException conversionException) {
			String exceptionMessage = "Error: Unable to view data:\n    " +
									  conversionException.getMessage();
			
			throw new AlgorithmExecutionException(
				exceptionMessage, conversionException);
		} catch (Throwable thrownObject) {
			throw new AlgorithmExecutionException(thrownObject);
		}
	}
}