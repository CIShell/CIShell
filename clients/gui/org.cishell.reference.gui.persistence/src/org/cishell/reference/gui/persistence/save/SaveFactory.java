package org.cishell.reference.gui.persistence.save;

import java.util.Dictionary;

import org.cishell.app.service.filesaver.FileSaverService;
import org.cishell.framework.CIShellContext;
import org.cishell.framework.algorithm.Algorithm;
import org.cishell.framework.algorithm.AlgorithmFactory;
import org.cishell.framework.data.Data;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.log.LogService;

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
    	// TODO Unpack data?
    	Data inputData = data[0];
    	LogService logger =
    		(LogService) ciShellContext.getService(LogService.class.getName());
    	FileSaverService fileSaver = (FileSaverService) ciShellContext.getService(
    		FileSaverService.class.getName());

        return new Save(inputData, logger, fileSaver);
    }

    @SuppressWarnings("unchecked")
	public void updated(Dictionary properties) throws ConfigurationException {}
}