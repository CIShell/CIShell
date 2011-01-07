package org.cishell.reference.gui.persistence.view;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.service.log.LogService;


public class FileViewFactory implements AlgorithmFactory {
	public Algorithm createAlgorithm(
			Data[] data, Dictionary<String, Object> parameters, CIShellContext ciShellContext) {
		DataConversionService conversionManager =
			(DataConversionService) ciShellContext.getService(
				DataConversionService.class.getName());
		LogService logger = (LogService) ciShellContext.getService(LogService.class.getName());

        return new FileView(data, ciShellContext, conversionManager, logger);
    }
}