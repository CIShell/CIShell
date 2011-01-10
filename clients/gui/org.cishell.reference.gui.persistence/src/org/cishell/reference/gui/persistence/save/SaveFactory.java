package org.cishell.reference.gui.persistence.save;

import java.util.Dictionary;

import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.cishell.service.conversion.DataConversionService;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 * Create a Save object
 * 
 * TODO: Should also support if we can convert to file, but have
 * no final file:X->file-ext:* converter.
 *
 */
public class SaveFactory implements AlgorithmFactory, ManagedService {	
    public Algorithm createAlgorithm(
    		Data[] data, Dictionary<String, Object> parameters, CIShellContext ciShellContext) {
    	Data inputData = data[0];
    	DataConversionService conversionManager =
    		(DataConversionService) ciShellContext.getService(
    			DataConversionService.class.getName());

        return new Save(inputData, ciShellContext, conversionManager);
    }

    @SuppressWarnings("unchecked")
	public void updated(Dictionary properties) throws ConfigurationException {}
}